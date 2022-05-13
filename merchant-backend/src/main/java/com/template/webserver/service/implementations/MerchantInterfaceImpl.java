package com.template.webserver.service.implementations;

import com.template.flows.merchantFlows.*;
import com.template.flows.model.*;
import com.template.model.merchant.MerchantData;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.model.MerchantAccountModel;
import com.template.webserver.security.SecurityConstante;
import com.template.webserver.service.interfaces.MerchantInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.template.webserver.emailSender.Email;
import com.template.webserver.emailSender.EmailSender;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MerchantInterfaceImpl implements MerchantInterface {

    @Autowired
    private NodeRPCConnection nodeRPCConnection;

    @Override
    public AccountIdAndPassword create(MerchantAccountInfo merchantAccountInfo) {
        System.out.println(merchantAccountInfo);
        try {
            AccountIdAndPassword compteIdAndPassword =nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    MerchantCreatorFlowInitiator.class, merchantAccountInfo).getReturnValue().get();
            if (compteIdAndPassword != null){
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("cbdctalan2022");
                email.setMailTo(merchantAccountInfo.getMerchantData().getEmail());
                email.setMailSubject("Ajout de vos données dans le système");

                String content = "Bonjour Chèr(e) "+merchantAccountInfo.getMerchantData().getBusinessName()+". <br>"
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
    public MerchantData getMerchantById(String merchantAccountId) {
        try {
            return nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    MerchantReaderFlowInitiator.class,merchantAccountId).getReturnValue().get();

        }catch (Exception exception){
            return null;
        }
    }

    @Override
    public AccountIdAndPassword update(MerchantData merchantData, String merchantAccountId) {

        try {
            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    MerchantUpdaterFlowInitiator.class, merchantData, merchantAccountId).getReturnValue().get();
            if (compteIdAndPassword != null){
                //send email
                Email email = new Email();
                email.setMailFrom("cbdc.talan@gmail.com");
                email.setMailFromPassword("cbdctalan2022");
                email.setMailTo(merchantData.getEmail());
                email.setMailSubject("Mise à jour de vos données dans le système");

                String content = "Bonjour Chèr(e)"+merchantData.getBusinessName()+"<br>"
                        +"Vos données viennent d'être mises à jour dans le système.<br>"
                        +"Desormais, vous allez utiliser votre wallet numérique avec : <br>"
                        +"Numéro de compte : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">" + compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>" ;
                email.setMailContent(content);

                //Send now email
                new EmailSender(email).sendmail();

            }
            return compteIdAndPassword;

        }catch (Exception exception){
            return null;
        }    }


    public int sendEmailVerification(String email) throws MessagingException, IOException {
        System.out.println("hello im here");
        System.out.println(email);
        int digit = ((int)(Math.random()*9000)+1000);
        //envoyer le mail de creation
        Email emailFromTo = new Email();
        emailFromTo.setMailFrom("cbdc.talan@gmail.com");
        emailFromTo.setMailFromPassword("cbdctalan2022");
        emailFromTo.setMailTo(email);
        emailFromTo.setMailSubject("Validation de création du compte");

        String content = "Bienvenue ! <br>"
                + "Nous vous prions de vérifier votre inscription par ce code .<br>"
                + "Code de vérification : <b style=\"color:rgb(255,0,0);\"> "+ digit +"</b><br>";
        emailFromTo.setMailContent(content);

        //send email now
        new EmailSender(emailFromTo).sendmail();

        System.out.println(digit);
        return digit;
    }

    @Override
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {

        try {
            String suspendEndUserEmail = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    MerchantSuspendOrActiveOrSwithAccountTypeFlowInitiator.class,suspendOrActiveOrSwithAccountType).getReturnValue().get();
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

                String content = "Bonjour chèr(e) responsable de l'entreprise . <br>"
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
    public AccountIdAndPassword createOtherMerchantAccount(NewMerchantAccount newMerchantAccount) {
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    MerchantOtherAccountCreatorFlowInitiator.class,newMerchantAccount).getReturnValue().get();

            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                Email emailFromTo = new Email();
                emailFromTo.setMailFrom("cbdc.talan@gmail.com");
                emailFromTo.setMailFromPassword("cbdctalan2022");
                emailFromTo.setMailTo(newMerchantAccount.getMerchantEmail());
                emailFromTo.setMailSubject("Création d'un autre compte dans le système");

                String content = "Bonjour chèr(e) client(e) responsable de l'entreprise "+newMerchantAccount.getAgreement()+" . <br>"
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

    public int notifyAdmin(MerchantAccountModel merchantAccountModel){
        try{
            Map<String,Object> userDataToken = new HashMap<>();
            userDataToken.put("endUser",merchantAccountModel);
            //acces token definition
            String jwtToken = Jwts.builder()
                    .setExpiration(new Date(System.currentTimeMillis()+ SecurityConstante.EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SecurityConstante.SECRET)
                    .setClaims(userDataToken)
                    .compact();

            //envoyer le mail de creation
            Email emailFromTo = new Email();
            emailFromTo.setMailFrom("cbdc.talan@gmail.com");
            emailFromTo.setMailFromPassword("cbdctalan2022");
            emailFromTo.setMailTo(merchantAccountModel.getMerchantData().getEmail());
            emailFromTo.setMailSubject("Demande d'activation de compte");

            String content = "Bonjour "+merchantAccountModel.getBankIndcation()+". <br>"
                    + "Je vous pris d'activer mon compte . <br>"
                    + "Nom de l'entreprise : "+ merchantAccountModel.getMerchantData().getBusinessName() +"</b><br>"
                    + "<h4><a href=\"http://localhost:4200/page/merchant/activation/"+ jwtToken +"\">Activer</a></h4>";
            emailFromTo.setMailContent(content);

            //send email now
            new EmailSender(emailFromTo).sendmail();
            return 1;

        }catch (Exception e){
            return -1;
        }

    }

}
