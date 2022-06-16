package com.template.flows.transactionsFlow;

import com.template.model.transactions.TransactionInterBanks;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class AllSentRecievedBalanceFlowInitiator extends FlowLogic<List<Double>> {


    String sender;


    public AllSentRecievedBalanceFlowInitiator(String sender) {
        this.sender = sender;
    }


    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public List<Double> call() throws FlowException {
        double totalRecievedBalance = 0;
        double totalSentBalance =0;

        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        if (stateAndRefList == null) {
            return null;
        }
        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null) {

                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(sender) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(sender)) {
                    totalSentBalance += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                }
                else if (!stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(sender) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(sender)) {
                    totalRecievedBalance += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                }
            }
        }
        List<Double> balanceUpdates = new ArrayList<>();


        balanceUpdates.add(new Double(totalSentBalance));
        balanceUpdates.add(new Double(totalRecievedBalance));
        return balanceUpdates;

    }

}
