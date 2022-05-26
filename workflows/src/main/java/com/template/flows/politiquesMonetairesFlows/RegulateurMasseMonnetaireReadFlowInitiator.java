package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@InitiatingFlow
@StartableByRPC
public class RegulateurMasseMonnetaireReadFlowInitiator  extends FlowLogic<RegulateurMasseMonnetaire> {

    private String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurMasseMonnetaireReadFlowInitiator(String pays) {
        this.pays = pays;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public RegulateurMasseMonnetaire call() throws FlowException {
        List<StateAndRef<RegulateurMasseMonnetaireStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();

        //AtomicReference<RegulateurMasseMonnetaire> regulateurMasseMonnetaireToReturn = null;

        Stream<StateAndRef<RegulateurMasseMonnetaireStates>> regulateurMasseMonetaireStates =
                stateAndRefList.stream().filter(
                        regulateurDeviseState1 -> regulateurDeviseState1.
                                getState().getData().getRegulateurMasseMonnetaire().getPays().equals(pays));
        System.out.println(regulateurMasseMonetaireStates);

        List<StateAndRef<RegulateurMasseMonnetaireStates>> regulateurMasseMonetaireState =
                regulateurMasseMonetaireStates.collect(Collectors.toList());
        System.out.println(regulateurMasseMonetaireState);
           return regulateurMasseMonetaireState.get(0).getState().getData().getRegulateurMasseMonnetaire();




//        stateAndRefList.forEach(regulateurMasseMonnetaireStatesStateAndRef -> {
//            RegulateurMasseMonnetaire regulateurMasseMonnetaire =
//                    regulateurMasseMonnetaireStatesStateAndRef.getState().getData().getRegulateurMasseMonnetaire();
//            if (regulateurMasseMonnetaire.getPays().equals(pays)){
//                regulateurMasseMonnetaireToReturn.set(regulateurMasseMonnetaire);
//            }
//        });
//        return regulateurMasseMonnetaireToReturn.get();
//    }
    }
}

