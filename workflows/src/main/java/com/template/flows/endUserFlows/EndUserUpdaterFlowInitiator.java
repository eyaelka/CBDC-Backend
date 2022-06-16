package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.endUserContracts.EndUserContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserData;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class EndUserUpdaterFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final EndUserData endUserData;
    private final String endUserAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public EndUserUpdaterFlowInitiator(EndUserData endUserData, String endUserAccountId) {
        this.endUserData = endUserData;
        this.endUserAccountId = endUserAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }


    @Suspendable
    @Override
    public AccountIdAndPassword call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        if (signedTransactions == null){
            return null;
        }

        EndUser endUser = new EndUser(new EndUserData(),new ArrayList<>());
        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPassword();
        Party addedBy = null, owner = null;
        //chercher le compte dans le ledger
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState = (EndUserState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState.getEndUser() != null && endUserState.getEndUser().getEndUserData() != null &&
                            endUserState.getEndUser().getEndUserAccounts() != null) {
                        for (int j = 0; j < endUserState.getEndUser().getEndUserAccounts().size(); j++) {
                            if (endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId().equals(endUserAccountId)) {
                                endUser = endUserState.getEndUser();

                                addedBy = getOurIdentity();
                                owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("endUserNode","Tunisie","TN"));

                                accountIdAndPassword.setCompteId(endUserAccountId);
                                accountIdAndPassword.setPassword(endUserState.getEndUser().getEndUserAccounts().get(j).getPassword());
                                System.out.println("accountIdAndPassword\n"+accountIdAndPassword);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        //s'il n'existe pas alors retourner null
        if(endUser == null || endUser.getEndUserData() == null || endUser.getEndUserData().getCin() == null){
            return null;
        }
        //S'il existe, alors le mettre à jours
        endUser.setEndUserData(endUserData);
        final EndUserState newUserState = new EndUserState(endUser,addedBy,owner,new UniqueIdentifier());

        final Command<EndUserContract.Create> txCommand = new Command<>(new EndUserContract.Create(), Arrays.asList(addedBy.getOwningKey(),owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newUserState);
        builder.addCommand(txCommand);
        builder.verify(getServiceHub());
        // signer la transaction par owner kypaire.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        FlowSession otherPartySession = initiateFlow(owner);
        //collecter toute les signatures
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
        // finaliser la transaction
        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
        return accountIdAndPassword;




//        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
//        if (signedTransactions == null){
//            return null;
//        }
//        EndUser endUser = new EndUser();
//        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPassword();
//        Party endUserBankNode = null, endUserNode = null;
//        //chercher le compte dans le ledger
//        for (SignedTransaction signedTransaction : signedTransactions) {
//            System.out.println(endUserAccountId);
//            try {
//                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
//                        instanceof EndUserState) {
//
//                    EndUserState endUserState = (EndUserState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
//                    System.out.println(endUserState);
//                    if (endUserState.getEndUser() != null && endUserState.getEndUser().getEndUserData() != null && endUserState.getEndUser().getEndUserAccounts() != null) {
//                        for (int j = 0; j < endUserState.getEndUser().getEndUserAccounts().size(); j++) {
//                            if (endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId().equals(endUserAccountId)) {
//
//                                endUser = endUserState.getEndUser();
//                                endUserBankNode = endUserState.getBankNodeWhoAddUser();
//                                endUserNode = endUserState.getEndUserNode();
//                                accountIdAndPassword.setCompteId(endUserAccountId);
//                                accountIdAndPassword.setPassword(endUserState.getEndUser().getEndUserAccounts().get(j).getPassword());
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                return null;
//            }
//        }
//        //s'il n'existe pas alors retourner null
//        if(endUser == null){
//            return null;
//        }
//        //S'il existe, alors le mettre à jours
//        endUser.setEndUserData(endUserData);
//        final EndUserState newEndUserState =
//                new EndUserState(endUser,endUserBankNode,endUserNode,new UniqueIdentifier());
//
//        final Command<EndUserContract.Create> txCommand = new Command<>(
//                new EndUserContract.Create(), Arrays.asList(endUserBankNode.getOwningKey(),endUserNode.getOwningKey()));
//        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
//        builder.addOutputState(newEndUserState);
//        builder.addCommand(txCommand);
//        builder.verify(getServiceHub());
//        // signer la transaction par owner kypaire.
//        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
//        //initier le canal de communication entre initiateur et le recepteur de la TX
//        FlowSession otherPartySession = initiateFlow(endUserNode);
//        //collecter toute les signatures
//        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
//        // finaliser la transaction
//        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();
//        return accountIdAndPassword;
    }
}
