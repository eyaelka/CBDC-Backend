package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.model.AccountIdAndPassword;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class LoadMerchantByAccountIdFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private String merchantAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public LoadMerchantByAccountIdFlowInitiator(String merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPassword();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof MerchantState) {
                    MerchantState merchantState1 = (MerchantState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState1.getMerchant() != null &&
                            merchantState1.getMerchant().getMerchantData() != null &&
                            merchantState1.getMerchant().getMerchantAccounts() != null) {

                        for (int j = 0; j < merchantState1.getMerchant().getMerchantAccounts().size(); j++) {
                            if (merchantState1.getMerchant().getMerchantAccounts().get(j).getAccountId().equals(merchantAccountId)) {
                                accountIdAndPassword.setCompteId(merchantAccountId);
                                accountIdAndPassword.setPassword(merchantState1.getMerchant().getMerchantAccounts().get(j).getPassword());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return accountIdAndPassword;
    }
}
