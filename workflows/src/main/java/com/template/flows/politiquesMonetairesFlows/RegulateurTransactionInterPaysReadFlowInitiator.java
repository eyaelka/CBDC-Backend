package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;

import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<StateAndRef<RegulateurTransactionInterPaysStates>> allTxLocal =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();
        List<RegulateurTransactionInterPays> filtered = new ArrayList<>();
        for (StateAndRef<RegulateurTransactionInterPaysStates> regulateurTxInterpays : allTxLocal){
            RegulateurTransactionInterPaysStates states = regulateurTxInterpays.getState().getData();
            if (states.getRegulateurTransactionInterPays().getPaysBanqueCentral().equalsIgnoreCase(pays)){
                filtered.add(regulateurTxInterpays.getState().getData().getRegulateurTransactionInterPays());
            }
        }
        return filtered.get(filtered.size()-1);
    }
}

