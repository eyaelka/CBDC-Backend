package com.template.contracts.politiquesMonetairesContract;

import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;


import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class RegulateurDeviseContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<RegulateurDeviseContract.Create> command = requireSingleCommand(tx.getCommands(),
                RegulateurDeviseContract.Create.class);

        if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("No inputs should be consumed when we store regulateur devise");
        }
        if (tx.getOutputs().size() != 1) {
            throw new IllegalArgumentException("There should be ONE output state of type regulateur devise");
        }
        RegulateurDeviseStates regulateurDeviseStates = tx.outputsOfType(RegulateurDeviseStates.class).get(0);

        if (regulateurDeviseStates.getRegulateurDevise().getMotifVariation() == null) {
            throw new IllegalArgumentException("regulateur devise motif cannot have null value");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getDate() == null) {
            throw new IllegalArgumentException("Date cannot have null value");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getPays() == null) {
            throw new IllegalArgumentException("country cannot have null value");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getTauxAchat() < 0) {
            throw new IllegalArgumentException("Taux d'achat doit être > 0 ");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getTauxVente() < 0) {
            throw new IllegalArgumentException("Taux de vente doit être > 0 ");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getTauxVente() <
                regulateurDeviseStates.getRegulateurDevise().getTauxAchat()) {
            throw new IllegalArgumentException("Taux de vente doit être >= au taux d'achat ");
        }
        if (regulateurDeviseStates.getRegulateurDevise().getNom() == null) {
            throw new IllegalArgumentException("Nom devise est null");
        }

        //Constraints on the signers
//        if (!command.getSigners().containsAll(regulateurDeviseStates.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))) {
//            throw new IllegalArgumentException("les parties prenantes doivent signer");
//        }
    }

    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}

