package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.contracts.endUserContracts.EndUserContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.SuspendOrActiveOrSwithAccountType;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class EndUserSuspendOrActiveOrSwithAccountTypeFlowInitiator  extends FlowLogic<String> {

    private SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType;

    private final ProgressTracker progressTracker = new ProgressTracker();

    public EndUserSuspendOrActiveOrSwithAccountTypeFlowInitiator(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        this.suspendOrActiveOrSwithAccountType = suspendOrActiveOrSwithAccountType;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }


    @Override
    @Suspendable
    public String call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        EndUser endUser = new EndUser();
        Party endUserBankNode = null, endUserNode = null;
        List<EndUserAccount> endUserAccounts = new ArrayList<>();
        EndUserAccount endUserAccount = new EndUserAccount();
        System.out.println(suspendOrActiveOrSwithAccountType);
        //chercher le compte dans le ledger
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState = (EndUserState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState.getEndUser() != null && endUserState.getEndUser().getEndUserData() != null &&
                            endUserState.getEndUser().getEndUserAccounts() != null) {
                        for (int j = 0; j < endUserState.getEndUser().getEndUserAccounts().size(); j++) {
                            if (endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId()
                                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId()) &&
                                    endUserState.getEndUser().getEndUserAccounts().get(j).getBankIndcation()
                                            .equals(suspendOrActiveOrSwithAccountType.getBankAccountId())) {
                                endUser = endUserState.getEndUser();
                                System.out.println(endUser);
                                endUserBankNode = endUserState.getBankNodeWhoAddUser();
                                endUserNode = endUserState.getEndUserNode();
                                endUserAccount = endUser.getEndUserAccounts().get(j);
                                System.out.println(endUserAccount);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        //s'il n'existe pas alors retourner null
        if(endUser == null){
            return null;
        }
        //S'il existe, alors le mettre à jours
        for(int i = 0; i < endUser.getEndUserAccounts().size(); i++ ){
            if (endUser.getEndUserAccounts().get(i).getAccountId()
                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId()) &&
                    endUser.getEndUserAccounts().get(i).getBankIndcation()
                            .equals(suspendOrActiveOrSwithAccountType.getBankAccountId())){
                endUserAccount.setSuspend(suspendOrActiveOrSwithAccountType.isSuspendFlag());
                endUserAccount.setCRUDDate(new Date());
                System.out.println(endUserAccount);
                if (suspendOrActiveOrSwithAccountType.getNewAccountType() != null){
                    endUserAccount.setAccountType(suspendOrActiveOrSwithAccountType.getNewAccountType());
                }
                endUserAccounts.add(endUserAccount);
            }else{
                endUserAccounts.add(endUser.getEndUserAccounts().get(i));
            }
        }
        endUser.setEndUserAccounts(endUserAccounts);
        final EndUserState newEndUserState =
                new EndUserState(endUser,endUserBankNode, endUserNode,new UniqueIdentifier());

        final Command<EndUserContract.Create> txCommand = new Command<>(
                new EndUserContract.Create(), Arrays.asList(endUserBankNode.getOwningKey(), endUserNode.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newEndUserState);
        builder.addCommand(txCommand);
        builder.verify(getServiceHub());
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        FlowSession otherPartySession = initiateFlow(endUserNode);
        //collecter toute les signatures
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
        // finaliser la transaction
        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();

        return endUser.getEndUserData().getEmail();

    }

}
