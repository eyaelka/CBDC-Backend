package com.template.webserver;

import com.template.flows.centralBankFlows.CentralBankCreatorFlowInitiator;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.model.centralBank.CentralBankData;
import com.template.webserver.emailSender.EmailFromTo;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.security.SecurityConstante;
import com.template.webserver.service.interfaces.CentralBankInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.WebApplicationType.SERVLET;

/**
 * Central bank backend application.
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Starter {
    @Autowired
    private NodeRPCConnection nodeRPCConnection;

    /**
     * Starts our Spring Boot application.
     */
    public static void main(String[] args) {
//        SpringApplication app = new SpringApplication(Starter.class);
//        app.setBannerMode(Banner.Mode.OFF);
//        app.setWebApplicationType(SERVLET);
//        app.run(args);


        ///////////
        ApplicationContext applicationContext = SpringApplication.run(Starter.class,args);
        CentralBankInterface centralBankInterface = applicationContext.getBean(CentralBankInterface.class);

        CentralBankData centralBankData = new CentralBankData();
        centralBankData.setNom("admin");
        centralBankData.setEmail("eya.elkamel@etudiant-fst.utm.tn");
        centralBankData.setPays("admin");
        centralBankData.setAdresse("admin");
        centralBankData.setLoiCreation("admin");
        CentralBankAccountInfo centralBankAccountInfo = new CentralBankAccountInfo();
        centralBankAccountInfo.setCentralBankData(centralBankData);
        centralBankAccountInfo.setAccountType("admin");
        centralBankAccountInfo.setSuspend(false);
        centralBankInterface.superAdmin(centralBankAccountInfo);
                //////////////////
        System.out.println(centralBankInterface.read("45584699bc"));
    }


//    @Bean
//    public int addSuperAdmin() {
//
//        try {
//            CentralBankAccountInfo centralBankAccountInfo = new CentralBankAccountInfo();
//            centralBankAccountInfo.getCentralBankData().setNom("admin");
//            centralBankAccountInfo.getCentralBankData().setPays("admin");
//            centralBankAccountInfo.getCentralBankData().setAdresse("admin");
//            centralBankAccountInfo.getCentralBankData().setLoiCreation("admin");
//            centralBankAccountInfo.getCentralBankData().setEmail("eya.elkamel@etudiant-fst.utm.tn");
//            centralBankAccountInfo.setAccountType("admin");
//            centralBankAccountInfo.setSuspend(false);
//            System.out.println(centralBankAccountInfo);
//
//            //envoyer le mail de creation
//            EmailFromTo emailFromTo = new EmailFromTo();
//            emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
//            emailFromTo.setEmailFromPassWord("cbdctalan2022");
//            emailFromTo.setEmailReceiver(centralBankAccountInfo.getCentralBankData().getEmail());
//            emailFromTo.setEmailSubject("Compte Super Admin");
//
//
//            AccountIdAndPassword compteIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
//                    CentralBankCreatorFlowInitiator.class, centralBankAccountInfo).getReturnValue().get();
//            System.out.println(compteIdAndPassword);
//
//            if (compteIdAndPassword != null) {
//                String content = "Bonjour Super Admin " + centralBankAccountInfo.getCentralBankData().getNom() + ". <br>"
//                        + "Vous pouvez utiliser ces coordonnées pour l'ajout de la Banque Centrale.<br>"
//                        + "Numéro de compte Super Admin: <b style=\"color:rgb(255,0,0);\"> " + compteIdAndPassword.getCompteId() + "</b><br>"
//                        + "Mot de passe Super Admin : <b style=\"color:rgb(255,0,0);\">" + compteIdAndPassword.getPassword() + "</b><br>";
//                emailFromTo.setEmailContent(content);
//
//                //send email now
//                new EmailSender(emailFromTo).sendmail();
//                return 1;
//            } else {
//                String content = "Bonjour Super Admin " + centralBankAccountInfo.getCentralBankData().getNom() + ". <br>"
//                        + "Nous sommes désolés de vous annoncé que votre compte n'a pas été crée. <br>"
//                        + "Motif : Identifiant ou Mot de passe null";
//                emailFromTo.setEmailContent(content);
//                //send email now
//                new EmailSender(emailFromTo).sendmail();
//                return 1;
//            }
//
//
//        } catch (Exception e) {
//            return -1;
//        }
//
//
//    }
}





