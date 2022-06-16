package com.template.webserver.service.impl;

import com.template.flows.centralBankFlows.*;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.flows.model.NewCentralBankAccount;
import com.template.flows.model.TransactionInterbancaire;
import com.template.flows.politiquesMonetairesFlows.*;
import com.template.flows.transactionsFlow.AllSentRecievedBalanceFlowInitiator;
import com.template.flows.transactionsFlow.EmissionCBDCFlowInitiator;
import com.template.flows.transactionsFlow.TransactionInterBanksFlowInitiator;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankData;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.emailSender.EmailFromTo;
import com.template.webserver.emailSender.EmailSender;
import com.template.webserver.model.SuspendOrActiveOrSwithAccountTypeModel;
import com.template.webserver.service.interfaces.CentralBankInterface;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.*;

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
                System.out.println(compteIdAndPassword);
                CentralBankData appCompteData = new CentralBankData();
                appCompteData.setNom("appcompte");
                appCompteData.setEmail(centralBankAccountInfo.getCentralBankData().getEmail());
                appCompteData.setPays(centralBankAccountInfo.getCentralBankData().getPays());
                appCompteData.setAdresse(centralBankAccountInfo.getCentralBankData().getAdresse());
                appCompteData.setLoiCreation(centralBankAccountInfo.getCentralBankData().getLoiCreation());
                CentralBankAccountInfo appAccountInfo = new CentralBankAccountInfo();
                appAccountInfo.setCentralBankData(appCompteData);
                appAccountInfo.setAccountType("courant");
                appAccountInfo.setSuspend(false);
                superAdmin(appAccountInfo);
                //envoyer le mail de creation
                    EmailFromTo emailFromTo = new EmailFromTo();
                    emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                    emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
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
        exception.printStackTrace();
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
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
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
            if (compteIdAndPassword != null){
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
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
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
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

            if (compteIdAndPassword != null) {
                //envoyer le mail de creation
                EmailFromTo emailFromTo = new EmailFromTo();
                emailFromTo.setEmailFrom("cbdc.talan@gmail.com");
                emailFromTo.setEmailFromPassWord("iwlwwcodmacansfn\n");
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
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public RegulateurMasseMonnetaire defineMasseMonnetaireRegulation(RegulateurMasseMonnetaire regulateurMasseMonnetaire) {
        try {
            RegulateurMasseMonnetaire regulateurMasseMonnetaire1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    RegulateurMasseMonnetaireCreatorFlowInitiator.class, regulateurMasseMonnetaire).getReturnValue().get();
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
    public RegulateurTransactionInterPays defineTransactionInterPaysRegulation(RegulateurTransactionInterPays regulateurTransactionInterPays) {
        try{RegulateurTransactionInterPays regulateurTransactionInterPays1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                RegulateurTransactionInterPaysCreatorFlowInitiator.class, regulateurTransactionInterPays).getReturnValue().get();
            return regulateurTransactionInterPays1;
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
    public RegulateurTransactionLocale defineTransactionLocaleRegulation(RegulateurTransactionLocale regulateurTransactionLocale) {
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
            return regulateurTransactionLocale1;
        }catch (Exception e){
            e.printStackTrace();
        return null;
    }

    }

    public TransactionInterBanks createMoney(TransactionInterbancaire transactionInterBancaire){
        try{
            TransactionInterBanks transactionInterBanks1 = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    EmissionCBDCFlowInitiator.class, transactionInterBancaire).getReturnValue().get();
            return transactionInterBanks1;
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

    public List<CommercialBank> getCommercialBankByCountry(String pays){
            Vault.Page<CommercialBankState> vaultStates = nodeRPCConnection.proxy.vaultQuery(CommercialBankState.class);
            List<StateAndRef<CommercialBankState>> allCommercialBanks = vaultStates.getStates();
            List<CommercialBank> filtered = new ArrayList<>();
            for (StateAndRef<CommercialBankState> commercialbank : allCommercialBanks){
                CommercialBankState state = commercialbank.getState().getData();
                if (state.getCommercialBank().getCommercialBankData().getPays().equals(pays)) {
                    filtered.add(commercialbank.getState().getData().getCommercialBank());
                }
            }
            return filtered;
        }


        public List<CommercialBank> getAllCommercialBanks(){
            Vault.Page<CommercialBankState> vaultStates = nodeRPCConnection.proxy.vaultQuery(CommercialBankState.class);
            List<StateAndRef<CommercialBankState>> allCommercialBanks = vaultStates.getStates();
            if(allCommercialBanks == null){
                return null;
            }
            List<CommercialBank> banks = new ArrayList<>();
            for (StateAndRef<CommercialBankState> commercialbank : allCommercialBanks){
                if (commercialbank.getState() != null && commercialbank.getState().getData() != null && commercialbank.getState().getData().getCommercialBank() != null){
                    if (ifExist(banks, commercialbank.getState().getData().getCommercialBank().getCommercialBankAccounts().get(0).getAccountId())==1){
                        banks = deleteOldRegulation(banks, commercialbank.getState().getData().getCommercialBank());
                    }else{
                        banks.add(commercialbank.getState().getData().getCommercialBank());
                    }
                }

            }

            return banks;
            }

            public int ifExist(List<CommercialBank> bankList ,String sender){
                 if (bankList == null){
                     return 0;
                 }
                for (CommercialBank commercialbank : bankList){
                    for (CommercialBankAccount bankAccount : commercialbank.getCommercialBankAccounts()){
                        if (bankAccount.getAccountId().equals(sender)){
                            return 1;
                        }
                    }
                }
                return 0;
            }

    public List<CommercialBank> deleteOldRegulation (List<CommercialBank> bankList, CommercialBank bankCommercial){
        List<CommercialBank> temp = new ArrayList<>();
        if (bankList != null ) {
            for (int i = 0 ; i<= bankList.size()-1; i++){
                int val = 0;
                for (CommercialBankAccount bankAccount : bankList.get(i).getCommercialBankAccounts()){
                    if (bankAccount.getAccountId().equals(bankCommercial.getCommercialBankAccounts().get(0).getAccountId())){
                    val = 1;
                    }

                }
                if (val == 0){
                    temp.add(bankList.get(i));
                }
            }
            temp.add(bankCommercial);
            return temp;

        }

        return null;
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


        public List<Double> getAllAmountSentByCentralBank(String sender){
        Vault.Page<TransactionInterBanksStates> vaultStates = nodeRPCConnection.proxy.vaultQuery(TransactionInterBanksStates.class);
        List<StateAndRef<TransactionInterBanksStates>> allTxInterBanks = vaultStates.getStates();
        List<Double> filtered = new ArrayList<>();
        for (StateAndRef<TransactionInterBanksStates> txInterbank : allTxInterBanks){
            TransactionInterBanksStates state = txInterbank.getState().getData();
            if (state.getTransactionInterBank().getAccountSender().equals(sender)){
                filtered.add(txInterbank.getState().getData().getTransactionInterBank().getAmountToTransfert());

            }
        }
    return filtered;
    }

    public List<String> getAllDateByCentralBank(String sender){
        Vault.Page<TransactionInterBanksStates> vaultStates = nodeRPCConnection.proxy.vaultQuery(TransactionInterBanksStates.class);
        List<StateAndRef<TransactionInterBanksStates>> allTxInterBanks = vaultStates.getStates();
        List<String> filtered = new ArrayList<>();
        for (StateAndRef<TransactionInterBanksStates> txInterbank : allTxInterBanks){
            TransactionInterBanksStates state = txInterbank.getState().getData();
            if (state.getTransactionInterBank().getAccountSender().equals(sender)){
                filtered.add(txInterbank.getState().getData().getTransactionInterBank().getDate());

            }
        }
        return filtered;

    }

    public List<TransactionInterBanks> getAlltxByCentralBank(String sender){

        try {
            List<TransactionInterBanks> transactions = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    CentralBankReadAllTxByCBFlowInitiator.class,sender).getReturnValue().get();
            return transactions;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
//        Vault.Page<TransactionInterBanksStates> vaultStates = nodeRPCConnection.proxy.vaultQuery(TransactionInterBanksStates.class);
//        List<StateAndRef<TransactionInterBanksStates>> allTxInterBanks = vaultStates.getStates();
//        List<TransactionInterBanks> filtered = new ArrayList<>();
//        for (StateAndRef<TransactionInterBanksStates> txInterbank : allTxInterBanks){
//            if (txInterbank != null && txInterbank.getState() != null && txInterbank.getState().getData() != null ){
//                TransactionInterBanksStates state = txInterbank.getState().getData();
//                System.out.println("state.getTransactionInterBank().getAccountSender()"+state.getTransactionInterBank().getAccountSender());
//                if (state.getTransactionInterBank().getAccountSender().equals(sender) ){
//                    filtered.add(state.getTransactionInterBank());
//                }
//            }
//        }
//        System.out.println(filtered);
//        return filtered;

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


    }
