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

    public CurrentAmountCBDCReadFlowInitiator() {
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public Double call() throws FlowException {
        List<StateAndRef<TransactionInterBanksStates>> allTxInterbank =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        List<Double> filtered = new ArrayList<>();
        for (StateAndRef<TransactionInterBanksStates> txInterbank : allTxInterbank){
            TransactionInterBanksStates states = txInterbank.getState().getData();
            if (states.getTransactionInterBank().getAccountSender().equals(states.getTransactionInterBank().getAccountReceiver())){
                filtered.add(txInterbank.getState().getData().getTransactionInterBank().getCurrentAmount());
            }
        }
        return filtered.get(filtered.size()-1);

    }
}

