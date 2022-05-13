package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.model.AccountIdAndPassword;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class LoadCommercialBankByAccountIdFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final String commercialalBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public LoadCommercialBankByAccountIdFlowInitiator(String commercialalBankAccountId) {
        this.commercialalBankAccountId = commercialalBankAccountId;
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
                        instanceof CommercialBankState) {
                    CommercialBankState commercialBankState = (CommercialBankState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (commercialBankState.getCommercialBank() != null &&
                            commercialBankState.getCommercialBank().getCommercialBankData() != null &&
                            commercialBankState.getCommercialBank().getCommercialBankAccounts() != null) {

                        for (int j = 0; j < commercialBankState.getCommercialBank().getCommercialBankAccounts().size(); j++) {
                            if (commercialBankState.getCommercialBank().getCommercialBankAccounts().get(j).getAccountId().equals(commercialalBankAccountId)) {
                                accountIdAndPassword.setCompteId(commercialalBankAccountId);
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
        return accountIdAndPassword;
    }
}
