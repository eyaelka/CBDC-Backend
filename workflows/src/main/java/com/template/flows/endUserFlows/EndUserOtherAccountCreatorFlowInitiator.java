package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.endUserContracts.EndUserContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.NewEndUserAccount;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
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
public class EndUserOtherAccountCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private NewEndUserAccount newEndUserAccount ;

    private ProgressTracker progressTracker = new ProgressTracker();

    public EndUserOtherAccountCreatorFlowInitiator(NewEndUserAccount newEndUserAccount) {
        this.newEndUserAccount = newEndUserAccount;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        EndUserState endUserState = getLastEndUserModification(newEndUserAccount.getCin());
        if (endUserState == null){
            return null;
        }

        //extraire les données de la state
        EndUser oldEndUser = endUserState.getEndUser();
        List<EndUserAccount> oldEndUserAccounts = oldEndUser.getEndUserAccounts();
        EndUserData endUserData = oldEndUser.getEndUserData();

        //generate accountId and password
        AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly(endUserState, newEndUserAccount.getBankIndcation());

        EndUserAccount endUserAccount = new EndUserAccount();
        endUserAccount.setAccountType(newEndUserAccount.getAccountType());
        endUserAccount.setSuspend(newEndUserAccount.isSuspend());
        endUserAccount.setBankIndcation(newEndUserAccount.getBankIndcation());
        endUserAccount.setAccountId(accountIdAndPassword.getCompteId());
        endUserAccount.setPassword(accountIdAndPassword.getPassword());

        List<EndUserAccount> newEndUserAccounts = new ArrayList<>();
        for (EndUserAccount oldEndUserAccount : oldEndUserAccounts) {
            newEndUserAccounts.add(oldEndUserAccount);
        }
        newEndUserAccounts.add(endUserAccount);
        EndUser newEndUser = new EndUser();
        newEndUser.setEndUserData(endUserData);
        newEndUser.setEndUserAccounts(newEndUserAccounts);
        //create new state
        Party bankNodeWhoAddUser = getOurIdentity();
        //recuration du node receiver
        Party endUserNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("endUserNode","Tunisie","TN"));

        EndUserState newEndUserState = new EndUserState(newEndUser,bankNodeWhoAddUser,endUserNode,new UniqueIdentifier());
        //build TX
        final Command<EndUserContract.Create> txCommand = new Command<>(new EndUserContract.Create(),
                Arrays.asList(bankNodeWhoAddUser.getOwningKey(),endUserNode.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newEndUserState);
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

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(EndUserState endUserState, String bankIndication){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("us").generateAccountIdAdnPassword();

        while (containsAccountIdOrPassword(endUserState.getEndUser().getEndUserAccounts(), accountIdAndPassword,bankIndication) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("us").generateAccountIdAdnPassword();
        }
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(List<EndUserAccount> endUserAccounts, AccountIdAndPassword accountIdAndPassword,String bankIndication){
        for (EndUserAccount endUserAccount : endUserAccounts) {
            if (endUserAccount.getBankIndcation().equals(bankIndication) &&
                    (endUserAccount.getAccountId().equals(accountIdAndPassword.getCompteId()) ||
                            endUserAccount.getAccountId().equals(accountIdAndPassword.getPassword()))) {
                return 1;
            }
        }
        return 0;
    }

    public EndUserState getLastEndUserModification(String cin){
        EndUserState endUserState = null;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (int i = 0; i < signedTransactions.size(); i++){
            try {
                if (signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState1 = (EndUserState) signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState1 != null && endUserState1.getEndUser() != null &&
                            endUserState1.getEndUser().getEndUserData() != null &&
                            endUserState1.getEndUser().getEndUserData().getCin().equals(cin)) {
                        endUserState = endUserState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return endUserState;
    }
}
