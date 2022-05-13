package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.commercialBankContracts.CommercialBankContract;
import com.template.contracts.merchantContracts.MerchantContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.NewCommercialBankAccount;
import com.template.flows.model.NewMerchantAccount;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.commercialBank.CommercialBankData;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.model.merchant.MerchantData;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.merchantStates.MerchantState;
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

public class CommercialBankOtherAccountCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final NewCommercialBankAccount newCommercialBankAccount ;

    private final ProgressTracker progressTracker = new ProgressTracker();

    public CommercialBankOtherAccountCreatorFlowInitiator(NewCommercialBankAccount newCommercialBankAccount) {
        this.newCommercialBankAccount = newCommercialBankAccount;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        CommercialBankState commercialBankState = getLastCommercialBankModification(newCommercialBankAccount.getNom());
        if (commercialBankState == null){
            return null;
        }

        //extraire les données de la state
        CommercialBank oldCommercialBank = commercialBankState.getCommercialBank();
        List<CommercialBankAccount> oldCommercialBankAccounts = oldCommercialBank.getCommercialBankAccounts();
        CommercialBankData commercialBankData = oldCommercialBank.getCommercialBankData();

        //generate accountId and password

        AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly(commercialBankState);

        CommercialBankAccount commercialBankAccount = new CommercialBankAccount();
        commercialBankAccount.setAccountType(newCommercialBankAccount.getAccountType());
        commercialBankAccount.setSuspend(newCommercialBankAccount.isSuspend());
        commercialBankAccount.setAccountId(accountIdAndPassword.getCompteId());
        commercialBankAccount.setPassword(accountIdAndPassword.getPassword());

        List<CommercialBankAccount> newCommercialBankAccounts = new ArrayList<>(oldCommercialBankAccounts);

        newCommercialBankAccounts.add(commercialBankAccount);
        CommercialBank newCommercialBank = new CommercialBank();
        newCommercialBank.setCommercialBankData(commercialBankData);
        newCommercialBank.setCommercialBankAccounts(newCommercialBankAccounts);
        //create new state
        //Retrieve the notary identity from the network map
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        //Commercial bank who will add the merchant
        Party addedBy = getOurIdentity();
        //The Merchant
        Party owner = commercialBankState.getOwner();

        CommercialBankState newCommercialBankState = new CommercialBankState(newCommercialBank,addedBy,owner,new UniqueIdentifier());
        //build TX
        final Command<CommercialBankContract.Create> txCommand = new Command<>(new CommercialBankContract.Create(),
                Arrays.asList(addedBy.getOwningKey(),owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(newCommercialBankState);
        builder.addCommand(txCommand);
        // Verifier si la transaction est valide.
        builder.verify(getServiceHub());
        // signer la transaction par owner kypaire.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        FlowSession otherPartySession = initiateFlow(owner);
        //collecter toute les signatures
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
        // finaliser la transaction
        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();
        return accountIdAndPassword;
    }

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(CommercialBankState commercialBankState){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("cb").generateAccountIdAdnPassword();

        while (containsAccountIdOrPassword(commercialBankState.getCommercialBank().getCommercialBankAccounts(), accountIdAndPassword) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("cb").generateAccountIdAdnPassword();
        }
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(List<CommercialBankAccount> commercialBankAccounts, AccountIdAndPassword accountIdAndPassword){
        for (CommercialBankAccount commercialBankAccount : commercialBankAccounts) {
            if (commercialBankAccount.getAccountId().equals(accountIdAndPassword.getCompteId()) ||
                            commercialBankAccount.getAccountId().equals(accountIdAndPassword.getPassword())) {
                return 1;
            }
        }
        return 0;
    }

    public CommercialBankState getLastCommercialBankModification(String commercialBankName){
        CommercialBankState commercialBankState = null;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState1 = (CommercialBankState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState1.getCommercialBank() != null && commercialBankState1.getCommercialBank().getCommercialBankData() != null &&
                            commercialBankState1.getCommercialBank().getCommercialBankData().getName().equals(commercialBankName)) {
                        commercialBankState = commercialBankState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return commercialBankState;
    }
}
