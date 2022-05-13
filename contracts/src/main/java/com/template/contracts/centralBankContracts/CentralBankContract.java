package com.template.contracts.centralBankContracts;

import com.template.model.centralBank.CentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class CentralBankContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.CentralBankContract";
    @Override
    public void verify(LedgerTransaction tx) {

        final CommandWithParties<CentralBankContract.Create> command = requireSingleCommand(tx.getCommands(), CentralBankContract.Create.class);

        if(!tx.getInputs().isEmpty()){
            throw new IllegalArgumentException("Aucune entrée ne peut être depenser (utiliser) pendant la creation du compte de la BC");
        }
        if(tx.getOutputs().size() != 1){
            throw new IllegalArgumentException("Seulement un seul compte de la banque centrale ne peut être enregistré à la fois !!!");
        }

        final CentralBankState centralBankState = tx.outputsOfType(CentralBankState.class).get(0);

        if (centralBankState == null || centralBankState.getCentralBank() == null){
            throw new IllegalArgumentException("Impossible d'enregistrer un compte null.");
        }

        //central bank data verification
        CentralBankData centralBankData = centralBankState.getCentralBank().getCentralBankData();
        if (centralBankData == null){
            throw new IllegalArgumentException("Banque centrale null.");
        }
        if(centralBankData.getNom()== null){
            throw new IllegalArgumentException("Nom banque centrale null!");
        }
        if(centralBankData.getPays()== null){
            throw new IllegalArgumentException("Pays null!");
        }
        if(centralBankData.getAdresse()== null){
            throw new IllegalArgumentException("Adresse null!");
        }
        if(centralBankData.getLoiCreation()== null){
            throw new IllegalArgumentException("Loi création null!");
        }
        if(centralBankData.getEmail()== null){
            throw new IllegalArgumentException("Email est null!");
        }
        //central bank accound info verification
         CentralBankAccount centralBankAccount =
                centralBankState.getCentralBank().getCentralBankAccount().get(centralBankState.getCentralBank().getCentralBankAccount().size()-1);
        if (centralBankAccount == null){
            throw new IllegalArgumentException("Compte de laBanque centrale null.");
        }
        if(centralBankAccount.getAccountId() == null){
            throw new IllegalArgumentException("Numero compte est null");
        }
        if(centralBankAccount.getPassword() == null){
            throw new IllegalArgumentException("Mot de passe compte est null");
        }

        if(!command.getSigners().containsAll(centralBankState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))){
            throw new IllegalArgumentException("Au moins le sender doit signer !");
        }
    }
    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}
