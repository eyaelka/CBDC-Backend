package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.merchantContracts.MerchantContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.MerchantAccountInfo;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.states.merchantStates.MerchantState;
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
public class MerchantCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {
    private final MerchantAccountInfo merchantAccountInfo;
    //The progress tracker indicate the progress of the flow to the observers
    private final ProgressTracker progressTracker = new ProgressTracker();

    public MerchantCreatorFlowInitiator(MerchantAccountInfo merchantAccountInfo) {
        this.merchantAccountInfo = merchantAccountInfo;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public AccountIdAndPassword call() throws FlowException {

        //verfier si merchant est dejà enregistré
        MerchantState merchantState = null;
        int index =0;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof MerchantState) {
                    MerchantState merchantState1 = (MerchantState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState1.getMerchant() != null && merchantState1.getMerchant().getMerchantData() != null &&
                            merchantState1.getMerchant().getMerchantData().getAgreement().equals(merchantAccountInfo.getMerchantData().getAgreement())) {
                        index = 1;
                        merchantState = merchantState1;
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
            //Retrieve the notary identity from the network map
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            //Commercial bank who will add the merchant
            Party addedBy = getOurIdentity();
            //The Merchant
            Party owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("merchant","Tunisie","TN"));

            Merchant merchant = new Merchant();
            merchant.setMerchantData(merchantAccountInfo.getMerchantData());
            MerchantAccount merchantAccount = new MerchantAccount();
            merchantAccount.setAccountType(merchantAccountInfo.getAccountType());
            merchantAccount.setSuspend(merchantAccountInfo.isSuspend());
            merchantAccount.setBankIndcation(merchantAccountInfo.getBankIndcation());
            merchantAccount.setAccountId(accountIdAndPassword.getCompteId());
            merchantAccount.setPassword(accountIdAndPassword.getPassword());
            merchant.getMerchantAccounts().add(merchantAccount);

            //Create the transaction components
            MerchantState merchantStoreState = new MerchantState(merchant, addedBy, owner, new UniqueIdentifier());
            Command<MerchantContract.Create> command = new Command<>(new MerchantContract.Create(), Arrays.asList(addedBy.getOwningKey(), owner.getOwningKey()));

            //Create a transaction builder and add the components
            TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addOutputState(merchantStoreState);
            builder.addCommand(command);
            //Verify if the transaction is valid
            builder.verify(getServiceHub());

            //Signing the transaction
            SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);

            //Creating a session with the other party
            FlowSession otherPartySession = initiateFlow(owner);
            // Obtaining the counterparty's signature.
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.tracker()));
            // finaliser la transaction
            subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();
            return accountIdAndPassword;
        }
        String accountId =  merchantState.getMerchant().getMerchantAccounts().get(
                merchantState.getMerchant().getMerchantAccounts().size()-1).getAccountId();
        String pw =  merchantState.getMerchant().getMerchantAccounts().get(
                merchantState.getMerchant().getMerchantAccounts().size()-1).getPassword();
        return new AccountIdAndPassword(accountId,pw);
    }

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("me").generateAccountIdAdnPassword();

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
                        instanceof MerchantState) {
                    MerchantState merchantState1 = (MerchantState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState1.getMerchant() != null && merchantState1.getMerchant().getMerchantData() != null) {
                        for (int i =0; i < merchantState1.getMerchant().getMerchantAccounts().size(); i++){
                            if (merchantState1.getMerchant().getMerchantAccounts().get(i).getAccountId().equals(accountIdAndPassword.getCompteId()) &&
                                    merchantState1.getMerchant().getMerchantAccounts().get(i).getBankIndcation().equals(merchantAccountInfo.getBankIndcation()) &&
                                    merchantState1.getMerchant().getMerchantAccounts().get(i).getPassword().equals(accountIdAndPassword.getPassword())){
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
