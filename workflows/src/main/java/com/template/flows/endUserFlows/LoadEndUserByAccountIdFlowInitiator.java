package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.model.AccountIdAndPassword;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class LoadEndUserByAccountIdFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private String endUserAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public LoadEndUserByAccountIdFlowInitiator(String endUserAccountId) {
        this.endUserAccountId = endUserAccountId;
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
        for (int i = 0; i < signedTransactions.size(); i++){
            try {
                if (signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState1 = (EndUserState) signedTransactions.get(i).
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (endUserState1 != null && endUserState1.getEndUser() != null &&
                            endUserState1.getEndUser().getEndUserData() != null &&
                            endUserState1.getEndUser().getEndUserAccounts() != null) {

                        for (int j = 0; j < endUserState1.getEndUser().getEndUserAccounts().size(); j++) {
                            if (endUserState1.getEndUser().getEndUserAccounts().get(j).getAccountId().equals(endUserAccountId)) {
                                accountIdAndPassword.setCompteId(endUserAccountId);
                                accountIdAndPassword.setPassword(endUserState1.getEndUser().getEndUserAccounts().get(j).getPassword());
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
