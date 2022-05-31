package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@InitiatingFlow
@StartableByRPC
public class RegulateurTransactionLocaleReadFlowInitiator extends FlowLogic<RegulateurTransactionLocale> {

    private String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurTransactionLocaleReadFlowInitiator(String pays) {
        this.pays = pays;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public RegulateurTransactionLocale call() throws FlowException {
        List<StateAndRef<RegulateurTransactionLocaleStates>> allTxLocal =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();
        List<RegulateurTransactionLocale> filtered = new ArrayList<>();
        for (StateAndRef<RegulateurTransactionLocaleStates> regulateurTxLocal : allTxLocal){
            RegulateurTransactionLocaleStates states = regulateurTxLocal.getState().getData();
            if (states.getRegulateurTransactionLocale().getPays().equalsIgnoreCase(pays)){
                filtered.add(regulateurTxLocal.getState().getData().getRegulateurTransactionLocale());
            }
        }
        return filtered.get(filtered.size()-1);

    }
}

