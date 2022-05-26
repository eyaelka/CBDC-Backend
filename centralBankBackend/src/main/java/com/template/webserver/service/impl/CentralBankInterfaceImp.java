package com.template.webserver.service.impl;

import com.template.flows.centralBankFlows.*;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.flows.model.NewCentralBankAccount;
import com.template.flows.politiquesMonetairesFlows.*;
import com.template.model.centralBank.CentralBankData;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.emailSender.EmailFromTo;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.model.SuspendOrActiveOrSwithAccountTypeModel;
import com.template.webserver.security.SecurityConstante;
import com.template.webserver.service.interfaces.CentralBankInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class CentralBankInterfaceImp implements CentralBankInterface {

    @Autowired
    private NodeRPCConnection nodeRPCConnection;


    @Override
    public AccountIdAndPassword save(CentralBankAccountInfo centralBankAccountInfo) {
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankCreatorFlowInitiator.class,centralBankAccountInfo).getReturnValue().get();

            if (compteIdAndPassword != null){
                    //envoyer le mail de creation
                    EmailFromTo emailFromTo = new EmailFromTo();
                    emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                    emailFromTo.setEmailFromPassWord("cbdctalan2022");
                    emailFromTo.setEmailReceiver(centralBankAccountInfo.getCentralBankData().getEmail());
                    emailFromTo.setEmailSubject("Création de compte dans le système");

                    String content = "Bonjour cher  responsable de la banque "+centralBankAccountInfo.getCentralBankData().getNom()+". <br>"
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

                return compteIdAndPassword;
            }
            return null;

        }catch (Exception exception){
            return null;
        }
    }



    @Override
    public AccountIdAndPassword createOtherBankCount(NewCentralBankAccount newCentralBankAccount) {
        try {
            AccountIdAndPassword compteIdAndPassword  = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankOtherAccountCreatorFlowInitiator.class,newCentralBankAccount).getReturnValue().get();

            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("cbdctalan2022");
                emailFromTo.setEmailReceiver(newCentralBankAccount.getCentralBankEmail());
                emailFromTo.setEmailSubject("Création d'un autre compte dans le système");

                String content = "Bonjour chère  responsable de la banque "+newCentralBankAccount.getCentralBankName()+". <br>"
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
    public AccountIdAndPassword update(CentralBankData banqueCentrale, String centralBankAccountId) {
        //Methode à modifier
        try {
            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankUpdaterFlowInitiator.class,banqueCentrale,centralBankAccountId).getReturnValue().get();
            System.out.println("le num de compte est "+compteIdAndPassword);
            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("cbdctalan2022");
                emailFromTo.setEmailReceiver(banqueCentrale.getEmail());
                emailFromTo.setEmailSubject("Mise à jour de vos données");

                String content = "Bonjour chèr responsable de la banque "+banqueCentrale.getNom()+"<br>"
                        + "Vos données dans le système monétaire numérique viennent d'être mise à jour<br>"
                        + "Desormais, vous allez utiliser votre wallet numérique avec<br> "
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
            return null;
        }
    }

    @Override
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountTypeModel suspendOrActiveOrSwithAccountTypeModel) {

        try {
            String suspendCentralBankEmail = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankSuspendOrActiveOrSwithAccountTypeFlowInitiator.class,
                    suspendOrActiveOrSwithAccountTypeModel.getCentralBankAccountId(),
                    suspendOrActiveOrSwithAccountTypeModel.isSuspendFlag(),
                    suspendOrActiveOrSwithAccountTypeModel.getNewAccountType()).getReturnValue().get();
            if (suspendCentralBankEmail != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("cbdctalan2022");
                emailFromTo.setEmailReceiver(suspendCentralBankEmail);

                String activeOrDesactiveOrSwithAccountType;
                if (suspendOrActiveOrSwithAccountTypeModel.getNewAccountType()!=null){
                    emailFromTo.setEmailSubject("Changement de type de votre compte");
                    activeOrDesactiveOrSwithAccountType = "changé de type";
                }
                else if (suspendOrActiveOrSwithAccountTypeModel.isSuspendFlag() ) {
                    emailFromTo.setEmailSubject("Suppression de votre compte");
                    activeOrDesactiveOrSwithAccountType = "supprimé";
                }else{
                    emailFromTo.setEmailSubject("Activation de votre compte");
                    activeOrDesactiveOrSwithAccountType = "Activé";
                }

                String content = "Bonjour cher responsable de la banque centrale <br>"
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
    public CentralBankData read(String centralBankAccountId) {
        try {
            return  nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankReadFlowInitiator.class,centralBankAccountId).getReturnValue().get();
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public AccountIdAndPassword superAdmin(CentralBankAccountInfo centralBankAccountInfo) {
        try {
            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankCreateSuperAdminFlowInitiator.class, centralBankAccountInfo).getReturnValue().get();
            System.out.println(compteIdAndPassword);

            if (compteIdAndPassword != null) {
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("cbdctalan2022");
                emailFromTo.setEmailReceiver(centralBankAccountInfo.getCentralBankData().getEmail());
                emailFromTo.setEmailSubject("Compte Super Admin");

                String content = "Bonjour Super Admin " + centralBankAccountInfo.getCentralBankData().getNom() + ". <br>"
                        + "Vous pouvez utiliser ces coordonnées pour l'ajout de la Banque Centrale.<br>"
                        + "Numéro de compte Super Admin: <b style=\"color:rgb(255,0,0);\"> " + compteIdAndPassword.getCompteId() + "</b><br>"
                        + "Mot de passe Super Admin : <b style=\"color:rgb(255,0,0);\">" + compteIdAndPassword.getPassword() + "</b><br>";
                emailFromTo.setEmailContent(content);

                //send email now
                new EmailSender(emailFromTo).sendmail();
            }
                return compteIdAndPassword;
            }catch (Exception exception){
            return null;
        }
    }

    @Override
    public RegulateurMasseMonnetaire defineMasseMonnetaireRegulation(RegulateurMasseMonnetaire regulateurMasseMonnetaire) {
        System.out.println(regulateurMasseMonnetaire);
        try {
            RegulateurMasseMonnetaire regulateurMasseMonnetaire1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurMasseMonnetaireCreatorFlowInitiator.class, regulateurMasseMonnetaire).getReturnValue().get();
            System.out.println(regulateurMasseMonnetaire1);
            return regulateurMasseMonnetaire1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
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

    @Override
    public RegulateurDevise defineDeviseRegulation(RegulateurDevise regulateurDevise) {
        try{
            RegulateurDevise regulateurDevise1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurDeviseCreatorFlowInitiator.class, regulateurDevise).getReturnValue().get();
            return regulateurDevise1;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public RegulateurDevise getLastRegulattionDevise(String pays) {
        try{
            RegulateurDevise regulateurDevise = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurDeviseReadFlowInitiator.class, pays).getReturnValue().get();
            System.out.println(regulateurDevise);
            return regulateurDevise;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public RegulateurTransactionInterPays defineTransactionInterPaysRegulation(RegulateurTransactionInterPays regulateurTransactionInterPays) {
        try{RegulateurTransactionInterPays regulateurTransactionInterPays1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                RegulateurTransactionInterPaysCreatorFlowInitiator.class, regulateurTransactionInterPays).getReturnValue().get();
            System.out.println(regulateurTransactionInterPays1);
            return regulateurTransactionInterPays1;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public RegulateurMasseMonnetaire getLastRegulationTransactionInterPays(String pays) {
        try{
            RegulateurMasseMonnetaire regulateurMasseMonnetaire = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurMasseMonnetaireReadFlowInitiator.class, pays).getReturnValue().get();
            System.out.println(regulateurMasseMonnetaire);
            return regulateurMasseMonnetaire;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RegulateurTransactionLocale defineTransactionLocaleRegulation(RegulateurTransactionLocale regulateurTransactionLocale) {
        System.out.println(regulateurTransactionLocale);
        try{
            RegulateurTransactionLocale regulateurTransactionLocale1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurTransactionLocaleCreatorFlowInitiator.class, regulateurTransactionLocale).getReturnValue().get();
            return regulateurTransactionLocale1;
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
            System.out.println(regulateurTransactionLocale1);
            return regulateurTransactionLocale1;
        }catch (Exception e){
            e.printStackTrace();
        return null;
    }

//    public String getToken( AccountIdAndPassword accountIdAndPassword){
//
//        try {
//            AccountIdAndPassword accountIdPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
//                    LoadCentralBankByAccountIdFlowInitiator.class,accountIdAndPassword.getCompteId()).getReturnValue().get();
//            if(accountIdAndPassword == null) throw  new UsernameNotFoundException("Cette banque centrale n'existe pas");
//            //Preparer les roles de l sous forme de collection d'objets compressible par spring security
//            Collection<GrantedAuthority> authorisations = new ArrayList<GrantedAuthority>();
//
//            if (accountIdPassword.getCompteId().substring(accountIdAndPassword.getCompteId().length()-2).equals("bc")) {
//                //bc = bank central
//                authorisations.add(new SimpleGrantedAuthority("centralbank"));
//
//            }else{
//                //super admin
//                authorisations.add(new SimpleGrantedAuthority("cbdcadmin"));
//
//            }
//
//            //acces token definition
//            String jwtToken = Jwts.builder()
//                    .setSubject(accountIdAndPassword.getCompteId())//on peut mettre tout ce qu'on veut
//                    .setExpiration(new Date(System.currentTimeMillis()+ SecurityConstante.EXPIRATION_TIME))
//                    .setIssuer("/centralbank/login")
//                    .signWith(SignatureAlgorithm.HS256, SecurityConstante.SECRET)
//                    .claim("roles", authorisations)
//                    .compact();
//            return jwtToken;
//
//        }catch (Exception exception){
//            throw  new UsernameNotFoundException("Problème d'accès au reseau");
//        }
//    }
    }
}
