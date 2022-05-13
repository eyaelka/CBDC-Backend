package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.merchantContracts.MerchantContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.NewMerchantAccount;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.model.merchant.MerchantData;
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

public class MerchantOtherAccountCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final NewMerchantAccount newMerchantAccount ;

    private final ProgressTracker progressTracker = new ProgressTracker();

    public MerchantOtherAccountCreatorFlowInitiator(NewMerchantAccount newMerchantAccount) {
        this.newMerchantAccount = newMerchantAccount;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        MerchantState merchantState = getLastMerchantModification(newMerchantAccount.getAgreement());
        if (merchantState == null){
            return null;
        }

        //extraire les données de la state
        Merchant oldMerchant = merchantState.getMerchant();
        List<MerchantAccount> oldMerchantAccounts = oldMerchant.getMerchantAccounts();
        MerchantData merchantData = oldMerchant.getMerchantData();

        //generate accountId and password
        AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly(merchantState, newMerchantAccount.getBankIndcation());

        MerchantAccount merchantAccount = new MerchantAccount();
        merchantAccount.setAccountType(newMerchantAccount.getAccountType());
        merchantAccount.setSuspend(newMerchantAccount.isSuspend());
        merchantAccount.setBankIndcation(newMerchantAccount.getBankIndcation());
        merchantAccount.setAccountId(accountIdAndPassword.getCompteId());
        merchantAccount.setPassword(accountIdAndPassword.getPassword());

        List<MerchantAccount> newMerchantAccounts = new ArrayList<>(oldMerchantAccounts);

        newMerchantAccounts.add(merchantAccount);
        Merchant newMarchant = new Merchant();
        newMarchant.setMerchantData(merchantData);
        newMarchant.setMerchantAccounts(newMerchantAccounts);
        //create new state
        //Retrieve the notary identity from the network map
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        //Commercial bank who will add the merchant
        Party addedBy = getOurIdentity();
        //The Merchant
        Party owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("merchant","Tunisie","TN"));

        MerchantState newMarchantState = new MerchantState(newMarchant,addedBy,owner,new UniqueIdentifier());
        //build TX
        final Command<MerchantContract.Create> txCommand = new Command<>(new MerchantContract.Create(),
                Arrays.asList(addedBy.getOwningKey(),owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(newMarchantState);
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

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(MerchantState merchantState, String bankIndication){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("me").generateAccountIdAdnPassword();

        while (containsAccountIdOrPassword(merchantState.getMerchant().getMerchantAccounts(), accountIdAndPassword,bankIndication) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("me").generateAccountIdAdnPassword();
        }
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(List<MerchantAccount> merchantAccounts, AccountIdAndPassword accountIdAndPassword,String bankIndication){
        for (MerchantAccount merchantAccount : merchantAccounts) {
            if (merchantAccount.getBankIndcation().equals(bankIndication) &&
                    (merchantAccount.getAccountId().equals(accountIdAndPassword.getCompteId()) ||
                            merchantAccount.getAccountId().equals(accountIdAndPassword.getPassword()))) {
                return 1;
            }
        }
        return 0;
    }

    public MerchantState getLastMerchantModification(String agreement){
        MerchantState merchantState = null;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof MerchantState) {
                    MerchantState merchantState1 = (MerchantState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState1.getMerchant() != null && merchantState1.getMerchant().getMerchantData() != null &&
                            merchantState1.getMerchant().getMerchantData().getAgreement().equals(agreement)) {
                        merchantState = merchantState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return merchantState;
    }
}

