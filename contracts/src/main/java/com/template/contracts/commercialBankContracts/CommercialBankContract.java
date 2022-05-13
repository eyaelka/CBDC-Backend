package com.template.contracts.commercialBankContracts;

import com.template.model.commercialBank.CommercialBankAccount;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class CommercialBankContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.commercialBankContracts.CommercialBankContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<CommercialBankContract.Create> command = requireSingleCommand(tx.getCommands(), CommercialBankContract.Create.class);

        if(!tx.getInputs().isEmpty()){
            throw new IllegalArgumentException("No inputs should be consumed when creating Commercial bank");
        }
        if(tx.getOutputs().size() != 1){
            throw new IllegalArgumentException("There should be ONE output state of type Commercial Bank");
        }

       //Commercial Bank specific constraints
        CommercialBankState commercialBankState = tx.outputsOfType(CommercialBankState.class).get(0);
        Party addedBy = commercialBankState.getAddedBy();
        Party owner = commercialBankState.getOwner();

        if (addedBy.equals(owner)){
            throw new IllegalArgumentException("The Central bank and the added Commercial bank cannot be the same entity ");
        }

        if (commercialBankState.getCommercialBank().getCommercialBankData().getName() == null){
            throw new IllegalArgumentException("Commercial bank's name cannot have null value");
        }

        if (commercialBankState.getCommercialBank().getCommercialBankData().getEmail() == null){
            throw new IllegalArgumentException("Commercial bank's email cannot have null value");
        }

        if (commercialBankState.getCommercialBank().getCommercialBankData().getAbreviation() == null){
            throw new IllegalArgumentException("Commercial bank's abreviation cannot have null value");
        }

        if (commercialBankState.getCommercialBank().getCommercialBankData().getAddress() == null){
            throw new IllegalArgumentException("Commercial bank's address cannot have null value");
        }

        if (commercialBankState.getCommercialBank().getCommercialBankData().getPays() == null){
            throw new IllegalArgumentException("Commercial bank's pays cannot have null value");
        }

        //Account info verification
        CommercialBankAccount commercialBankAccount = commercialBankState.getCommercialBank().getCommercialBankAccounts()
                .get(commercialBankState.getCommercialBank().getCommercialBankAccounts().size()-1);
        if(commercialBankAccount.getAccountId()== null){
            throw new IllegalArgumentException("Numero de compte est null!");
        }
        if(commercialBankAccount.getAccountType()== null){
            throw new IllegalArgumentException("Type de compte est null!");
        }
        if(commercialBankAccount.getPassword()== null){
            throw new IllegalArgumentException("Mot de passe du compte est null!");
        }
        if(commercialBankAccount.getCRUDDate()== null){
            throw new IllegalArgumentException("Date d'ouverture du compte est null!");
        }

        //Constraints on the signers
        final List<PublicKey> requiredSigners = command.getSigners();
        final List<PublicKey> expectedSigners = Arrays.asList(addedBy.getOwningKey(), owner.getOwningKey());

        if (requiredSigners.size() !=2){
            throw new IllegalArgumentException("There must be two signers");
        }

        if (!(requiredSigners.containsAll(expectedSigners))){
            throw new IllegalArgumentException("The Central bank and The commercial bank must be signers");
        }

    }
    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}
