package com.template.contracts.merchantContracts;

import com.template.model.merchant.MerchantAccount;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class MerchantContract implements Contract {

    public static final String ID = "com.template.contracts.MerchantContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<MerchantContract.Create> command = requireSingleCommand(tx.getCommands(), MerchantContract.Create.class);

        if(!tx.getInputs().isEmpty()){
            throw new IllegalArgumentException("No inputs should be consumed when creating Commercial bank");
        }
        if(tx.getOutputs().size() != 1){
            throw new IllegalArgumentException("There should be ONE output state of type Commercial Bank");
        }
        //Merchant specific constraints
        MerchantState merchantState = tx.outputsOfType(MerchantState.class).get(0);

        if (merchantState.getMerchant().getMerchantData() == null){
            throw new IllegalArgumentException("Merchant's data cannot have null value");
        }

        //verifier les data
        if (merchantState.getMerchant().getMerchantData().getBusinessName() == null){
            throw new IllegalArgumentException("Merchant's name cannot have null value");
        }

        if (merchantState.getMerchant().getMerchantData().getBusinessType() == null){
            throw new IllegalArgumentException("Merchant's type cannot have null value");
        }

        if (merchantState.getMerchant().getMerchantData().getAddress() == null){
            throw new IllegalArgumentException("Merchant's address cannot have null value");
        }

        if (merchantState.getMerchant().getMerchantData().getAgreement() == null){
            throw new IllegalArgumentException("Merchant's agreement cannot have null value");
        }

        if (merchantState.getMerchant().getMerchantData().getEmail() == null){
            throw new IllegalArgumentException("Merchant's email cannot have null value");
        }

        //verifier les info du compte
        MerchantAccount merchantAccount = merchantState.getMerchant().getMerchantAccounts().get(merchantState.getMerchant().getMerchantAccounts().size()-1);
        if(merchantAccount.getAccountId()== null){
            throw new IllegalArgumentException("Numero de compte est null!");
        }
        if(merchantAccount.getAccountType()== null){
            throw new IllegalArgumentException("Type de compte est null!");
        }
        if(merchantAccount.getBankIndcation()== null){
            throw new IllegalArgumentException("indication de la banque fournissant ce compte est null!");
        }
        if(merchantAccount.getPassword()== null){
            throw new IllegalArgumentException("Mot de passe du compte est null!");
        }
        if(merchantAccount.getCRUDDate()== null){
            throw new IllegalArgumentException("Date d'ouverture du compte est null!");
        }

        //Constraints on the signers
        if (!command.getSigners().containsAll(merchantState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))){
            throw new IllegalArgumentException("Commercial bank node and merchant node must be signers");
        }
    }
    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}
