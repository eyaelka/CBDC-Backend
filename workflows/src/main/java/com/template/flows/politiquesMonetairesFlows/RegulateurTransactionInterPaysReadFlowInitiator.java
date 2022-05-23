package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;

import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@InitiatingFlow
@StartableByRPC
public class RegulateurTransactionInterPaysReadFlowInitiator extends FlowLogic<RegulateurTransactionInterPays> {

    private String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurTransactionInterPaysReadFlowInitiator(String pays) {
        this.pays =pays;
    }

    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public RegulateurTransactionInterPays call() throws FlowException {
        List<StateAndRef<RegulateurTransactionInterPaysStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();
        AtomicReference<RegulateurTransactionInterPays> regulateurTransactionInterPaysToReturn = null;
        stateAndRefList.forEach(regulateurTransactionInterPaysStatesStateAndRef ->{
            RegulateurTransactionInterPays regulateurTransactionInterPays =
                    regulateurTransactionInterPaysStatesStateAndRef.getState().getData().getRegulateurTransactionInterPays();
            if (regulateurTransactionInterPays.getPaysBanqueCentral().equals(pays)){
                regulateurTransactionInterPaysToReturn.set(regulateurTransactionInterPays);
            }
        });
        return regulateurTransactionInterPaysToReturn.get();
    }
}

