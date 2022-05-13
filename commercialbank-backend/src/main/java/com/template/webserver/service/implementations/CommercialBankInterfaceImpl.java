package com.template.webserver.service.implementations;

import com.template.flows.commercialBankFlows.*;
import com.template.flows.merchantFlows.MerchantOtherAccountCreatorFlowInitiator;
import com.template.flows.merchantFlows.MerchantSuspendOrActiveOrSwithAccountTypeFlowInitiator;
import com.template.flows.model.*;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankData;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.emailSender.Email;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.service.interfaces.CommercialBankInterface;

import net.corda.core.contracts.StateAndRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("cbdctalan2022");
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
                //send email
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("cbdctalan2022");
                email.setMailTo(commercialBankData.getEmail());
                email.setMailSubject("Mise à jour de vos données");

                String content = "Bonjour Chèr(e)"+commercialBankData.getName()+"<br>"
                        +"Vos données viennent d'être mises à jour dans le système.<br>"
                        +"Desormais, vous allez utiliser votre wallet numérique avec : <br>"
                        +"Numéro de compte : <b style=\"color:rgb(255,0,0);\"> \"+ compteIdAndPassword.getCompteId() +\"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">\"+ compteIdAndPassword.getPassword() +\"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>" ;
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
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {

        try {
            String suspendEndUserEmail = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CommercialBankSuspendOrActiveOrSwithAccountTypeFlowInitiator.class,suspendOrActiveOrSwithAccountType).getReturnValue().get();
            if (suspendEndUserEmail != null){
                //envoyer le mail de creation
                Email emailFromTo = new Email();
                emailFromTo.setMailFrom("cbdc.talan@gmail.com");
                emailFromTo.setMailFromPassword("cbdctalan2022");
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
                emailFromTo.setMailFromPassword("cbdctalan2022");
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






}
