package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.commercialBankContracts.CommercialBankContract;
import com.template.contracts.merchantContracts.MerchantContract;
import com.template.flows.model.CommonTreatment;
import com.template.flows.model.SuspendOrActiveOrSwithAccountType;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.commercialBank.CommercialBankData;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.merchantStates.MerchantState;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
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
public class CommercialBankSuspendOrActiveOrSwithAccountTypeFlowInitiator  extends FlowLogic<String> {

    private SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType;
    private final ProgressTracker progressTracker = new ProgressTracker();
    public CommercialBankSuspendOrActiveOrSwithAccountTypeFlowInitiator(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        this.suspendOrActiveOrSwithAccountType = suspendOrActiveOrSwithAccountType;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }


    @Override
    @Suspendable
    public String call() throws FlowException {

        List<StateAndRef<CommercialBankState>> allTxLocal =
                getServiceHub().getVaultService().queryBy(CommercialBankState.class).getStates();
        if (allTxLocal == null ){
            return null;
        }
        CommercialBank bank = new CommercialBank(new CommercialBankData(), new ArrayList<>());
        CommercialBankAccount account = new CommercialBankAccount();
        for (StateAndRef<CommercialBankState> reg : allTxLocal){
            if (reg.getState() != null && reg.getState().getData() != null && reg.getState().getData().getCommercialBank()!= null){
                CommercialBankState state = reg.getState().getData();
                for ( CommercialBankAccount bankAccount : state.getCommercialBank().getCommercialBankAccounts() ){
                    if (bankAccount.getAccountId().equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId())){
                        bank.setCommercialBankAccounts(state.getCommercialBank().getCommercialBankAccounts());
                        bank.setCommercialBankData(state.getCommercialBank().getCommercialBankData());
                        account  = bankAccount;
                    }
                }

            }

        }
        if (bank == null || bank.getCommercialBankData() == null  ||  bank.getCommercialBankData().getName() == null){
            return null;
        }
        List<CommercialBankAccount> listAccount = new ArrayList<>();
        for ( CommercialBankAccount bankAccount : bank.getCommercialBankAccounts() ){
            if (! bankAccount.getAccountId().equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId())){
                listAccount.add(bankAccount);

            }else{
                account.setSuspend(suspendOrActiveOrSwithAccountType.isSuspendFlag());
                if (suspendOrActiveOrSwithAccountType.getNewAccountType() != null){
                    account.setAccountType(suspendOrActiveOrSwithAccountType.getNewAccountType());
                }
                account.setCRUDDate(new Date());
                listAccount.add(account);
            }
        }
        bank.setCommercialBankAccounts(listAccount);

        Party addedBy = getOurIdentity();
        Party owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("PartyA","London","GB"));


        final CommercialBankState newCommercialBankState = new CommercialBankState(bank,addedBy, owner,new UniqueIdentifier());

        final Command<CommercialBankContract.Create> txCommand = new Command<>(
                new CommercialBankContract.Create(), Arrays.asList(addedBy.getOwningKey(), owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newCommercialBankState);
        builder.addCommand(txCommand);
        builder.verify(getServiceHub());
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        FlowSession otherPartySession = initiateFlow(owner);
        //collecter toute les signatures
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
        // finaliser la transaction
        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));

        return bank.getCommercialBankData().getEmail();


    }



    }



//        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
//        if (signedTransactions == null){
//            return null;
//        }
//
//        CommercialBank commercialBank = new CommercialBank();
//        Party addedBy = null, owner = null;
//        List<CommercialBankAccount> commercialBankAccounts = new ArrayList<>();
//        CommercialBankAccount commercialBankAccount = new CommercialBankAccount();
//
//        //chercher le compte dans le ledger
//        for (SignedTransaction signedTransaction : signedTransactions) {
//            try {
//                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
//                        instanceof CommercialBankState) {
//                    CommercialBankState commercialBankState = (CommercialBankState) signedTransaction.
//                            toLedgerTransaction(getServiceHub()).getOutput(0);
//                    if (commercialBankState.getCommercialBank() != null && commercialBankState.getCommercialBank().getCommercialBankData() != null &&
//                            commercialBankState.getCommercialBank().getCommercialBankAccounts() != null) {
//                        for (int j = 0; j < commercialBankState.getCommercialBank().getCommercialBankAccounts().size(); j++) {
//                            if (commercialBankState.getCommercialBank().getCommercialBankAccounts().get(j).getAccountId()
//                                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId())) {
//                                commercialBank = commercialBankState.getCommercialBank();
//                                addedBy = getOurIdentity();
//                                //The Merchant
//                                owner = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("PartyA","London","GB"));
//
//                                commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
//                                break;
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                return null;
//            }
//        }
//        //s'il n'existe pas alors retourner null
//        if(commercialBank == null){
//            return null;
//        }
//        //S'il existe, alors le mettre à jours
//        for(int i = 0; i < commercialBank.getCommercialBankAccounts().size(); i++ ){
//            if (commercialBank.getCommercialBankAccounts().get(i).getAccountId()
//                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId())){
//                commercialBankAccount.setSuspend(suspendOrActiveOrSwithAccountType.isSuspendFlag());
//                commercialBankAccount.setCRUDDate(new Date());
//                if (suspendOrActiveOrSwithAccountType.getNewAccountType() != null){
//                    commercialBankAccount.setAccountType(suspendOrActiveOrSwithAccountType.getNewAccountType());
//                }
//                commercialBankAccounts.add(commercialBankAccount);
//            }else{
//                commercialBankAccounts.add(commercialBank.getCommercialBankAccounts().get(i));
//            }
//        }
//        commercialBank.setCommercialBankAccounts(commercialBankAccounts);
//        final CommercialBankState newCommercialBankState = new CommercialBankState(commercialBank,addedBy, owner,new UniqueIdentifier());
//
//        final Command<CommercialBankContract.Create> txCommand = new Command<>(
//                new CommercialBankContract.Create(), Arrays.asList(addedBy.getOwningKey(), owner.getOwningKey()));
//        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
//        builder.addOutputState(newCommercialBankState);
//        builder.addCommand(txCommand);
//        builder.verify(getServiceHub());
//        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
//        //initier le canal de communication entre initiateur et le recepteur de la TX
//        FlowSession otherPartySession = initiateFlow(owner);
//        //collecter toute les signatures
//        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
//        // finaliser la transaction
//        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();
//
//        return commercialBank.getCommercialBankData().getEmail();
//
//    }
//
//




