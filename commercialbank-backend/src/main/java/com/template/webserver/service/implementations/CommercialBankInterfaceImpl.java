package com.template.webserver.service.implementations;

import com.template.flows.centralBankFlows.CentralBankReadAllTxByCBFlowInitiator;
import com.template.flows.centralBankFlows.CentralBankReadAllTxFlowInitiator;
import com.template.flows.commercialBankFlows.*;
import com.template.flows.merchantFlows.MerchantOtherAccountCreatorFlowInitiator;
import com.template.flows.merchantFlows.MerchantSuspendOrActiveOrSwithAccountTypeFlowInitiator;
import com.template.flows.model.*;
import com.template.flows.politiquesMonetairesFlows.*;
import com.template.flows.transactionsFlow.AllSentRecievedBalanceFlowInitiator;
import com.template.flows.transactionsFlow.TransactionInterBanksFlowInitiator;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.commercialBank.CommercialBankData;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.endUserStates.EndUserState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.emailSender.Email;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.service.interfaces.CommercialBankInterface;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class CommercialBankInterfaceImpl implements CommercialBankInterface {

    @Autowired
    private NodeRPCConnection nodeRPCConnection;


    @Override
    public AccountIdAndPassword create(CommercialBankAccountInfo commercialBankAccountInfo) {
        try {
            AccountIdAndPassword compteIdAndPassword =  nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankCreatorFlowInitiator.class, commercialBankAccountInfo).getReturnValue().get();
            if (compteIdAndPassword != null){
                System.out.println(compteIdAndPassword);
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("iwlwwcodmacansfn\n");
                email.setMailTo(commercialBankAccountInfo.getCommercialBankData().getEmail());
                email.setMailSubject("Ajout de vos données dans le système");

                String content = "Bonjour Chèr(e) responsable de la banque "+commercialBankAccountInfo.getCommercialBankData().getName()+"<br>"
                        +"Vous venez d'intégrer le nouveau système monétaire numérique, dont " +
                        "nous vous en remercions.<br> "
                        +"Désormais, vous pouvez accéder à votre wallet numérique en "
                        +"<h3><a href=\"wallet_link_here\" target=\"_self\">cliquant ici</a></h3>"
                        + "Merci d'utiliser ces informations pour vos différentes actions sur la plateforme.<br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                email.setMailContent(content);

                //Send now email
                new EmailSender(email).sendmail();

            }
            return compteIdAndPassword;
        }catch (Exception exception){
            return null;
        }

    }

    @Override
    public CommercialBankData getCommercialBankById(String commercialBankAccountId) {
        try {
            return nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankReadFlowInitiator.class,commercialBankAccountId).getReturnValue().get();

        }catch (Exception exception){
            return null;
        }
    }

    public List<StateAndRef<CommercialBankState>> getAll(){
        return nodeRPCConnection.proxy.vaultQuery(CommercialBankState.class).getStates();
    }

    @Override
    public AccountIdAndPassword update(CommercialBankData commercialBankData, String commercialBankAccountId) {
        try {
            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankUpdaterFlowInitiator.class, commercialBankData, commercialBankAccountId).getReturnValue().get();
            if (compteIdAndPassword != null){
                System.out.println("compteIdAndPassword \n"+compteIdAndPassword);
                //send email
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("iwlwwcodmacansfn\n");
                email.setMailTo(commercialBankData.getEmail());
                email.setMailSubject("Mise à jour de vos données");

                String content = "Bonjour Chèr(e)"+commercialBankData.getName()+"<br>"
                        +"Vos données viennent d'être mises à jour dans le système.<br>"
                        +"Desormais, vous allez utiliser votre wallet numérique avec : <br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>" ;
                email.setMailContent(content);

                //Send now email
                new EmailSender(email).sendmail();

            }
            return compteIdAndPassword;


        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {

        try {
            String suspendEndUserEmail = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankSuspendOrActiveOrSwithAccountTypeFlowInitiator.class,suspendOrActiveOrSwithAccountType).getReturnValue().get();
            if (suspendEndUserEmail != null){
                //envoyer le mail de creation
                Email emailFromTo = new Email();
                emailFromTo.setMailFrom("cbdc.talan@gmail.com");
                emailFromTo.setMailFromPassword("iwlwwcodmacansfn\n");
                emailFromTo.setMailTo(suspendEndUserEmail);
                String activeOrDesactiveOrSwithAccountType;
                if (suspendOrActiveOrSwithAccountType.getNewAccountType()!=null){
                    emailFromTo.setMailSubject("Changement de type de votre compte");
                    activeOrDesactiveOrSwithAccountType = "changé de type";
                }
                else if (suspendOrActiveOrSwithAccountType.isSuspendFlag() ) {
                    emailFromTo.setMailSubject("Suppression de votre compte");
                    activeOrDesactiveOrSwithAccountType = "supprimé";
                }else{
                    emailFromTo.setMailSubject("Activation de votre compte");
                    activeOrDesactiveOrSwithAccountType = "Activé";
                }

                String content = "Bonjour chèr(e) responsable de la banque, <br>"
                        + "Votre compte dans le système monétaire numérique est "+activeOrDesactiveOrSwithAccountType+"<br>"
                        + "En cas de changement d'avis, contacter nous en "
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setMailContent(content);

                //send email now
                new EmailSender(emailFromTo).sendmail();

                return 1;
            }
            return 0;

        }catch (Exception exception){
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public AccountIdAndPassword createOtherCommercialBankAccount(NewCommercialBankAccount newCommercialBankAccount) {
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankOtherAccountCreatorFlowInitiator.class,newCommercialBankAccount).getReturnValue().get();

            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                Email emailFromTo = new Email();
                emailFromTo.setMailFrom("cbdc.talan@gmail.com");
                emailFromTo.setMailFromPassword("iwlwwcodmacansfn\n");
                emailFromTo.setMailTo(newCommercialBankAccount.getCommercialBankEmail());
                emailFromTo.setMailSubject("Création d'un autre compte dans le système");

                String content = "Bonjour chèr(e) client(e) responsable de la banque "+newCommercialBankAccount.getNom()+" . <br>"
                        + "Vous venez de créer un autre compte le nouveau système monétaire numérique, dont nous vous en remercions.<br>"
                        + "Desormais, vous pouvez acceder à votre wallet numérique en "
                        + "<h3><a href=\"wallet_link_here\" target=\"_self\">cliquant ici</a></h3>"
                        + "Merci d'utiliser ces informations pour vos différentes actions sur la plateforme.<br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setMailContent(content);

                //send email now
                new EmailSender(emailFromTo).sendmail();

                return compteIdAndPassword;
            }
            return null;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }


    public List<EndUser> getAllUsers(){
        Vault.Page<EndUserState> vaultStates = nodeRPCConnection.proxy.vaultQuery(EndUserState.class);
        List<StateAndRef<EndUserState>> allUsers = vaultStates.getStates();
        if(allUsers == null){
            return null;
        }
        List<EndUser> users = new ArrayList<>();
        for (StateAndRef<EndUserState> user : allUsers){
            if (user.getState() != null && user.getState().getData() != null && user.getState().getData().getEndUser() != null){
                if (ifExist(users, user.getState().getData().getEndUser().getEndUserAccounts().get(0).getAccountId())==1){
                    users = deleteOldRegulation(users, user.getState().getData().getEndUser());
                }else{
                    users.add(user.getState().getData().getEndUser());
                }
            }

        }

        return users;
    }

    public int ifExist(List<EndUser> userList ,String sender){
        if (userList == null){
            return 0;
        }
        for (EndUser user : userList){
            for (EndUserAccount userAccount : user.getEndUserAccounts()){
                if (userAccount.getAccountId().equals(sender)){
                    return 1;
                }
            }
        }
        return 0;
    }

    public List<EndUser> deleteOldRegulation (List<EndUser> userList, EndUser user){
        List<EndUser> temp = new ArrayList<>();
        if (userList != null ) {
            for (int i = 0 ; i<= userList.size()-1; i++){
                int val = 0;
                for (EndUserAccount userAccount : userList.get(i).getEndUserAccounts()){
                    if (userAccount.getAccountId().equals(user.getEndUserAccounts().get(0).getAccountId())){
                        val = 1;
                    }

                }
                if (val == 0){
                    temp.add(userList.get(i));
                }
            }
            temp.add(user);
            return temp;

        }

        return null;
    }

    public  List<RegulateurDevise> getLastRegulattionDevise(String pays) {
        try{
            List<RegulateurDevise> regulateurDevise = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurDeviseReadFlowInitiator.class, pays).getReturnValue().get();
            System.out.println(regulateurDevise);
            return regulateurDevise;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public RegulateurTransactionInterPays getLastRegulationTransactionInterPays(String pays) {
        try{
            RegulateurTransactionInterPays regulateurTransactionInterPays = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurTransactionInterPaysReadFlowInitiator.class, pays).getReturnValue().get();
            return regulateurTransactionInterPays;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public RegulateurTransactionLocale getLastRegulationTransactionLocaleString(String pays) {
        try{
            RegulateurTransactionLocale regulateurTransactionLocale1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurTransactionLocaleReadFlowInitiator.class, pays).getReturnValue().get();
            return regulateurTransactionLocale1;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public RegulateurMasseMonnetaire getLastRegulationMasseMonnetaire(String pays) {
        try{
            RegulateurMasseMonnetaire regulateurMasseMonnetaire = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurMasseMonnetaireReadFlowInitiator.class, pays).getReturnValue().get();
            return regulateurMasseMonnetaire;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Double> getUpdatesBalance( String sender){
        try {
            List<Double> balance = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    AllSentRecievedBalanceFlowInitiator.class,sender).getReturnValue().get();
            return balance;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public Double getCurrentBalance(String sender){
        try {
            Double balance = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CurrentAmountCBDCReadFlowInitiator.class,sender).getReturnValue().get();
            return balance;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public List<Double> getAllTx(String sender){
        try {
            List<Double> transactions = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankReadAllTxFlowInitiator.class,sender).getReturnValue().get();
            return transactions;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<TransactionInterBanks> getAlltxByCommercialBank(String sender){

        try {
            List<TransactionInterBanks> transactions = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankReadAllTxByCBFlowInitiator.class,sender).getReturnValue().get();
            return transactions;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
}

    public TransactionInterBanks createTransaction(TransactionInterbancaire transactionInterbancaire){
        try {
            TransactionInterBanks transactionInterBanks = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    TransactionInterBanksFlowInitiator.class,transactionInterbancaire).getReturnValue().get();
            return transactionInterBanks;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
