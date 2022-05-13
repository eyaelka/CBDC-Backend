package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.endUserContracts.EndUserContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.EndUserAccountInfo;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class EndUserCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final EndUserAccountInfo endUserAccountInfo;

    private final ProgressTracker progressTracker = new ProgressTracker();

    public EndUserCreatorFlowInitiator(EndUserAccountInfo endUserAccountInfo) {
        this.endUserAccountInfo = endUserAccountInfo;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

    //verfier si end user est dejà enregistré
        EndUserState endUserState = null;
        int index =0;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState1 = (EndUserState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState1.getEndUser() != null && endUserState1.getEndUser().getEndUserData() != null &&
                            endUserState1.getEndUser().getEndUserData().getCin().equals(endUserAccountInfo.getEndUserData().getCin())) {
                        index = 1;
                        endUserState = endUserState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }

        //s'il n'existe pas alors l'enregistré
        if (index == 0){

            //créer le mot de passe
            AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly();
            if (accountIdAndPassword==null)
                return null;
            Party bankNodeWhoAddUser = getOurIdentity();
            //recuration du node receiver
            Party endUserNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("endUserNode","Tunisie","TN"));

            EndUser endUser = new EndUser();
            endUser.setEndUserData(endUserAccountInfo.getEndUserData());
            EndUserAccount endUserAccount = new EndUserAccount();
            endUserAccount.setAccountType(endUserAccountInfo.getAccountType());
            endUserAccount.setSuspend(endUserAccountInfo.isSuspend());
            endUserAccount.setBankIndcation(endUserAccountInfo.getBankIndcation());
            endUserAccount.setAccountId(accountIdAndPassword.getCompteId());
            endUserAccount.setPassword(accountIdAndPassword.getPassword());
            endUser.getEndUserAccounts().add(endUserAccount);
            EndUserState endUserState1 = new EndUserState(endUser,bankNodeWhoAddUser,endUserNode, new UniqueIdentifier());
            final Command<EndUserContract.Create> txCommand = new Command<>(new EndUserContract.Create(),
                    Arrays.asList(bankNodeWhoAddUser.getOwningKey(),endUserNode.getOwningKey()));

            final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
            builder.addOutputState(endUserState1);
            builder.addCommand(txCommand);
            // Verifier si la transaction est valide.
            builder.verify(getServiceHub());
            // signer la transaction par owner kypaire.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
            //initier le canal de communication entre initiateur et le recepteur de la TX
            FlowSession otherPartySession = initiateFlow(endUserNode);
            //collecter toute les signatures
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
            // finaliser la transaction
            subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();
            return accountIdAndPassword;
        }
        String accountId =  endUserState.getEndUser().getEndUserAccounts().get(
                endUserState.getEndUser().getEndUserAccounts().size()-1).getAccountId();
        String pw =  endUserState.getEndUser().getEndUserAccounts().get(
                endUserState.getEndUser().getEndUserAccounts().size()-1).getPassword();
        return new AccountIdAndPassword(accountId,pw);

    }

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("us").generateAccountIdAdnPassword();

        int index;
        while ((index = containsAccountIdOrPassword(accountIdAndPassword)) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("me").generateAccountIdAdnPassword();
        }
        if (index == -1)
            return null;
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(AccountIdAndPassword accountIdAndPassword){

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState = (EndUserState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState.getEndUser() != null && endUserState.getEndUser().getEndUserData() != null) {
                        for (int i =0; i < endUserState.getEndUser().getEndUserAccounts().size(); i++){
                            if (endUserState.getEndUser().getEndUserAccounts().get(i).getAccountId().equals(accountIdAndPassword.getCompteId()) &&
                                    endUserState.getEndUser().getEndUserAccounts().get(i).getBankIndcation().equals(endUserAccountInfo.getBankIndcation()) &&
                                    endUserState.getEndUser().getEndUserAccounts().get(i).getPassword().equals(accountIdAndPassword.getPassword())){
                                return 1;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

}
