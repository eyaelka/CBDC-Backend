package com.template.flows.model;

import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.RetailTransactions;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.endUserStates.EndUserState;
import com.template.states.merchantStates.MerchantState;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import com.template.states.transactionsStates.RetailTransactionsStates;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.ServiceHub;
import net.corda.core.serialization.CordaSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CordaSerializable
public class CommonTreatment {

    //fonction qui retourne un objet contenant l'emetteur de la transaction

    public static Object getSenderObject(String account, String password, ServiceHub serviceHub){

        if (account.endsWith("bc")) {
            List<StateAndRef<CentralBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
            CentralBank centralBank = null;
            for (int i = 0; i < stateAndRefList.size(); i++) {
                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                    if (centralBankAccount1.getAccountId().equals(account) &&
                            centralBankAccount1.getPassword().equals(password)) {
                        centralBank = new CentralBank();
                        centralBank.setCentralBankData(centralBank1.getCentralBankData());
                        centralBank.getCentralBankAccount().add(centralBankAccount1);
                    }
                }
            }
            return centralBank;
        }else if (account.endsWith("cb")) {
            List<StateAndRef<CommercialBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
            CommercialBank commercialBank = null;
            for (int i = stateAndRefList.size()-1; i >0; i--) {
                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
                    if (commercialBankAccount.getAccountId().equals(account) &&
                            commercialBankAccount.getPassword().equals(password)) {
                        commercialBank = new CommercialBank();
                        commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
                        return commercialBank;
                    }
                }
            }
            return null;
        }else if (account.endsWith("cb")) {
            List<StateAndRef<MerchantState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
            Merchant merchant = null;
            for (int i = stateAndRefList.size()-1; i >0; i--) {
                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
                    if (merchantAccount.getAccountId().equals(account) &&
                            merchantAccount.getPassword().equals(password)) {
                        merchant = new Merchant();
                        merchant.setMerchantData(merchant1.getMerchantData());
                        merchant.getMerchantAccounts().add(merchantAccount);
                        return merchant;
                    }
                }
            }
            return null;
        }else if (account.endsWith("us")) {
            List<StateAndRef<EndUserState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
            EndUser endUser = null;
            for (int i = stateAndRefList.size()-1; i > 0; i--) {
                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
                    if (endUserAccount.getAccountId().equals(account) &&
                            endUserAccount.getPassword().equals(password)) {
                        endUser = new EndUser();
                        endUser.setEndUserData(endUser1.getEndUserData());
                        endUser.getEndUserAccounts().add(endUserAccount);
                        return endUser;
                    }
                }
            }
            return null;
        }
        return null;
    }

    //fonction qui retourne un objet contenant le recepteur de la transaction

    public static Object getReceiverObject(String account,ServiceHub serviceHub){

        if (account.endsWith("bc")) {
            List<StateAndRef<CentralBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
            CentralBank centralBank = null;
            for (int i = 0; i < stateAndRefList.size(); i++) {
                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                    if (centralBankAccount1.getAccountId().equals(account)) {
                        centralBank = new CentralBank();
                        centralBank.setCentralBankData(centralBank1.getCentralBankData());
                        centralBank.getCentralBankAccount().add(centralBankAccount1);
                    }
                }
            }
            return centralBank;
        }else if (account.endsWith("cb")) {
            List<StateAndRef<CommercialBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
            CommercialBank commercialBank = null;
            for (int i = stateAndRefList.size()-1; i > 0; i--) {
                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
                    if (commercialBankAccount.getAccountId().equals(account)) {
                        commercialBank = new CommercialBank();
                        commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
                        return commercialBank;
                    }
                }
            }
            return null;
        }else if (account.endsWith("cb")) {
            List<StateAndRef<MerchantState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
            Merchant merchant = null;
            for (int i = stateAndRefList.size()-1; i >0; i--) {
                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
                    if (merchantAccount.getAccountId().equals(account)) {
                        merchant = new Merchant();
                        merchant.setMerchantData(merchant1.getMerchantData());
                        merchant.getMerchantAccounts().add(merchantAccount);
                        return merchant;
                    }
                }
            }
            return null;
        }else if (account.endsWith("us")) {
            List<StateAndRef<EndUserState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
            EndUser endUser = null;
            for (int i = stateAndRefList.size()-1; i >0; i--) {
                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
                    if (endUserAccount.getAccountId().equals(account)) {
                        endUser = new EndUser();
                        endUser.setEndUserData(endUser1.getEndUserData());
                        endUser.getEndUserAccounts().add(endUserAccount);
                        return endUser;
                    }
                }
            }
            return null;
        }
        return null;
    }

    // fonction qui retourne la dernière regulation des transferts locales
    // i.e emetteur et recepteur appartient au meme pays

    public static RegulateurTransactionLocale getRegulateurTransactionLocale(String pays, ServiceHub serviceHub) {
        List<StateAndRef<RegulateurTransactionLocaleStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();
        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
            if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPays().equals(pays)) {
                return stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale();
            }
        }
        return null;
    }

    // fonction qui retourne la dernière regulation des transferts inter pays
    // i.e emetteur et recepteur appartient au pays différents

    public static RegulateurTransactionInterPays getRegulateurTransactionInterPays(String pays, ServiceHub serviceHub){
        List<StateAndRef<RegulateurTransactionInterPaysStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();
        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
            if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPays().equals(pays)) {
                return stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays();
            }
        }
        return null;
    }

    // fonction qui retourne la dernière regulation de la valeur de devise
    //la fonction prend le pays qui ajouter ce regulateur et le nom de la devise
    public static RegulateurDevise getRegulateurDevise(String pays, String nomDevise,ServiceHub serviceHub ){
        List<StateAndRef<RegulateurDeviseStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurDeviseStates.class).getStates();
        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
            if (stateAndRefList.get(i).getState().getData().getRegulateurDevise().getPays().equalsIgnoreCase(pays) &&
                    stateAndRefList.get(i).getState().getData().getRegulateurDevise().getNom().equalsIgnoreCase(nomDevise)) {
                return stateAndRefList.get(i).getState().getData().getRegulateurDevise();
            }
        }
        return null;
    }

    // fonction qui retourne la dernière regulation des parametres du contrôle de la masse monétaire.

    public static RegulateurMasseMonnetaire getRegulateurMasseMonnetaire(String pays, ServiceHub serviceHub ){
//        List<StateAndRef<RegulateurMasseMonnetaireStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire().getPays().equals(pays)) {
//                return stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire();
//            }
//        }
//        return null;

        List<StateAndRef<RegulateurMasseMonnetaireStates>> allTxLocal =
                serviceHub.getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
        List<RegulateurMasseMonnetaire> filtered = new ArrayList<>();
        for (StateAndRef<RegulateurMasseMonnetaireStates> regulateurMasseMonetaire : allTxLocal){
            RegulateurMasseMonnetaireStates states = regulateurMasseMonetaire.getState().getData();
            if (states.getRegulateurMasseMonnetaire().getPays().equalsIgnoreCase(pays)){
                filtered.add(regulateurMasseMonetaire.getState().getData().getRegulateurMasseMonnetaire());
            }
        }
        return filtered.get(filtered.size()-1);
    }

    // fonction qui retourne l'objet content le montant d'un compte donné.
    public static RetailTransactions getBalanceObject(String account, String pays, ServiceHub serviceHub ){

        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);

        if (retailTransactions1 == null){
            return retailTransactions2;
        }
        if (retailTransactions2 == null){
            return retailTransactions1;
        }
        if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
            return retailTransactions2;
        }
        return retailTransactions1;
    }
    private static RetailTransactions getLastBalanceObjectFromRetailTransactionsStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
        List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();

        if (searchIndex==0) {
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays)) {
                    return stateAndRefList.get(i).getState().getData().getRetailTransactions();
                }
            }
        }else if (searchIndex==1){
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (
                        !stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays)
                ) {
                    return stateAndRefList.get(i).getState().getData().getRetailTransactions();
                }
            }
        }
        return null;
    }

    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné.
    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
    public static Object getCommercialBankBalanceObject(String account, String pays, ServiceHub serviceHub ){
        TransactionInterBanks transactionInterBanks1 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,0);
        TransactionInterBanks transactionInterBanks2 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,1);
        TransactionInterBanks transactionInterBanksDecidedObject;
        if (transactionInterBanks1 == null && transactionInterBanks2 == null){
            transactionInterBanksDecidedObject = null;
        }
        else if (transactionInterBanks1 == null && transactionInterBanks2 != null){
            transactionInterBanksDecidedObject = transactionInterBanks2;
        }
        else if (transactionInterBanks2 == null && transactionInterBanks1 != null){
            transactionInterBanksDecidedObject = transactionInterBanks1;
        }else{
            if (stringCompare(transactionInterBanks1.getDate().toString(),transactionInterBanks2.getDate().toString()) <= 0){
                transactionInterBanksDecidedObject = transactionInterBanks2;
            }else{
                transactionInterBanksDecidedObject = transactionInterBanks1;

            }

        }
        if (transactionInterBanksDecidedObject == null){
            return null;
        }

        //Balance from RetailTransactions
        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);
        RetailTransactions retailTransactionsBanksDecidedObject;
        if (retailTransactions1 == null && retailTransactions2 == null){
            retailTransactionsBanksDecidedObject = null;
        }
        else if (retailTransactions1 == null && retailTransactions2 != null){
            retailTransactionsBanksDecidedObject = retailTransactions2;
        }
        else if (retailTransactions2 == null && retailTransactions1 != null){
            retailTransactionsBanksDecidedObject = retailTransactions1;
        }else{
            if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
                retailTransactionsBanksDecidedObject = retailTransactions2;
            }else{
                retailTransactionsBanksDecidedObject = retailTransactions1;

            }

        }


        if(retailTransactionsBanksDecidedObject == null){
            return transactionInterBanksDecidedObject;
        }
        if(transactionInterBanksDecidedObject == null){
            return retailTransactionsBanksDecidedObject ;
        }
        if (stringCompare(transactionInterBanksDecidedObject.getDate().toString(),retailTransactionsBanksDecidedObject.getDate().toString()) <= 0){
            return retailTransactionsBanksDecidedObject;
        }
        return transactionInterBanksDecidedObject;
    }
    private static TransactionInterBanks getLastBalanceObjectFromTransactionInterBanksStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        if (searchIndex==0) {
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                }
            }
        }else if (searchIndex==1){
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (
                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)
                ) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                }
            }
        }
        return null;
    }

    //Fonction qui retourne un objet banque centrale
    public static CentralBank getCentralBank(String pays, ServiceHub serviceHub){
        List<StateAndRef<CentralBankState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
                    ! stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("admin") &&
                    ! stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
                return stateAndRefList.get(i).getState().getData().getCentralBank();
            }
        }
        return null;
    }

    //Fonction qui retourne le compte appcompte pour verser les frais de l'application
    public static CentralBank getAppCompte(String pays, ServiceHub serviceHub){
        List<StateAndRef<CentralBankState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
        for (int i = stateAndRefList.size() - 1; i >=0; i--) {
            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
                    stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
                return stateAndRefList.get(i).getState().getData().getCentralBank();
            }
        }
        return null;
    }

    // Fonction qui returne le pays de l'emetteur de la transaction afin de savoir
    // si ce pays = au pays du recepteur alors c'est une transaction locale sinon c'est une transaction transfrontalière
    public static String getPaysSender(String account, String password, ServiceHub serviceHub ) {
        List<StateAndRef<EndUserState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(EndUserState.class).getStates();

        String pays = null;
        String bankId = null;
        int out=0;
        for (int i = stateAndRefList.size()-1; i >0; i--) {
            EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
            for (int j = endUser.getEndUserAccounts().size()-1; j >0 ; j--) {
                EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
                if (endUserAccount.getAccountId().equals(account) &&
                        endUserAccount.getPassword().equals(password)) {
                    bankId = endUserAccount.getBankIndcation();
                    out=1;
                    break;
                }
            }
            if (out==1){
                break;
            }
        }
        if (bankId==null)
            return null;
        out=0;
        List<StateAndRef<CommercialBankState>> stateAndRefList1 =
                serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
        for (int i =  stateAndRefList.size()-1; i > 0; i--) {
            CommercialBank commercialBank = stateAndRefList1.get(i).getState().getData().getCommercialBank();
            for (int j = commercialBank.getCommercialBankAccounts().size()-1; j >0; j--) {
                CommercialBankAccount commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
                if (commercialBankAccount.getAccountId().equals(bankId)) {
                    pays = commercialBank.getCommercialBankData().getPays();
                    out=1;
                    break;
                }
            }
            if (out==1)
                break;
        }
        return pays;
    }

    // Fonction qui returne le numero de compte de la banque commerciale de l'emetteur de la transaction
    // afin de verserles frais de la banque commerciale  dans ce compte
    public static String getSenderCommercialBankAccount(String account, String password, ServiceHub serviceHub ) {
        List<StateAndRef<EndUserState>> stateAndRefList = serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
        String bankId = null;
        int out=0;
        for (int i = stateAndRefList.size()-1; i >0; i--) {
            EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
            for (int j = endUser.getEndUserAccounts().size()-1; j >0 ; j--) {
                EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
                if (endUserAccount.getAccountId().equals(account) &&
                        endUserAccount.getPassword().equals(password)) {
                    bankId = endUserAccount.getBankIndcation();
                    out=1;
                    break;
                }
            }
            if (out==1){
                break;
            }
        }
        return bankId;
    }

    //Fonction qui calcule le montant transferé localement depuis une durée jusqu'à maintenant
    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
    public static double getSumAmontTransferedDuringSpecificPeriodOfLocalTX(String account, String pays, int period, ServiceHub serviceHub, int txTypeIndex){
        Date now = new Date();
        long periodeDebutControle = now.getTime()-period;
        Date dateDebutControle = new Date(periodeDebutControle);

        double somme = 0;
        if (txTypeIndex==0){//retailTx in local
            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
                    somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
                }
            }
        }else if(txTypeIndex ==1){//inter bank en local
            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
                    somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                }
            }
        }
        return somme;
    }

    //Fonction qui calcule le montant transferé inter pays depuis une durée jusqu'à maintenant
    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
    // la variable pays ici doit être le pays de l'émetteur de la transaction
    public static double getSumAmontTransferedDuringSpecificPeriodOfTransfrontalier(String account, String pays, int period, ServiceHub serviceHub, int txTypeIndex){
        Date now = new Date();
        long periodeDebutControle = now.getTime()-period;
        Date dateDebutControle = new Date(periodeDebutControle);

        double somme = 0;
        if (txTypeIndex==0){//retailTx in local
            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (! stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
                    somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
                }
            }
        }else if(txTypeIndex ==1){//inter bank en local
            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (! stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
                    somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                }
            }
        }
        return somme;
    }

    //verifier si le montant à transferer est conforme.
    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
    public static double conformiterTransactionLocalTX(double seuilMax, String pays, int periode, double montantATransferer, String account, ServiceHub serviceHub, int txTypeIndex){
        double montantDejaTransfererPendantPeriode =
                getSumAmontTransferedDuringSpecificPeriodOfLocalTX(account, pays, periode,serviceHub,txTypeIndex);
        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
    }

    //verifier si le montant à transferer est conforme entre pays.
    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
    public static double conformiterTransactionInterPays(double seuilMax, String pays, int periode, double montantATransferer, String account, ServiceHub serviceHub, int txTypeIndex){
        double montantDejaTransfererPendantPeriode =
                getSumAmontTransferedDuringSpecificPeriodOfTransfrontalier(account, pays, periode,serviceHub,txTypeIndex);
        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
    }

    //convertisseur
    public static double convertisseurDevise(double montantAConvertir, double tauxConversion){
        double converti = montantAConvertir * tauxConversion;
        //prendre 3 chiffres après la virgule
        int convertiEnInt = (int) (converti*1000);
        return convertiEnInt/1000;
    }

    //preparer le montant de reserve obligatoir
    public static double preparateurMontantDeReserveObligatoir(double montantCourant, double tauxReserveObligatoir){
        return montantCourant*tauxReserveObligatoir;
    }

    // Fonction qui comprare deux string
    public  static  int stringCompare(String str1, String str2){
        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }
        // Edge case for strings like
        // String 1="Geeks" and String 2="Geeksforgeeks"
        if (l1 != l2) {
            return l1 - l2;
        }
        // If none of the above conditions is true,
        // it implies both the strings are equal
        else {
            return 0;
        }
    }

    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné quand elle est considerée comme receiver.
    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
    public static Object getCommercialBankBalanceObjectForReceiving(String account, String pays, ServiceHub serviceHub ){

        //Balance from TransactionInterBanks
        TransactionInterBanks transactionInterBanks1 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,0);
        TransactionInterBanks transactionInterBanks2 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,1);
        TransactionInterBanks transactionInterBanksDecidedObject;
        if (transactionInterBanks1 == null && transactionInterBanks2 == null){
            transactionInterBanksDecidedObject = null;
        }
        else if (transactionInterBanks1 == null && transactionInterBanks2 != null){
            transactionInterBanksDecidedObject = transactionInterBanks2;
        }
        else if (transactionInterBanks2 == null && transactionInterBanks1 != null){
            transactionInterBanksDecidedObject = transactionInterBanks1;
        }else{
            if (stringCompare(transactionInterBanks1.getDate().toString(),transactionInterBanks2.getDate().toString()) <= 0){
         transactionInterBanksDecidedObject = transactionInterBanks2;
            }else{
                transactionInterBanksDecidedObject = transactionInterBanks1;

            }

        }

        //Balance from RetailTransactions
        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);
        RetailTransactions retailTransactionsBanksDecidedObject;
        if (retailTransactions1 == null && retailTransactions2 == null){
            retailTransactionsBanksDecidedObject = null;
        }
        else if (retailTransactions1 == null && retailTransactions2 != null){
            retailTransactionsBanksDecidedObject = retailTransactions2;
        }
        else if (retailTransactions2 == null && retailTransactions1 != null){
            retailTransactionsBanksDecidedObject = retailTransactions1;
        }else{
            if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
                retailTransactionsBanksDecidedObject = retailTransactions2;
            }else{
                retailTransactionsBanksDecidedObject = retailTransactions1;

            }

        }


        if(retailTransactionsBanksDecidedObject == null){
            return transactionInterBanksDecidedObject;
        }
        if(transactionInterBanksDecidedObject == null){
            return retailTransactionsBanksDecidedObject ;
        }
        if (stringCompare(transactionInterBanksDecidedObject.getDate().toString(),retailTransactionsBanksDecidedObject.getDate().toString()) <= 0){
            return retailTransactionsBanksDecidedObject;
        }
        return transactionInterBanksDecidedObject;
    }



    public static TransactionInterBanks getInterbankBalanceObject(String account, String pays, ServiceHub serviceHub ){

        TransactionInterBanks interBankTransactions1 = getLastBalanceObjectFromInterbankTransactionsStates(account,pays, serviceHub,0);
        TransactionInterBanks interBankTransactions2 = getLastBalanceObjectFromInterbankTransactionsStates(account,pays, serviceHub,1);

        if (interBankTransactions1 == null){
            return interBankTransactions2;
        }
        if (interBankTransactions2 == null){
            return interBankTransactions1;
        }
        if (stringCompare(interBankTransactions1.getDate().toString(),interBankTransactions2.getDate().toString()) <= 0){
            return interBankTransactions2;
        }
        return interBankTransactions1;
    }

    private static TransactionInterBanks getLastBalanceObjectFromInterbankTransactionsStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        if (searchIndex==0) {
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                }
            }
        }else if (searchIndex==1){
            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
                if (
                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)
                ) {
                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                }
            }
        }
        return null;
    }

}
