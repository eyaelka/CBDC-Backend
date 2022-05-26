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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@InitiatingFlow
@StartableByRPC
public class RegulateurDeviseReadFlowInitiator extends FlowLogic<RegulateurDevise> {

    private final String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurDeviseReadFlowInitiator(String pays) {
        System.out.println("Constructeur");

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
        //AtomicReference<RegulateurDevise> regulateurDeviseStatesToReturn = null;
        ///////////////////

        Stream<StateAndRef<RegulateurDeviseStates>> regulateurDeviseStates =
                stateAndRefList.stream().filter(
                        regulateurDeviseState1 -> regulateurDeviseState1.
                                getState().getData().getRegulateurDevise().getPays().equals(pays));
        System.out.println(regulateurDeviseStates);

        List<StateAndRef<RegulateurDeviseStates>> regulateurDeviseState =
                regulateurDeviseStates.collect(Collectors.toList());
        System.out.println(regulateurDeviseState);
        return  regulateurDeviseState.get(0).getState().getData().getRegulateurDevise();

        //////////////////////
//        stateAndRefList.forEach(deviseStatesStateAndRef -> {
//            RegulateurDevise regulateurDevise =
//                    deviseStatesStateAndRef.getState().getData().getRegulateurDevise();
//            System.out.println(regulateurDevise);
//            if (regulateurDevise.getPays().equals(pays)){
//                System.out.println("je suis dans if");
//                regulateurDeviseStatesToReturn.set(regulateurDevise);
//                System.out.println(regulateurDeviseStatesToReturn.get());
//            }
//        });
//        System.out.println(regulateurDeviseStatesToReturn.get());
//        return regulateurDeviseStatesToReturn.get();
    }
}



