package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.commercialBankContracts.CommercialBankContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankData;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CommercialBankUpdaterFlowInitiator extends FlowLogic<AccountIdAndPassword> {
    private CommercialBankData commercialBankData;
    private String commercialBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CommercialBankUpdaterFlowInitiator(CommercialBankData commercialBankData, String commercialBankAccountId) {
        this.commercialBankData = commercialBankData;
        this.commercialBankAccountId = commercialBankAccountId;
    }

    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }


    @Suspendable
    @Override
    public AccountIdAndPassword call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        CommercialBank commercialBank = new CommercialBank();
        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPassword();
        Party addedBy = null, owner = null;
        //chercher le compte dans le ledger
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState = (CommercialBankState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState.getCommercialBank() != null && commercialBankState.getCommercialBank().getCommercialBankData() != null &&
                            commercialBankState.getCommercialBank().getCommercialBankAccounts() != null) {
                        for (int j = 0; j < commercialBankState.getCommercialBank().getCommercialBankAccounts().size(); j++) {
                            if (commercialBankState.getCommercialBank().getCommercialBankAccounts().get(j).getAccountId().equals(commercialBankAccountId)) {
                                commercialBank = commercialBankState.getCommercialBank();
                                addedBy = commercialBankState.getAddedBy();
                                owner = commercialBankState.getOwner();
                                accountIdAndPassword.setCompteId(commercialBankAccountId);
                                accountIdAndPassword.setPassword(commercialBankState.getCommercialBank().getCommercialBankAccounts().get(j).getPassword());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        //s'il n'existe pas alors retourner null
        if(commercialBank == null){
            return null;
        }
        //S'il existe, alors le mettre à jours
        commercialBank.setCommercialBankData(commercialBankData);
        final CommercialBankState newCommercialState = new CommercialBankState(commercialBank,addedBy,owner,new UniqueIdentifier());

        final Command<CommercialBankContract.Create> txCommand = new Command<>(new CommercialBankContract.Create(), Arrays.asList(addedBy.getOwningKey(),owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newCommercialState);
        builder.addCommand(txCommand);
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
}
