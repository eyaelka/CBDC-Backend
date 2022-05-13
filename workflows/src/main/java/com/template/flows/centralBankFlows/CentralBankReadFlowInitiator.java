package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.centralBank.CentralBankData;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CentralBankReadFlowInitiator extends FlowLogic<CentralBankData> {

    private final String centralBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankReadFlowInitiator(String centralBankAccountId) {
        this.centralBankAccountId = centralBankAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public CentralBankData call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        CentralBankData centralBankData = new CentralBankData();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CentralBankState) {
                    CentralBankState centralBankState1 = (CentralBankState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (centralBankState1.getCentralBank() != null && centralBankState1.getCentralBank().getCentralBankData() != null &&
                            centralBankState1.getCentralBank().getCentralBankAccount() != null) {

                        for (int j = 0; j < centralBankState1.getCentralBank().getCentralBankAccount().size(); j++) {
                            if (centralBankState1.getCentralBank().getCentralBankAccount().get(j).getAccountId().equals(centralBankAccountId)) {
                                centralBankData = centralBankState1.getCentralBank().getCentralBankData();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return centralBankData;
    }
}
