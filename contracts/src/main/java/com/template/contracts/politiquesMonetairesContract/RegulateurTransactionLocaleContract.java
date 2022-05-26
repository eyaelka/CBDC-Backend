package com.template.contracts.politiquesMonetairesContract;

import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class RegulateurTransactionLocaleContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<RegulateurTransactionLocaleContract.Create> command = requireSingleCommand(tx.getCommands(),
                RegulateurTransactionLocaleContract.Create.class);

        if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("No inputs should be consumed when we storage RegulateurTransactionLocale");
        }
        if (tx.getOutputs().size() != 1) {
            throw new IllegalArgumentException("There should be ONE output state of type RegulateurTransactionLocale");
        }

        RegulateurTransactionLocaleStates regulateurTransactionLocaleStates = tx.outputsOfType(RegulateurTransactionLocaleStates.class).get(0);

        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getMotifRegulation() == null) {
            throw new IllegalArgumentException("regulateur transaction motif cannot have null value");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getDate() == null) {
            throw new IllegalArgumentException("Date cannot have null value");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getPays() == null) {
            throw new IllegalArgumentException("country cannot have null value");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getBorneMinimum() < 0) {
            throw new IllegalArgumentException("borne min doit être >= 0");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getSeuilMaximumAutresTX() <0) {
            throw new IllegalArgumentException("seuil maximal Tx doit être >=0");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getSeuilMaximumInterbank() < 0) {
            throw new IllegalArgumentException("borne max interbank doit être >= 0");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getSeuilMaximumAutresTX() <
                regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getBorneMinimum()) {
            throw new IllegalArgumentException("seuil maximal Tx doit être >= à borne min");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getSeuilMaximumInterbank() <
                regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getBorneMinimum()) {
            throw new IllegalArgumentException("borne max interbank doit être >= borne min");
        }
        if (regulateurTransactionLocaleStates.getRegulateurTransactionLocale().getPeriode() < 0) {
            throw new IllegalArgumentException(" Periode doit être >=0");
        }
//
//        //Constraints on the signers
//        if (!command.getSigners().containsAll(regulateurTransactionLocaleStates.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))) {
//            throw new IllegalArgumentException("les parties prenantes doivent signer");
//        }
    }

    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}

