package com.template.contracts.politiquesMonetairesContract;

import com.template.contracts.merchantContracts.MerchantContract;
import com.template.model.merchant.MerchantAccount;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.states.merchantStates.MerchantState;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class RegulateurMasseMonnetaireContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<RegulateurMasseMonnetaireContract.Create> command = requireSingleCommand(tx.getCommands(),
                RegulateurMasseMonnetaireContract.Create.class);

        if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("No inputs should be consumed when regulateurMonnetaire");
        }
        if (tx.getOutputs().size() != 1) {
            throw new IllegalArgumentException("There should be ONE output state of type regulateur monnetaire");
        }
        RegulateurMasseMonnetaireStates regulateurMasseMonnetaireStates = tx.outputsOfType(RegulateurMasseMonnetaireStates.class).get(0);

        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getMotifRegulation() == null) {
            throw new IllegalArgumentException("regulateur masse monnetaire motif cannot have null value");
        }
        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getDate() == null) {
            throw new IllegalArgumentException("Date cannot have null value");
        }
        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getPays() == null) {
            throw new IllegalArgumentException("country cannot have null value");
        }
        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxDirecteur() < 0 ||
                regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxDirecteur() > 100) {
            throw new IllegalArgumentException("Taux directeur doit être entre 0 et 100");
        }
        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxReserveObligatoir() < 0 ||
                regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxReserveObligatoir() > 100) {
            throw new IllegalArgumentException("Taux de reserve obligatoir doit être entre 0 et 100");
        }
        if (regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxNegatif() > 0 ||
                regulateurMasseMonnetaireStates.getRegulateurMasseMonnetaire().getTauxNegatif() < -100) {
            throw new IllegalArgumentException("Taux negatif doit être entre -100 et 0");
        }
//        //Constraints on the signers
//        if (!command.getSigners().containsAll(regulateurMasseMonnetaireStates.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))) {
//            throw new IllegalArgumentException("les parties prenantes doivent signer");
//        }
    }

    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}

