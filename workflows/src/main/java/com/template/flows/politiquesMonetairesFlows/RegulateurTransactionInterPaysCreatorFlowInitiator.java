package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.politiquesMonetairesContract.RegulateurMasseMonnetaireContract;
import com.template.contracts.politiquesMonetairesContract.RegulateurTransactionInterPaysContract;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RegulateurTransactionInterPaysCreatorFlowInitiator extends FlowLogic<RegulateurTransactionInterPays> {

    private final RegulateurTransactionInterPays regulateurTransactionInterPays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurTransactionInterPaysCreatorFlowInitiator(
            RegulateurTransactionInterPays regulateurTransactionInterPays) {
        this.regulateurTransactionInterPays = regulateurTransactionInterPays;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public RegulateurTransactionInterPays call() throws FlowException {

        Party centralBankNode = getOurIdentity();
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        List<Party> partyList = new ArrayList<>();
//        Party endUserNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("endUserNode","Tunisie","TN"));
//        Party merchant = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("merchant","Tunisie","TN"));
        Party PartyB = getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("PartyB","New York","US"));
        partyList.add(PartyB);
//        getServiceHub().getNetworkMapCache().getAllNodes().forEach(nodeInfo -> {
//            if (notary != nodeInfo.getLegalIdentities().get(0) && centralBankNode != nodeInfo.getLegalIdentities().get(0)) {
//                partyList.add(nodeInfo.getLegalIdentities().get(0));
//            }
//        });

        RegulateurTransactionInterPaysStates regulateurTransactionInterPaysStates = null;
        for (int i = 0; i < partyList.size(); i++) {
            regulateurTransactionInterPaysStates = new RegulateurTransactionInterPaysStates(regulateurTransactionInterPays, centralBankNode, partyList.get(i), new UniqueIdentifier());

            final Command<RegulateurTransactionInterPaysContract.Create> txCommand =
                    new Command<>(new RegulateurTransactionInterPaysContract.Create(),
                            Arrays.asList(centralBankNode.getOwningKey()));
            final TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addOutputState(regulateurTransactionInterPaysStates);
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
        return regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays();
    }
}
