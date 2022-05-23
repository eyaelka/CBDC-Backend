package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
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
public class RegulateurTransactionLocaleReadFlowInitiator extends FlowLogic<RegulateurTransactionLocale> {

    private String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurTransactionLocaleReadFlowInitiator(String pays) {
        this.pays =pays;
    }

    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public RegulateurTransactionLocale call() throws FlowException {
        List<StateAndRef<RegulateurTransactionLocaleStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();

        AtomicReference<RegulateurTransactionLocale> regulateurTransactionLocaleToReturn = null;
        stateAndRefList.forEach(regulateurTransactionLocaleStateAndRef ->{
            RegulateurTransactionLocale regulateurTransactionLocale =
                    regulateurTransactionLocaleStateAndRef.getState().getData().getRegulateurTransactionLocale();
            if (regulateurTransactionLocale.getPays().equals(pays)){
                regulateurTransactionLocaleToReturn.set(regulateurTransactionLocale);
            }
        });
        return regulateurTransactionLocaleToReturn.get();
    }
}

