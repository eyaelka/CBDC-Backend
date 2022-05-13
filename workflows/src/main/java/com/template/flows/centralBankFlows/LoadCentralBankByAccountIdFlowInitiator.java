package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.model.AccountIdAndPassword;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class LoadCentralBankByAccountIdFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private String centralBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public LoadCentralBankByAccountIdFlowInitiator(String centralBankAccountId) {
        this.centralBankAccountId = centralBankAccountId;
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
                        instanceof CentralBankState) {
                    CentralBankState centralBankState1 = (CentralBankState) signedTransactions.get(i).
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (centralBankState1 != null && centralBankState1.getCentralBank() != null &&
                            centralBankState1.getCentralBank().getCentralBankData() != null &&
                            centralBankState1.getCentralBank().getCentralBankAccount() != null) {

                        for (int j = 0; j < centralBankState1.getCentralBank().getCentralBankAccount().size(); j++) {
                            if (centralBankState1.getCentralBank().getCentralBankAccount().get(j).getAccountId().equals(centralBankAccountId)) {
                                accountIdAndPassword.setCompteId(centralBankAccountId);
                                accountIdAndPassword.setPassword(centralBankState1.getCentralBank().getCentralBankAccount().get(j).getPassword());
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
