package com.template.webserver.service.impl;

import com.template.flows.endUserFlows.*;
import com.template.flows.model.*;
import com.template.flows.transactionsFlow.EndUserRetailTransactionsFlowInitiator;
import com.template.model.endUser.EndUserData;
import com.template.model.transactions.RetailTransactions;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.endUserStates.EndUserState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.emailSender.EmailFromTo;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.security.SecurityConstante;
import com.template.webserver.service.interfaces.EndUserInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.corda.core.contracts.StateAndRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EndUserInterfaceImpl implements EndUserInterface {

    @Autowired
    private NodeRPCConnection nodeRPCConnection;

    @Override
    public AccountIdAndPassword save(EndUserAccountInfo endUserAccountInfo) {
        System.out.println(endUserAccountInfo);
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserCreatorFlowInitiator.class,endUserAccountInfo).getReturnValue().get();
            System.out.println(compteIdAndPassword);
            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
                emailFromTo.setEmailReceiver(endUserAccountInfo.getEndUserData().getEmail());
                emailFromTo.setEmailSubject("Ajout de vos données et création du compte dans le système");

                String content = "Bonjour Chèr(e) "+endUserAccountInfo.getEndUserData().getNom()+". <br> "
                        + "Vous venez d'intégrer le nouveau système monétaire numérique, dont nous vous en remercions.<br>"
                        + "Desormais, vous pouvez acceder à votre wallet numérique en "
                        + "<h3><a href=\"wallet_link_here\" target=\"_self\">cliquant ici</a></h3>"
                        + "Merci d'utiliser ces informations pour vos différentes actions sur la plateforme.<br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setEmailContent(content);

                //send email now
                new EmailSender(emailFromTo).sendmail();
            }
            return compteIdAndPassword;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public AccountIdAndPassword createOtherEndUserCount(NewEndUserAccount newEndUserAccount) {
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserOtherAccountCreatorFlowInitiator.class,newEndUserAccount).getReturnValue().get();

            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
                emailFromTo.setEmailReceiver(newEndUserAccount.getEndUserEmail());
                emailFromTo.setEmailSubject("Création d'un autre compte dans le système");

                String content = "Bonjour chèr(e) client(e) "+newEndUserAccount.getCin()+". <br>"
                        + "Vous venez de créer un autre compte le nouveau système monétaire numérique, dont nous vous en remercions.<br>"
                        + "Desormais, vous pouvez acceder à votre wallet numérique en "
                        + "<h3><a href=\"wallet_link_here\" target=\"_self\">cliquant ici</a></h3>"
                        + "Merci d'utiliser ces informations pour vos différentes actions sur la plateforme.<br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setEmailContent(content);

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

    @Override
    public AccountIdAndPassword update(EndUserData endUserData, String endUserAccountId) {
        try {
            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserUpdaterFlowInitiator.class,endUserData,endUserAccountId).getReturnValue().get();
            if (compteIdAndPassword != null){
                System.out.println("compteIdAndPassword\n"+compteIdAndPassword);
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
                emailFromTo.setEmailReceiver(endUserData.getEmail());
                emailFromTo.setEmailSubject("Mise à jour de vos données");

                String content = "Bonjour Chèr(e) "+endUserData.getNom()+"<br>"
                        + "Vos données viennent d'être mises à jour dans le système . <br>"
                        + "Desormais, vous allez utiliser votre wallet numérique avec : <br>"
                        + "Numéro de compte : <b style=\"color:rgb(255,0,0);\"> "+ compteIdAndPassword.getCompteId() +"</b><br>"
                        + "Mot de passe : <b style=\"color:rgb(255,0,0);\">"+ compteIdAndPassword.getPassword() +"</b><br>"
                        + "Nous vous mercions à bien vouloir garder secrète ces informations. En cas de perte, contacter nous en"
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setEmailContent(content);

                //send email now
                new EmailSender(emailFromTo).sendmail();
            }
            return compteIdAndPassword;

        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }


    @Override
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        System.out.println(suspendOrActiveOrSwithAccountType);
        try {
            String suspendEndUserEmail = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserSuspendOrActiveOrSwithAccountTypeFlowInitiator.class,suspendOrActiveOrSwithAccountType).getReturnValue().get();
            if (suspendEndUserEmail != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
                emailFromTo.setEmailReceiver(suspendEndUserEmail);
                String activeOrDesactiveOrSwithAccountType;
                if (suspendOrActiveOrSwithAccountType.getNewAccountType()!=null){
                    emailFromTo.setEmailSubject("Changement de type de votre compte");
                    activeOrDesactiveOrSwithAccountType = "changé de type";
                }
                else if (suspendOrActiveOrSwithAccountType.isSuspendFlag() ) {
                    emailFromTo.setEmailSubject("Suppression de votre compte");
                    activeOrDesactiveOrSwithAccountType = "supprimé";
                }else{
                    emailFromTo.setEmailSubject("Activation de votre compte");
                    activeOrDesactiveOrSwithAccountType = "Activé";
                }

                String content = "Bonjour chèr(e) client(e) . <br>"
                        + "Votre compte dans le système monétaire numérique est "+activeOrDesactiveOrSwithAccountType+"<br>"
                        + "En cas de changement d'avis, contacter nous en "
                        + "<h4><a href=\"problem_link_here\" target=\"_self\">cliquant ici</a></h4>";
                emailFromTo.setEmailContent(content);

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
    public EndUserData read(String endUserAccountId) {
        try {
            return  nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserReaderFlowInitiator.class,endUserAccountId).getReturnValue().get();
        }catch (Exception exception){
            return null;
        }
    }

    public List<StateAndRef<EndUserState>> getAll(){
        return nodeRPCConnection.proxy.vaultQuery(EndUserState.class).getStates();
    }

    public int sendEmailVerification(String email) throws MessagingException, IOException {
        System.out.println("hello im here");
        System.out.println(email);
        int digit = ((int)(Math.random()*9000)+1000);
        //envoyer le mail de creation
        EmailFromTo emailFromTo = new EmailFromTo();
        emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
        emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
        emailFromTo.setEmailReceiver(email);
        emailFromTo.setEmailSubject("Validation de création du compte");

        String content = "Bienvenue ! <br>"
                + "Nous vous prions de vérifier votre inscription par ce code .<br>"
                + "Code de vérification : <b style=\"color:rgb(255,0,0);\"> "+ digit +"</b><br>";
        emailFromTo.setEmailContent(content);

        //send email now
        new EmailSender(emailFromTo).sendmail();

        System.out.println(digit);
        return digit;
    }

    public int notifyAdmin(EndUserData endUserData){
        System.out.println(endUserData);
        try{Map<String,Object> userDataToken = new HashMap<>();
            userDataToken.put("endUser",endUserData);
            //acces token definition
            String jwtToken = Jwts.builder()
                    .setExpiration(new Date(System.currentTimeMillis()+ SecurityConstante.EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SecurityConstante.SECRET)
                    .setClaims(userDataToken)
                    .compact();
            System.out.println("token \n"+jwtToken);

            //envoyer le mail de creation
            EmailFromTo emailFromTo = new EmailFromTo();
            emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
            emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
            emailFromTo.setEmailReceiver(endUserData.getEmail());
            emailFromTo.setEmailSubject("Demande d'activation de compte");

            String content = "Bonjour <br>"
                    + "Je vous pris d'activer mon compte . <br>"
                    + "CIN : "+ endUserData.getCin() +"</b><br>"
                    + "<h4><a href=\"http://localhost:4200/page/enduser/activation/"+jwtToken+"\" target=\"_self\">Activer</a></h4>";

//                    + "<h4><a href=\"http://localhost:4200/page/enduser/activation/\" target=\"_self\">cliquant ici</a></h4>";
//            + "<h4><a href=\"localhost:4200/page/enduser/activation/" +jwtToken+ "\">Activer</a></h4>";
            emailFromTo.setEmailContent(content);

            //send email now
            new EmailSender(emailFromTo).sendmail();
            return 1;

        }catch (Exception e){
            return -1;
        }

    }

    public RetailTransactions doTransaction(TransactionDetail transactionDetail){
        System.out.println("TX Data in service :"+transactionDetail);
        try {
            return  nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EndUserRetailTransactionsFlowInitiator.class,transactionDetail).getReturnValue().get();
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }


    }

}
