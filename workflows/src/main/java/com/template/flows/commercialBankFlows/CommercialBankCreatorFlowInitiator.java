package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.commercialBankContracts.CommercialBankContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.CommercialBankAccountInfo;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.states.commercialBankStates.CommercialBankState;
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
public class CommercialBankCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final CommercialBankAccountInfo commercialBankAccountInfo;
    //The progress tracker indicate the progress of the flow to the observers
    private final ProgressTracker progressTracker = new ProgressTracker();


    public CommercialBankCreatorFlowInitiator(CommercialBankAccountInfo commercialBankAccountInfo) {
        this.commercialBankAccountInfo = commercialBankAccountInfo;

    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public AccountIdAndPassword call() throws FlowException {

        //verfier si end user est dejà enregistré
        CommercialBankState commercialBankState = null;
        int index =0;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState1 = (CommercialBankState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState1.getCommercialBank() != null && commercialBankState1.getCommercialBank().getCommercialBankData() != null &&
                            commercialBankState1.getCommercialBank().getCommercialBankData().getName().
                                    equals(commercialBankAccountInfo.getCommercialBankData().getName())) {
                        index = 1;
                        commercialBankState = commercialBankState1;
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
            //Central Bank who will add the commercial bank
            Party addedBy = getOurIdentity();
            System.out.println(addedBy);
            //The Merchant
            Party owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("PartyB","New York","US"));
            //Retrieve the notary identity from the network map
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            CommercialBank commercialBank = new CommercialBank();
            commercialBank.setCommercialBankData(commercialBankAccountInfo.getCommercialBankData());
            CommercialBankAccount commercialBankAccount = new CommercialBankAccount();
            commercialBankAccount.setAccountType(commercialBankAccountInfo.getAccountType());
            commercialBankAccount.setSuspend(commercialBankAccountInfo.isSuspend());
            commercialBankAccount.setAccountId(accountIdAndPassword.getCompteId());
            commercialBankAccount.setPassword(accountIdAndPassword.getPassword());
            commercialBank.getCommercialBankAccounts().add(commercialBankAccount);

            CommercialBankState commercialBankState1 = new CommercialBankState(commercialBank,addedBy,owner, new UniqueIdentifier());
            final Command<CommercialBankContract.Create> txCommand = new Command<>(new CommercialBankContract.Create(),
                    Arrays.asList(addedBy.getOwningKey(),owner.getOwningKey()));

            final TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addOutputState(commercialBankState1);
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
        String accountId =  commercialBankState.getCommercialBank().getCommercialBankAccounts().get(
                commercialBankState.getCommercialBank().getCommercialBankAccounts().size()-1).getAccountId();
        String pw =  commercialBankState.getCommercialBank().getCommercialBankAccounts().get(
                commercialBankState.getCommercialBank().getCommercialBankAccounts().size()-1).getPassword();
        return new AccountIdAndPassword(accountId,pw);

    }


    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("cb").generateAccountIdAdnPassword();

        int index;
        while ((index = containsAccountIdOrPassword(accountIdAndPassword)) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("cb").generateAccountIdAdnPassword();
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
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState = (CommercialBankState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState.getCommercialBank() != null && commercialBankState.getCommercialBank().getCommercialBankData() != null) {
                        for (int i =0; i < commercialBankState.getCommercialBank().getCommercialBankAccounts().size(); i++){
                            if (commercialBankState.getCommercialBank().getCommercialBankAccounts().get(i).getAccountId().equals(accountIdAndPassword.getCompteId()) &&
                                    commercialBankState.getCommercialBank().getCommercialBankAccounts().get(i).getPassword().equals(accountIdAndPassword.getPassword())){
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
