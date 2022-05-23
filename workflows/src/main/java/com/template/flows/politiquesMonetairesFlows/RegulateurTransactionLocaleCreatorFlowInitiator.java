package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.politiquesMonetairesContract.RegulateurTransactionLocaleContract;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RegulateurTransactionLocaleCreatorFlowInitiator extends FlowLogic<RegulateurTransactionLocale> {

    private final RegulateurTransactionLocale regulateurTransactionLocale;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurTransactionLocaleCreatorFlowInitiator(
            RegulateurTransactionLocale regulateurTransactionLocale) {
        this.regulateurTransactionLocale = regulateurTransactionLocale;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public RegulateurTransactionLocale call() throws FlowException {

        Party centralBankNode = getOurIdentity();
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        List<Party> partyList = new ArrayList<>();
        getServiceHub().getNetworkMapCache().getAllNodes().forEach(nodeInfo -> {
            if (notary != nodeInfo.getLegalIdentities().get(0) && centralBankNode != nodeInfo.getLegalIdentities().get(0)) {
                partyList.add(nodeInfo.getLegalIdentities().get(0));
            }
        });

        RegulateurTransactionLocaleStates regulateurTransactionLocaleStates = null;
        for (int i = 0; i < partyList.size(); i++) {
            regulateurTransactionLocaleStates = new RegulateurTransactionLocaleStates(regulateurTransactionLocale, centralBankNode, partyList.get(i), new UniqueIdentifier());

            final Command<RegulateurTransactionLocaleContract.Create> txCommand =
                    new Command<>(new RegulateurTransactionLocaleContract.Create(),
                            Arrays.asList(centralBankNode.getOwningKey()));
            final TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addOutputState(regulateurTransactionLocaleStates);
            builder.addCommand(txCommand);
            // Verifier si la transaction est valide.
            try {
                builder.verify(getServiceHub());
                // signer la transaction par owner kypaire.
                final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
                //initier le canal de communication entre initiateur et le recepteur de la TX
                FlowSession otherPartySession = initiateFlow(partyList.get(i));
                //collecter toute les signatures
                SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.tracker()));
                // finaliser la transaction
                subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
            } catch (Exception e) {
                return null;
            }
        }
        return regulateurTransactionLocaleStates.getRegulateurTransactionLocale();
    }
}
