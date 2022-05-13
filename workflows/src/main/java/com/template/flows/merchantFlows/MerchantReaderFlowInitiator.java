package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.merchant.MerchantData;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class MerchantReaderFlowInitiator extends FlowLogic<MerchantData> {

    private final String merchantAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public MerchantReaderFlowInitiator(String merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public MerchantData call() throws FlowException {
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        MerchantData merchantData = new MerchantData();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof MerchantState) {
                    MerchantState merchantState = (MerchantState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState.getMerchant() != null && merchantState.getMerchant().getMerchantData() != null &&
                            merchantState.getMerchant().getMerchantAccounts() != null) {

                        for (int j = 0; j < merchantState.getMerchant().getMerchantAccounts().size(); j++) {
                            if (merchantState.getMerchant().getMerchantAccounts().get(j).getAccountId().equals(merchantAccountId)) {
                                merchantData = merchantState.getMerchant().getMerchantData();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return merchantData;
    }
}
