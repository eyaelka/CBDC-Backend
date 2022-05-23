package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
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
public class RegulateurDeviseReadFlowInitiator extends FlowLogic<RegulateurDevise> {

    private String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurDeviseReadFlowInitiator(String pays) {
        this.pays =pays;
    }

    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public RegulateurDevise call() throws FlowException {
        List<StateAndRef<RegulateurDeviseStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurDeviseStates.class).getStates();
        AtomicReference<RegulateurDevise> regulateurDeviseStatesToReturn = null;
        stateAndRefList.forEach(deviseStatesStateAndRef -> {
            RegulateurDevise regulateurDevise =
                    deviseStatesStateAndRef.getState().getData().getRegulateurDevise();
            if (regulateurDevise.getPays().equals(pays)){
                regulateurDeviseStatesToReturn.set(regulateurDevise);
            }
        });
        return regulateurDeviseStatesToReturn.get();
    }
}



