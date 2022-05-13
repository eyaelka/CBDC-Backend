package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.endUser.EndUserData;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class EndUserReaderFlowInitiator extends FlowLogic<EndUserData> {

    private final String endUserAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public EndUserReaderFlowInitiator(String endUserAccountId) {
        this.endUserAccountId = endUserAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public EndUserData call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        EndUserData endUserData = new EndUserData();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof EndUserState) {
                    EndUserState endUserState = (EndUserState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);

                    if (endUserState.getEndUser() != null &&
                            endUserState.getEndUser().getEndUserData() != null &&
                            endUserState.getEndUser().getEndUserAccounts() != null) {
                        for (int j = 0; j < endUserState.getEndUser().getEndUserAccounts().size(); j++) {
                            System.out.println(endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId().equals(endUserAccountId));
                            System.out.println(endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId());
                            System.out.println(endUserAccountId);
                            if (endUserState.getEndUser().getEndUserAccounts().get(j).getAccountId().equals(endUserAccountId)) {
                                endUserData = endUserState.getEndUser().getEndUserData();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return endUserData;
    }
}
