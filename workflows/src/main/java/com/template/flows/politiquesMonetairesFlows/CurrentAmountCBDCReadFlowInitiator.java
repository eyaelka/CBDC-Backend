package com.template.flows.politiquesMonetairesFlows;

import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
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
public class CurrentAmountCBDCReadFlowInitiator extends FlowLogic<Double> {


    String sender;

    public CurrentAmountCBDCReadFlowInitiator(String sender) {
        this.sender = sender;
    }


    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public Double call() throws FlowException {
        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        if (stateAndRefList == null) {
            return null;
        }
        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null) {
                TransactionInterBanks txInterbank = stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(sender) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(sender)) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCurrentAmount();
                }
                if (!stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(sender) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(sender)) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCurrentAmount();
                }
            }
        }

        return null;

    }

}
