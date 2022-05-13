package com.template.contracts.endUserContracts;

import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.model.endUser.EndUserData;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class EndUserContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.endUserContracts.EndUserContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final CommandWithParties<EndUserContract.Create> command = requireSingleCommand(tx.getCommands(), EndUserContract.Create.class);

        if(!tx.getInputs().isEmpty()){
            throw new IllegalArgumentException("Aucune entrée ne peut être depenser (utiliser) pendant la creation de la endUser");
        }
        if(tx.getOutputs().size() != 1){
            throw new IllegalArgumentException("Seulement un seul endUser ne peut être enregistré à la fois !!!");
        }
        final EndUserState endUserState = tx.outputsOfType(EndUserState.class).get(0);

        if (endUserState == null){
            throw new IllegalArgumentException("Impossible d'enregistrer l'état null.");
        }
        EndUser endUser = endUserState.getEndUser();
        if (endUser.getEndUserData() == null){
            throw new IllegalArgumentException(" EndUser null.");
        }
        //verifier les data
        EndUserData endUserData = endUser.getEndUserData();
        if(endUserData.getNom()== null){
            throw new IllegalArgumentException("Nom endUser null!");
        }
        if(endUserData.getCin()== null){
            throw new IllegalArgumentException("CIN null!");
        }
        if(endUserData.getAdresse()== null){
            throw new IllegalArgumentException("Adresse null!");
        }
        if(endUserData.getEmail()== null){
            throw new IllegalArgumentException("Email null!");
        }
        if(endUserData.getDateNaissance()== null){
            throw new IllegalArgumentException("Date naissance est null!");
        }
        Long now = new Date().getTime();
        Long birthDate = endUserData.getDateNaissance().getTime();
        if ((now - birthDate) < 18*365*24*60*60*1000){//il faut avoir plus de 18 ans pour créer un compte
            throw new IllegalArgumentException("il faut avoir plus de 18 ans pour créer un compte");
        }
        if(endUserData.getNationalite()== null){
            throw new IllegalArgumentException("Nationnalité null!");
        }
        if(endUserData.getTelephone()== null){
            throw new IllegalArgumentException("Telephone null!");
        }
        if(endUserData.getBankWhoAddUser()== null){
            throw new IllegalArgumentException("Bank who wants to add end user is null!");
        }
        //verifier les info du compte
        EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(endUser.getEndUserAccounts().size()-1);
        if(endUserAccount.getAccountId()== null){
            throw new IllegalArgumentException("Numero de compte est null!");
        }
        if(endUserAccount.getAccountType()== null){
            throw new IllegalArgumentException("Type de compte est null!");
        }
        if(endUserAccount.getBankIndcation()== null){
            throw new IllegalArgumentException("indecation de la banque fournissant ce compte est null!");
        }
        if(endUserAccount.getPassword()== null){
            throw new IllegalArgumentException("Mot de passe du compte est null!");
        }
        if(endUserAccount.getCRUDDate()== null){
            throw new IllegalArgumentException("Date d'ouverture du compte est null!");
        }
        if(!command.getSigners().containsAll(endUserState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))){
            throw new IllegalArgumentException("bank Node Who wants to Add User et end User Node doivent signer");
        }
    }
    // Used to indicate the transaction's intent.
    public static class Create implements CommandData {
    }
}
