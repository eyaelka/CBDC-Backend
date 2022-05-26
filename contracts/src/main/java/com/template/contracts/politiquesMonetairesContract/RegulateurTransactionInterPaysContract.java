package com.template.contracts.politiquesMonetairesContract;

import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class RegulateurTransactionInterPaysContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<RegulateurTransactionInterPaysContract.Create> command = requireSingleCommand(tx.getCommands(),
                RegulateurTransactionInterPaysContract.Create.class);

        if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("No inputs should be consumed when RegulateurTransactionInterPaysContract");
        }
        if (tx.getOutputs().size() != 1) {
            throw new IllegalArgumentException("There should be ONE output state of type RegulateurTransactionInterPaysContract");
        }

        RegulateurTransactionInterPaysStates regulateurTransactionInterPaysStates = tx.outputsOfType(RegulateurTransactionInterPaysStates.class).get(0);

        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getMotifRegulation() == null) {
            throw new IllegalArgumentException("regulateur transaction motif cannot have null value");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getDate() == null) {
            throw new IllegalArgumentException("Date cannot have null value");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getPays() == null) {
            throw new IllegalArgumentException("country cannot have null value");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getBorneMinimum() < 0) {
            throw new IllegalArgumentException("borne min doit être >= 0");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getCentralBankFees() < 0) {
            throw new IllegalArgumentException(" fees doit être >=0");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getSeuilMaximumAutresTX() <0) {
            throw new IllegalArgumentException("seuil maximal Tx doit être >=0");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getSeuilMaximumInterbank() < 0) {
            throw new IllegalArgumentException("borne max interbank doit être >= 0");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getSeuilMaximumAutresTX() <
                regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getBorneMinimum()) {
            throw new IllegalArgumentException("seuil maximal Tx doit être >= à borne min");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getSeuilMaximumInterbank() <
                regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getBorneMinimum()) {
            throw new IllegalArgumentException("borne max interbank doit être >= borne min");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getPeriode() < 0) {
            throw new IllegalArgumentException(" Periode doit être >=0");
        }
        if (regulateurTransactionInterPaysStates.getRegulateurTransactionInterPays().getPaysBanqueCentral() ==null) {
            throw new IllegalArgumentException("pay BC est null");
        }

        //Constraints on the signers
//        if (!command.getSigners().containsAll(regulateurTransactionInterPaysStates.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))) {
//            throw new IllegalArgumentException("les parties prenantes doivent signer");
//        }
    }

    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}
