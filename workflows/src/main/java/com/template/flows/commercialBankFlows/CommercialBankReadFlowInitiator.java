package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.commercialBank.CommercialBankData;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CommercialBankReadFlowInitiator extends FlowLogic<CommercialBankData> {
    private String commercialBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CommercialBankReadFlowInitiator(String commercialBankAccountId) {
        this.commercialBankAccountId = commercialBankAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public CommercialBankData call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        CommercialBankData commercialBankData = new CommercialBankData();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState = (CommercialBankState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState.getCommercialBank() != null && commercialBankState.getCommercialBank().getCommercialBankData() != null &&
                            commercialBankState.getCommercialBank().getCommercialBankAccounts() != null) {

                        for (int j = 0; j < commercialBankState.getCommercialBank().getCommercialBankAccounts().size(); j++) {
                            if (commercialBankState.getCommercialBank().getCommercialBankAccounts().get(j).getAccountId().equals(commercialBankAccountId)) {
                                commercialBankData = commercialBankState.getCommercialBank().getCommercialBankData();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return commercialBankData;

    }
}
