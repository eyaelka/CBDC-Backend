package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
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

        List<StateAndRef<RegulateurMasseMonnetaireStates>> allTxLocal =
                getServiceHub().getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
        List<RegulateurMasseMonnetaire> filtered = new ArrayList<>();
        for (StateAndRef<RegulateurMasseMonnetaireStates> regulateurMasseMonetaire : allTxLocal){
            RegulateurMasseMonnetaireStates states = regulateurMasseMonetaire.getState().getData();
            if (states.getRegulateurMasseMonnetaire().getPays().equalsIgnoreCase(pays)){
                filtered.add(regulateurMasseMonetaire.getState().getData().getRegulateurMasseMonnetaire());
            }
        }
        return filtered.get(filtered.size()-1);

    }
}

