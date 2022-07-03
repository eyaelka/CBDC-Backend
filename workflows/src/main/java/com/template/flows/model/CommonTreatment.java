//package com.template.flows.model;
//
//import com.template.model.centralBank.CentralBank;
//import com.template.model.centralBank.CentralBankAccount;
//import com.template.model.centralBank.CentralBankData;
//import com.template.model.commercialBank.CommercialBank;
//import com.template.model.commercialBank.CommercialBankAccount;
//import com.template.model.commercialBank.CommercialBankData;
//import com.template.model.endUser.EndUser;
//import com.template.model.endUser.EndUserAccount;
//import com.template.model.endUser.EndUserData;
//import com.template.model.merchant.Merchant;
//import com.template.model.merchant.MerchantAccount;
//import com.template.model.merchant.MerchantData;
//import com.template.model.politiquesMonetaires.RegulateurDevise;
//import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
//import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
//import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
//import com.template.model.transactions.RetailTransactions;
//import com.template.model.transactions.TransactionInterBanks;
//import com.template.states.centralBanqueStates.CentralBankState;
//import com.template.states.commercialBankStates.CommercialBankState;
//import com.template.states.endUserStates.EndUserState;
//import com.template.states.merchantStates.MerchantState;
//import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
//import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
//import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
//import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
//import com.template.states.transactionsStates.RetailTransactionsStates;
//import com.template.states.transactionsStates.TransactionInterBanksStates;
//import net.corda.core.contracts.StateAndRef;
//import net.corda.core.node.ServiceHub;
//import net.corda.core.serialization.CordaSerializable;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@CordaSerializable
//public class CommonTreatment {
//
//
//
//    //fonction qui retourne un objet contenant l'emetteur de la transaction
//
//    public static Object getSenderObject(String account, String password, ServiceHub serviceHub){
//
//        if (account.endsWith("bc")) {
//            List<StateAndRef<CentralBankState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
//
//            for (int i = stateAndRefList.size()-1; i >= 0; i--) {
//                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
//                for (int j = centralBank1.getCentralBankAccount().size()-1; j >= 0 ; j--) {
//                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
//                    if (centralBankAccount1.getAccountId().equals(account) &&
//                            centralBankAccount1.getPassword().equals(password)) {
//
//                        CentralBankData centralBankData = new CentralBankData();
//                        centralBankData.setLoiCreation(centralBank1.getCentralBankData().getLoiCreation());
//                        centralBankData.setAdresse(centralBank1.getCentralBankData().getAdresse());
//                        centralBankData.setEmail(centralBank1.getCentralBankData().getEmail());
//                        centralBankData.setNom(centralBank1.getCentralBankData().getNom());
//                        centralBankData.setPays(centralBank1.getCentralBankData().getPays());
//
//                        CentralBankAccount centralBankAccount = new CentralBankAccount();
//                        centralBankAccount.setAccountId(centralBankAccount1.getAccountId());
//                        centralBankAccount.setAccountType(centralBankAccount1.getAccountType());
//                        centralBankAccount.setCRUDDate(centralBankAccount1.getCRUDDate());
//                        centralBankAccount.setSuspend(centralBankAccount1.isSuspend());
//                        centralBankAccount.setPassword(centralBankAccount1.getPassword());
//
//                        CentralBank centralBank = new CentralBank();
//                        centralBank.setCentralBankData(centralBankData);
//                        centralBank.getCentralBankAccount().add(centralBankAccount);
//
//                        return centralBank;
//                    }
//                }
//            }
//        }else if (account.endsWith("cb")) {
//            List<StateAndRef<CommercialBankState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i >0; i--) {
//                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
//                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
//                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
//                    if (commercialBankAccount.getAccountId().equals(account) &&
//                            commercialBankAccount.getPassword().equals(password)) {
//
//                        CommercialBankData commercialBankData = new CommercialBankData();
//                        commercialBankData.setPays(commercialBank1.getCommercialBankData().getPays());
//                        commercialBankData.setAddress(commercialBank1.getCommercialBankData().getAddress());
//                        commercialBankData.setEmail(commercialBank1.getCommercialBankData().getEmail());
//                        commercialBankData.setAbreviation(commercialBank1.getCommercialBankData().getAbreviation());
//                        commercialBankData.setFax(commercialBank1.getCommercialBankData().getFax());
//                        commercialBankData.setName(commercialBank1.getCommercialBankData().getName());
//
//                        CommercialBankAccount commercialBankAccount1 = new CommercialBankAccount();
//                        commercialBankAccount1.setAccountId(commercialBankAccount.getAccountId());
//                        commercialBankAccount1.setAccountType(commercialBankAccount.getAccountType());
//                        commercialBankAccount1.setCRUDDate(commercialBankAccount.getCRUDDate());
//                        commercialBankAccount1.setSuspend(commercialBankAccount.isSuspend());
//                        commercialBankAccount1.setPassword(commercialBankAccount.getPassword());
//
//                        CommercialBank commercialBank = new CommercialBank();
//                        commercialBank.setCommercialBankData(commercialBankData);
//                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount1);
//
//                        return commercialBank;
//                    }
//                }
//            }
//        }else if (account.endsWith("me")) {
//            List<StateAndRef<MerchantState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i >0; i--) {
//                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
//                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
//                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
//                    if (merchantAccount.getAccountId().equals(account) &&
//                            merchantAccount.getPassword().equals(password)) {
//
//                        MerchantData merchantData = new MerchantData();
//                        merchantData.setAgreement(merchant1.getMerchantData().getAgreement());
//                        merchantData.setAddress(merchant1.getMerchantData().getAddress());
//                        merchantData.setEmail(merchant1.getMerchantData().getEmail());;
//                        merchantData.setBusinessName(merchant1.getMerchantData().getBusinessName());
//                        merchantData.setBusinessType(merchant1.getMerchantData().getBusinessType());
//
//                        MerchantAccount merchantAccount1 = new MerchantAccount();
//                        merchantAccount1.setAccountId(merchantAccount.getAccountId());
//                        merchantAccount1.setAccountType(merchantAccount.getAccountType());
//                        merchantAccount1.setCRUDDate(merchantAccount.getCRUDDate());
//                        merchantAccount1.setSuspend(merchantAccount.isSuspend());
//                        merchantAccount1.setPassword(merchantAccount.getPassword());
//
//                        Merchant merchant = new Merchant();
//                        merchant.setMerchantData(merchantData);
//                        merchant.getMerchantAccounts().add(merchantAccount1);
//
//                        return merchant;
//                    }
//                }
//            }
//        }else if (account.endsWith("us")) {
//            List<StateAndRef<EndUserState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i > 0; i--) {
//                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
//                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
//                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
//                    if (endUserAccount.getAccountId().equals(account) &&
//                            endUserAccount.getPassword().equals(password)) {
//
//                        EndUserData endUserData = new EndUserData();
//
//                        endUserData.setNom(endUser1.getEndUserData().getNom());
//                        endUserData.setAdresse(endUser1.getEndUserData().getAdresse());
//                        endUserData.setEmail(endUser1.getEndUserData().getEmail());
//                        endUserData.setCin(endUser1.getEndUserData().getCin());
//                        endUserData.setBankWhoAddUser(endUser1.getEndUserData().getBankWhoAddUser());
//                        endUserData.setDateNaissance(endUser1.getEndUserData().getDateNaissance());
//                        endUserData.setNationalite(endUser1.getEndUserData().getNationalite());
//                        endUserData.setTelephone(endUser1.getEndUserData().getTelephone());
//
//                        EndUserAccount endUserAccount1 = new EndUserAccount();
//                        endUserAccount1.setAccountId(endUserAccount.getAccountId());
//                        endUserAccount1.setAccountType(endUserAccount.getAccountType());
//                        endUserAccount1.setCRUDDate(endUserAccount.getCRUDDate());
//                        endUserAccount1.setSuspend(endUserAccount.isSuspend());
//                        endUserAccount1.setPassword(endUserAccount.getPassword());
//
//                        EndUser endUser = new EndUser();
//                        endUser.setEndUserData(endUserData);
//                        endUser.getEndUserAccounts().add(endUserAccount1);
//
//                        return endUser;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    //fonction qui retourne un objet contenant le recepteur de la transaction
//    public static Object getReceiverObject(String account, ServiceHub serviceHub){
//
//        if (account.endsWith("bc")) {
//            List<StateAndRef<CentralBankState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
//
//            for (int i = stateAndRefList.size()-1; i >0; i--) {
//                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
//                for (int j = centralBank1.getCentralBankAccount().size()-1; j > 0 ; j--) {
//                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
//                    if (centralBankAccount1.getAccountId().equals(account)) {
//
//                        CentralBankData centralBankData = new CentralBankData();
//                        centralBankData.setLoiCreation(centralBank1.getCentralBankData().getLoiCreation());
//                        centralBankData.setAdresse(centralBank1.getCentralBankData().getAdresse());
//                        centralBankData.setEmail(centralBank1.getCentralBankData().getEmail());
//                        centralBankData.setNom(centralBank1.getCentralBankData().getNom());
//                        centralBankData.setPays(centralBank1.getCentralBankData().getPays());
//
//                        CentralBankAccount centralBankAccount = new CentralBankAccount();
//                        centralBankAccount.setAccountId(centralBankAccount1.getAccountId());
//                        centralBankAccount.setAccountType(centralBankAccount1.getAccountType());
//                        centralBankAccount.setCRUDDate(centralBankAccount1.getCRUDDate());
//                        centralBankAccount.setSuspend(centralBankAccount1.isSuspend());
//                        centralBankAccount.setPassword(centralBankAccount1.getPassword());
//
//                        CentralBank centralBank = new CentralBank();
//                        centralBank.setCentralBankData(centralBankData);
//                        centralBank.getCentralBankAccount().add(centralBankAccount);
//
//                        return centralBank;
//                    }
//                }
//            }
//        }else if (account.endsWith("cb")) {
//            List<StateAndRef<CommercialBankState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i >0; i--) {
//                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
//                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
//                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
//                    if (commercialBankAccount.getAccountId().equals(account)) {
//
//                        CommercialBankData commercialBankData = new CommercialBankData();
//                        commercialBankData.setPays(commercialBank1.getCommercialBankData().getPays());
//                        commercialBankData.setAddress(commercialBank1.getCommercialBankData().getAddress());
//                        commercialBankData.setEmail(commercialBank1.getCommercialBankData().getEmail());
//                        commercialBankData.setAbreviation(commercialBank1.getCommercialBankData().getAbreviation());
//                        commercialBankData.setFax(commercialBank1.getCommercialBankData().getFax());
//                        commercialBankData.setName(commercialBank1.getCommercialBankData().getName());
//
//                        CommercialBankAccount commercialBankAccount1 = new CommercialBankAccount();
//                        commercialBankAccount1.setAccountId(commercialBankAccount.getAccountId());
//                        commercialBankAccount1.setAccountType(commercialBankAccount.getAccountType());
//                        commercialBankAccount1.setCRUDDate(commercialBankAccount.getCRUDDate());
//                        commercialBankAccount1.setSuspend(commercialBankAccount.isSuspend());
//                        commercialBankAccount1.setPassword(commercialBankAccount.getPassword());
//
//                        CommercialBank commercialBank = new CommercialBank();
//                        commercialBank.setCommercialBankData(commercialBankData);
//                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount1);
//
//
//                        return commercialBank;
//                    }
//                }
//            }
//        }else if (account.endsWith("me")) {
//            List<StateAndRef<MerchantState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i >0; i--) {
//                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
//                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
//                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
//                    if (merchantAccount.getAccountId().equals(account)) {
//
//                        MerchantData merchantData = new MerchantData();
//                        merchantData.setAgreement(merchant1.getMerchantData().getAgreement());
//                        merchantData.setAddress(merchant1.getMerchantData().getAddress());
//                        merchantData.setEmail(merchant1.getMerchantData().getEmail());;
//                        merchantData.setBusinessName(merchant1.getMerchantData().getBusinessName());
//                        merchantData.setBusinessType(merchant1.getMerchantData().getBusinessType());
//
//                        MerchantAccount merchantAccount1 = new MerchantAccount();
//                        merchantAccount1.setAccountId(merchantAccount.getAccountId());
//                        merchantAccount1.setAccountType(merchantAccount.getAccountType());
//                        merchantAccount1.setCRUDDate(merchantAccount.getCRUDDate());
//                        merchantAccount1.setSuspend(merchantAccount.isSuspend());
//                        merchantAccount1.setPassword(merchantAccount.getPassword());
//
//                        Merchant merchant = new Merchant();
//                        merchant.setMerchantData(merchantData);
//                        merchant.getMerchantAccounts().add(merchantAccount1);
//
//
//                        return merchant;
//                    }
//                }
//            }
//        }else if (account.endsWith("us")) {
//            List<StateAndRef<EndUserState>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
//            for (int i = stateAndRefList.size()-1; i > 0; i--) {
//                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
//                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
//                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
//                    if (endUserAccount.getAccountId().equals(account)) {
//
//                        EndUserData endUserData = new EndUserData();
//
//                        endUserData.setNom(endUser1.getEndUserData().getNom());
//                        endUserData.setAdresse(endUser1.getEndUserData().getAdresse());
//                        endUserData.setEmail(endUser1.getEndUserData().getEmail());
//                        endUserData.setCin(endUser1.getEndUserData().getCin());
//                        endUserData.setBankWhoAddUser(endUser1.getEndUserData().getBankWhoAddUser());
//                        endUserData.setDateNaissance(endUser1.getEndUserData().getDateNaissance());
//                        endUserData.setNationalite(endUser1.getEndUserData().getNationalite());
//                        endUserData.setTelephone(endUser1.getEndUserData().getTelephone());
//
//                        EndUserAccount endUserAccount1 = new EndUserAccount();
//                        endUserAccount1.setAccountId(endUserAccount.getAccountId());
//                        endUserAccount1.setAccountType(endUserAccount.getAccountType());
//                        endUserAccount1.setCRUDDate(endUserAccount.getCRUDDate());
//                        endUserAccount1.setSuspend(endUserAccount.isSuspend());
//                        endUserAccount1.setPassword(endUserAccount.getPassword());
//
//                        EndUser endUser = new EndUser();
//                        endUser.setEndUserData(endUserData);
//                        endUser.getEndUserAccounts().add(endUserAccount1);
//
//                        return endUser;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//
//
//    //fonction qui retourne un objet contenant l'emetteur de la transaction
//
////    public static Object getSenderObject(String account, String password, ServiceHub serviceHub){
////
////        if (account.endsWith("bc")) {
////            List<StateAndRef<CentralBankState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
////            CentralBank centralBank = null;
////            for (int i = 0; i < stateAndRefList.size(); i++) {
////                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
////                for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
////                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
////                    if (centralBankAccount1.getAccountId().equals(account) &&
////                            centralBankAccount1.getPassword().equals(password)) {
////                        centralBank = new CentralBank();
////                        centralBank.setCentralBankData(centralBank1.getCentralBankData());
////                        centralBank.getCentralBankAccount().add(centralBankAccount1);
////                    }
////                }
////            }
////            return centralBank;
////        }else if (account.endsWith("cb")) {
////            List<StateAndRef<CommercialBankState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
////            CommercialBank commercialBank = null;
////            for (int i = stateAndRefList.size()-1; i >0; i--) {
////                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
////                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
////                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
////                    if (commercialBankAccount.getAccountId().equals(account) &&
////                            commercialBankAccount.getPassword().equals(password)) {
////                        commercialBank = new CommercialBank();
////                        commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
////                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
////                        return commercialBank;
////                    }
////                }
////            }
////            return null;
////        }else if (account.endsWith("cb")) {
////            List<StateAndRef<MerchantState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
////            Merchant merchant = null;
////            for (int i = stateAndRefList.size()-1; i >0; i--) {
////                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
////                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
////                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
////                    if (merchantAccount.getAccountId().equals(account) &&
////                            merchantAccount.getPassword().equals(password)) {
////                        merchant = new Merchant();
////                        merchant.setMerchantData(merchant1.getMerchantData());
////                        merchant.getMerchantAccounts().add(merchantAccount);
////                        return merchant;
////                    }
////                }
////            }
////            return null;
////        }else if (account.endsWith("us")) {
////            List<StateAndRef<EndUserState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
////            EndUser endUser = null;
////            for (int i = stateAndRefList.size()-1; i > 0; i--) {
////                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
////                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
////                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
////                    if (endUserAccount.getAccountId().equals(account) &&
////                            endUserAccount.getPassword().equals(password)) {
////                        endUser = new EndUser();
////                        endUser.setEndUserData(endUser1.getEndUserData());
////                        endUser.getEndUserAccounts().add(endUserAccount);
////                        return endUser;
////                    }
////                }
////            }
////            return null;
////        }
////        return null;
////    }
////
////    //fonction qui retourne un objet contenant le recepteur de la transaction
////
////    public static Object getReceiverObject(String account,ServiceHub serviceHub){
////
////        if (account.endsWith("bc")) {
////            List<StateAndRef<CentralBankState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
////            CentralBank centralBank = null;
////            for (int i = 0; i < stateAndRefList.size(); i++) {
////                CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
////                for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
////                    CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
////                    if (centralBankAccount1.getAccountId().equals(account)) {
////                        centralBank = new CentralBank();
////                        centralBank.setCentralBankData(centralBank1.getCentralBankData());
////                        centralBank.getCentralBankAccount().add(centralBankAccount1);
////                    }
////                }
////            }
////            return centralBank;
////        }else if (account.endsWith("cb")) {
////            List<StateAndRef<CommercialBankState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
////            CommercialBank commercialBank = null;
////            for (int i = stateAndRefList.size()-1; i > 0; i--) {
////                CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
////                for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j > 0 ; j--) {
////                    CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
////                    if (commercialBankAccount.getAccountId().equals(account)) {
////                        commercialBank = new CommercialBank();
////                        commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
////                        commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
////                        return commercialBank;
////                    }
////                }
////            }
////            return null;
////        }else if (account.endsWith("cb")) {
////            List<StateAndRef<MerchantState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();
////            Merchant merchant = null;
////            for (int i = stateAndRefList.size()-1; i >0; i--) {
////                Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
////                for (int j = merchant1.getMerchantAccounts().size()-1; j > 0 ; j--) {
////                    MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
////                    if (merchantAccount.getAccountId().equals(account)) {
////                        merchant = new Merchant();
////                        merchant.setMerchantData(merchant1.getMerchantData());
////                        merchant.getMerchantAccounts().add(merchantAccount);
////                        return merchant;
////                    }
////                }
////            }
////            return null;
////        }else if (account.endsWith("us")) {
////            List<StateAndRef<EndUserState>> stateAndRefList =
////                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
////            EndUser endUser = null;
////            for (int i = stateAndRefList.size()-1; i >0; i--) {
////                EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
////                for (int j = endUser1.getEndUserAccounts().size()-1; j > 0 ; j--) {
////                    EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
////                    if (endUserAccount.getAccountId().equals(account)) {
////                        endUser = new EndUser();
////                        endUser.setEndUserData(endUser1.getEndUserData());
////                        endUser.getEndUserAccounts().add(endUserAccount);
////                        return endUser;
////                    }
////                }
////            }
////            return null;
////        }
////        return null;
////    }
//
//    // fonction qui retourne la dernière regulation des transferts locales
//    // i.e emetteur et recepteur appartient au meme pays
//
//    public static RegulateurTransactionLocale getRegulateurTransactionLocale(String pays, ServiceHub serviceHub) {
//        List<StateAndRef<RegulateurTransactionLocaleStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPays().equals(pays)) {
//                return stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale();
//            }
//        }
//        return null;
//    }
//
//    // fonction qui retourne la dernière regulation des transferts inter pays
//    // i.e emetteur et recepteur appartient au pays différents
//
//    public static RegulateurTransactionInterPays getRegulateurTransactionInterPays(String pays, ServiceHub serviceHub){
//        List<StateAndRef<RegulateurTransactionInterPaysStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPays().equals(pays)) {
//                return stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays();
//            }
//        }
//        return null;
//    }
//
//    // fonction qui retourne la dernière regulation de la valeur de devise
//    //la fonction prend le pays qui ajouter ce regulateur et le nom de la devise
//    public static RegulateurDevise getRegulateurDevise(String pays, String nomDevise,ServiceHub serviceHub ){
//        List<StateAndRef<RegulateurDeviseStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(RegulateurDeviseStates.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getRegulateurDevise().getPays().equalsIgnoreCase(pays) &&
//                    stateAndRefList.get(i).getState().getData().getRegulateurDevise().getNom().equalsIgnoreCase(nomDevise)) {
//                return stateAndRefList.get(i).getState().getData().getRegulateurDevise();
//            }
//        }
//        return null;
//    }
//
//    // fonction qui retourne la dernière regulation des parametres du contrôle de la masse monétaire.
//
//    public static RegulateurMasseMonnetaire getRegulateurMasseMonnetaire(String pays, ServiceHub serviceHub ){
////        List<StateAndRef<RegulateurMasseMonnetaireStates>> stateAndRefList =
////                serviceHub.getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
////        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
////            if (stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire().getPays().equals(pays)) {
////                return stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire();
////            }
////        }
////        return null;
//
//        List<StateAndRef<RegulateurMasseMonnetaireStates>> allTxLocal =
//                serviceHub.getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
//        List<RegulateurMasseMonnetaire> filtered = new ArrayList<>();
//        for (StateAndRef<RegulateurMasseMonnetaireStates> regulateurMasseMonetaire : allTxLocal){
//            RegulateurMasseMonnetaireStates states = regulateurMasseMonetaire.getState().getData();
//            if (states.getRegulateurMasseMonnetaire().getPays().equalsIgnoreCase(pays)){
//                filtered.add(regulateurMasseMonetaire.getState().getData().getRegulateurMasseMonnetaire());
//            }
//        }
//        return filtered.get(filtered.size()-1);
//    }
//
//    // fonction qui retourne l'objet content le montant d'un compte donné.
//    public static RetailTransactions getBalanceObject(String account, String pays, ServiceHub serviceHub ){
//
//        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
//        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);
//
//        if (retailTransactions1 == null){
//            return retailTransactions2;
//        }
//        if (retailTransactions2 == null){
//            return retailTransactions1;
//        }
//        if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
//            return retailTransactions2;
//        }
//        return retailTransactions1;
//    }
//    private static RetailTransactions getLastBalanceObjectFromRetailTransactionsStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
//        List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();
//
//        if (searchIndex==0) {
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays)) {
//                    return stateAndRefList.get(i).getState().getData().getRetailTransactions();
//                }
//            }
//        }else if (searchIndex==1){
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (
//                        !stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays)
//                ) {
//                    return stateAndRefList.get(i).getState().getData().getRetailTransactions();
//                }
//            }
//        }
//        return null;
//    }
//
//    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné.
//    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
//    public static Object getCommercialBankBalanceObject(String account, String pays, ServiceHub serviceHub ){
//        TransactionInterBanks transactionInterBanks1 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,0);
//        TransactionInterBanks transactionInterBanks2 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,1);
//        TransactionInterBanks transactionInterBanksDecidedObject;
//        if (transactionInterBanks1 == null && transactionInterBanks2 == null){
//            transactionInterBanksDecidedObject = null;
//        }
//        else if (transactionInterBanks1 == null && transactionInterBanks2 != null){
//            transactionInterBanksDecidedObject = transactionInterBanks2;
//        }
//        else if (transactionInterBanks2 == null && transactionInterBanks1 != null){
//            transactionInterBanksDecidedObject = transactionInterBanks1;
//        }else{
//            if (stringCompare(transactionInterBanks1.getDate().toString(),transactionInterBanks2.getDate().toString()) <= 0){
//                transactionInterBanksDecidedObject = transactionInterBanks2;
//            }else{
//                transactionInterBanksDecidedObject = transactionInterBanks1;
//
//            }
//
//        }
//        if (transactionInterBanksDecidedObject == null){
//            return null;
//        }
//
//        //Balance from RetailTransactions
//        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
//        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);
//        RetailTransactions retailTransactionsBanksDecidedObject;
//        if (retailTransactions1 == null && retailTransactions2 == null){
//            retailTransactionsBanksDecidedObject = null;
//        }
//        else if (retailTransactions1 == null && retailTransactions2 != null){
//            retailTransactionsBanksDecidedObject = retailTransactions2;
//        }
//        else if (retailTransactions2 == null && retailTransactions1 != null){
//            retailTransactionsBanksDecidedObject = retailTransactions1;
//        }else{
//            if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
//                retailTransactionsBanksDecidedObject = retailTransactions2;
//            }else{
//                retailTransactionsBanksDecidedObject = retailTransactions1;
//
//            }
//
//        }
//
//
//        if(retailTransactionsBanksDecidedObject == null){
//            return transactionInterBanksDecidedObject;
//        }
//        if(transactionInterBanksDecidedObject == null){
//            return retailTransactionsBanksDecidedObject ;
//        }
//        if (stringCompare(transactionInterBanksDecidedObject.getDate().toString(),retailTransactionsBanksDecidedObject.getDate().toString()) <= 0){
//            return retailTransactionsBanksDecidedObject;
//        }
//        return transactionInterBanksDecidedObject;
//    }
//    private static TransactionInterBanks getLastBalanceObjectFromTransactionInterBanksStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
//        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
//
//        if (searchIndex==0) {
//            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
//                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equalsIgnoreCase(pays)) {
//                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
//                }
//            }
//        }else if (searchIndex==1){
//            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
//                if (
//                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)
//                ) {
//                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
//                }
//            }
//        }
//        return null;
//    }
//
//    //Fonction qui retourne un objet banque centrale
//    public static CentralBank getCentralBank(String pays, ServiceHub serviceHub){
//        List<StateAndRef<CentralBankState>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
//                    ! stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("admin") &&
//                    ! stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
//                return stateAndRefList.get(i).getState().getData().getCentralBank();
//            }
//        }
//        return null;
//    }
//
//    //Fonction qui retourne le compte appcompte pour verser les frais de l'application
//    public static CentralBank getAppCompte(String pays, ServiceHub serviceHub){
//        List<StateAndRef<CentralBankState>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
//        for (int i = stateAndRefList.size() - 1; i >=0; i--) {
//            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
//                    stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
//                return stateAndRefList.get(i).getState().getData().getCentralBank();
//            }
//        }
//        return null;
//    }
//
//    // Fonction qui returne le pays de l'emetteur de la transaction afin de savoir
//    // si ce pays = au pays du recepteur alors c'est une transaction locale sinon c'est une transaction transfrontalière
//    public static String getPaysSender(String account, String password, ServiceHub serviceHub ) {
//        List<StateAndRef<EndUserState>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
//
//        String pays = null;
//        String bankId = null;
//        int out=0;
//        for (int i = stateAndRefList.size()-1; i >0; i--) {
//            EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
//            for (int j = endUser.getEndUserAccounts().size()-1; j >0 ; j--) {
//                EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
//                if (endUserAccount.getAccountId().equals(account) &&
//                        endUserAccount.getPassword().equals(password)) {
//                    bankId = endUserAccount.getBankIndcation();
//                    out=1;
//                    break;
//                }
//            }
//            if (out==1){
//                break;
//            }
//        }
//        if (bankId==null)
//            return null;
//        out=0;
//        List<StateAndRef<CommercialBankState>> stateAndRefList1 =
//                serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();
//        for (int i =  stateAndRefList.size()-1; i > 0; i--) {
//            CommercialBank commercialBank = stateAndRefList1.get(i).getState().getData().getCommercialBank();
//            for (int j = commercialBank.getCommercialBankAccounts().size()-1; j >0; j--) {
//                CommercialBankAccount commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
//                if (commercialBankAccount.getAccountId().equals(bankId)) {
//                    pays = commercialBank.getCommercialBankData().getPays();
//                    out=1;
//                    break;
//                }
//            }
//            if (out==1)
//                break;
//        }
//        return pays;
//    }
//
//    // Fonction qui returne le numero de compte de la banque commerciale de l'emetteur de la transaction
//    // afin de verserles frais de la banque commerciale  dans ce compte
//    public static String getSenderCommercialBankAccount(String account, String password, ServiceHub serviceHub ) {
//        List<StateAndRef<EndUserState>> stateAndRefList = serviceHub.getVaultService().queryBy(EndUserState.class).getStates();
//        String bankId = null;
//        int out=0;
//        for (int i = stateAndRefList.size()-1; i >0; i--) {
//            EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
//            for (int j = endUser.getEndUserAccounts().size()-1; j >0 ; j--) {
//                EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
//                if (endUserAccount.getAccountId().equals(account) &&
//                        endUserAccount.getPassword().equals(password)) {
//                    bankId = endUserAccount.getBankIndcation();
//                    out=1;
//                    break;
//                }
//            }
//            if (out==1){
//                break;
//            }
//        }
//        return bankId;
//    }
//
//    //Fonction qui calcule le montant transferé localement depuis une durée jusqu'à maintenant
//    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
//    public static double getSumAmontTransferedDuringSpecificPeriodOfLocalTX(String account, String pays, int period, ServiceHub serviceHub, int txTypeIndex){
//        Date now = new Date();
//        long periodeDebutControle = now.getTime()-period;
//        Date dateDebutControle = new Date(periodeDebutControle);
//
//        double somme = 0;
//        if (txTypeIndex==0){//retailTx in local
//            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
//                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
//                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
//                    somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
//                }
//            }
//        }else if(txTypeIndex ==1){//inter bank en local
//            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
//                    somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
//                }
//            }
//        }
//        return somme;
//    }
//
//    //Fonction qui calcule le montant transferé inter pays depuis une durée jusqu'à maintenant
//    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
//    // la variable pays ici doit être le pays de l'émetteur de la transaction
//    public static double getSumAmontTransferedDuringSpecificPeriodOfTransfrontalier(String account, String pays, int period, ServiceHub serviceHub, int txTypeIndex){
//        Date now = new Date();
//        long periodeDebutControle = now.getTime()-period;
//        Date dateDebutControle = new Date(periodeDebutControle);
//
//        double somme = 0;
//        if (txTypeIndex==0){//retailTx in local
//            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (! stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
//                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
//                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
//                    somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
//                }
//            }
//        }else if(txTypeIndex ==1){//inter bank en local
//            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
//                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (! stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                        stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
//                    somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
//                }
//            }
//        }
//        return somme;
//    }
//
//    //verifier si le montant à transferer est conforme.
//    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
//    public static double conformiterTransactionLocalTX(double seuilMax, String pays, int periode, double montantATransferer, String account, ServiceHub serviceHub, int txTypeIndex){
//        double montantDejaTransfererPendantPeriode =
//                getSumAmontTransferedDuringSpecificPeriodOfLocalTX(account, pays, periode,serviceHub,txTypeIndex);
//        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
//    }
//
//    //verifier si le montant à transferer est conforme entre pays.
//    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
//    public static double conformiterTransactionInterPays(double seuilMax, String pays, int periode, double montantATransferer, String account, ServiceHub serviceHub, int txTypeIndex){
//        double montantDejaTransfererPendantPeriode =
//                getSumAmontTransferedDuringSpecificPeriodOfTransfrontalier(account, pays, periode,serviceHub,txTypeIndex);
//        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
//    }
//
//    //convertisseur
//    public static double convertisseurDevise(double montantAConvertir, double tauxConversion){
//        double converti = montantAConvertir * tauxConversion;
//        //prendre 3 chiffres après la virgule
//        int convertiEnInt = (int) (converti*1000);
//        return convertiEnInt/1000;
//    }
//
//    //preparer le montant de reserve obligatoir
//    public static double preparateurMontantDeReserveObligatoir(double montantCourant, double tauxReserveObligatoir){
//        return montantCourant*tauxReserveObligatoir;
//    }
//
//    // Fonction qui comprare deux string
//    public  static  int stringCompare(String str1, String str2){
//        int l1 = str1.length();
//        int l2 = str2.length();
//        int lmin = Math.min(l1, l2);
//
//        for (int i = 0; i < lmin; i++) {
//            int str1_ch = (int)str1.charAt(i);
//            int str2_ch = (int)str2.charAt(i);
//
//            if (str1_ch != str2_ch) {
//                return str1_ch - str2_ch;
//            }
//        }
//        // Edge case for strings like
//        // String 1="Geeks" and String 2="Geeksforgeeks"
//        if (l1 != l2) {
//            return l1 - l2;
//        }
//        // If none of the above conditions is true,
//        // it implies both the strings are equal
//        else {
//            return 0;
//        }
//    }
//
//    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné quand elle est considerée comme receiver.
//    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
//    public static Object getCommercialBankBalanceObjectForReceiving(String account, String pays, ServiceHub serviceHub ){
//
//        //Balance from TransactionInterBanks
//        TransactionInterBanks transactionInterBanks1 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,0);
//        TransactionInterBanks transactionInterBanks2 = getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub,1);
//        TransactionInterBanks transactionInterBanksDecidedObject;
//        if (transactionInterBanks1 == null && transactionInterBanks2 == null){
//            transactionInterBanksDecidedObject = null;
//        }
//        else if (transactionInterBanks1 == null && transactionInterBanks2 != null){
//            transactionInterBanksDecidedObject = transactionInterBanks2;
//        }
//        else if (transactionInterBanks2 == null && transactionInterBanks1 != null){
//            transactionInterBanksDecidedObject = transactionInterBanks1;
//        }else{
//            if (stringCompare(transactionInterBanks1.getDate().toString(),transactionInterBanks2.getDate().toString()) <= 0){
//         transactionInterBanksDecidedObject = transactionInterBanks2;
//            }else{
//                transactionInterBanksDecidedObject = transactionInterBanks1;
//
//            }
//
//        }
//
//        //Balance from RetailTransactions
//        RetailTransactions retailTransactions1 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,0);
//        RetailTransactions retailTransactions2 = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub,1);
//        RetailTransactions retailTransactionsBanksDecidedObject;
//        if (retailTransactions1 == null && retailTransactions2 == null){
//            retailTransactionsBanksDecidedObject = null;
//        }
//        else if (retailTransactions1 == null && retailTransactions2 != null){
//            retailTransactionsBanksDecidedObject = retailTransactions2;
//        }
//        else if (retailTransactions2 == null && retailTransactions1 != null){
//            retailTransactionsBanksDecidedObject = retailTransactions1;
//        }else{
//            if (stringCompare(retailTransactions1.getDate().toString(),retailTransactions2.getDate().toString()) <= 0){
//                retailTransactionsBanksDecidedObject = retailTransactions2;
//            }else{
//                retailTransactionsBanksDecidedObject = retailTransactions1;
//
//            }
//
//        }
//
//
//        if(retailTransactionsBanksDecidedObject == null){
//            return transactionInterBanksDecidedObject;
//        }
//        if(transactionInterBanksDecidedObject == null){
//            return retailTransactionsBanksDecidedObject ;
//        }
//        if (stringCompare(transactionInterBanksDecidedObject.getDate().toString(),retailTransactionsBanksDecidedObject.getDate().toString()) <= 0){
//            return retailTransactionsBanksDecidedObject;
//        }
//        return transactionInterBanksDecidedObject;
//    }
//
//
//
//    public static TransactionInterBanks getInterbankBalanceObject(String account, String pays, ServiceHub serviceHub ){
//
//        TransactionInterBanks interBankTransactions1 = getLastBalanceObjectFromInterbankTransactionsStates(account,pays, serviceHub,0);
//        TransactionInterBanks interBankTransactions2 = getLastBalanceObjectFromInterbankTransactionsStates(account,pays, serviceHub,1);
//
//        if (interBankTransactions1 == null){
//            return interBankTransactions2;
//        }
//        if (interBankTransactions2 == null){
//            return interBankTransactions1;
//        }
//        if (stringCompare(interBankTransactions1.getDate().toString(),interBankTransactions2.getDate().toString()) <= 0){
//            return interBankTransactions2;
//        }
//        return interBankTransactions1;
//    }
//
//    private static TransactionInterBanks getLastBalanceObjectFromInterbankTransactionsStates(String account, String pays, ServiceHub serviceHub, int searchIndex){
//        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
//                serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
//
//        if (searchIndex==0) {
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
//                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)) {
//                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
//                }
//            }
//        }else if (searchIndex==1){
//            for (int i = stateAndRefList.size() - 1; i > 0; i--) {
//                if (
//                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
//                                stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)
//                ) {
//                    return stateAndRefList.get(i).getState().getData().getTransactionInterBank();
//                }
//            }
//        }
//        return null;
//    }
//
//}

package com.template.flows.model;

import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.commercialBank.CommercialBankData;
import com.template.model.endUser.EndUser;
import com.template.model.endUser.EndUserAccount;
import com.template.model.endUser.EndUserData;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.model.merchant.MerchantData;
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

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size() -1 ; i >=0; i--) {
                if ( stateAndRefList.get(i) != null &&  stateAndRefList.get(i).getState() != null &&  stateAndRefList.get(i).getState().getData() != null
                        &&  stateAndRefList.get(i).getState().getData().getCentralBank() != null){
                    CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                    for (int j = centralBank1.getCentralBankAccount().size() -1 ; j >= 0; j--) {
                        CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                        if (centralBankAccount1.getAccountId().equals(account) &&
                                centralBankAccount1.getPassword().equals(password)) {
                            CentralBank centralBank = new CentralBank(new CentralBankData(),new ArrayList<>());
                            centralBank.setCentralBankData(centralBank1.getCentralBankData());
                            centralBank.getCentralBankAccount().add(centralBankAccount1);
                            return centralBank;
                        }
                    }

                }
                            }
        }else if (account.endsWith("cb")) {
            List<StateAndRef<CommercialBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if ( stateAndRefList.get(i) != null &&  stateAndRefList.get(i).getState() != null &&  stateAndRefList.get(i).getState().getData() != null
                        &&  stateAndRefList.get(i).getState().getData().getCommercialBank() != null){
                    CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
                    for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j >= 0 ; j--) {
                        CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
                        if (commercialBankAccount.getAccountId().equals(account) &&
                                commercialBankAccount.getPassword().equals(password)) {
                            CommercialBank commercialBank = new CommercialBank(new CommercialBankData(),new ArrayList<>());
                            commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
                            commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
                            return commercialBank;
                        }
                    }
                }

            }
        }else if (account.endsWith("me")) {
            List<StateAndRef<MerchantState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if ( stateAndRefList.get(i) != null &&  stateAndRefList.get(i).getState() != null &&  stateAndRefList.get(i).getState().getData() != null
                        &&  stateAndRefList.get(i).getState().getData().getMerchant() != null){
                    Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
                    for (int j = merchant1.getMerchantAccounts().size()-1; j >= 0 ; j--) {
                        MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
                        if (merchantAccount.getAccountId().equals(account) &&
                                merchantAccount.getPassword().equals(password)) {
                            Merchant merchant = new Merchant(new MerchantData(), new ArrayList<>());
                            merchant.setMerchantData(merchant1.getMerchantData());
                            merchant.getMerchantAccounts().add(merchantAccount);
                            return merchant;
                        }
                    }
                }

            }
        }else if (account.endsWith("us")) {
            List<StateAndRef<EndUserState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if ( stateAndRefList.get(i) != null &&  stateAndRefList.get(i).getState() != null &&  stateAndRefList.get(i).getState().getData() != null
                        &&  stateAndRefList.get(i).getState().getData().getEndUser() != null){
                    EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
                    for (int j = endUser1.getEndUserAccounts().size()-1; j >= 0 ; j--) {
                        EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
                        if (endUserAccount.getAccountId().equals(account) &&
                                endUserAccount.getPassword().equals(password)) {
                            EndUser endUser = new EndUser(new EndUserData(), new ArrayList<>());
                            endUser.setEndUserData(endUser1.getEndUserData());
                            endUser.getEndUserAccounts().add(endUserAccount);
                            return endUser;
                        }
                    }
                }

            }
        }
        return null;
    }

    //fonction qui retourne un objet contenant le recepteur de la transaction

    public static Object getReceiverObject(String account,ServiceHub serviceHub){

        if (account.endsWith("bc")) {
            List<StateAndRef<CentralBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >= 0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
                && stateAndRefList.get(i).getState().getData().getCentralBank() != null){
                    CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                    for (int j = centralBank1.getCentralBankAccount().size() -1; j >= 0; j--) {
                        CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                        if (centralBankAccount1.getAccountId().equals(account)) {
                            CentralBank centralBank = new CentralBank(new CentralBankData(),new ArrayList<>());
                            centralBank.setCentralBankData(centralBank1.getCentralBankData());
                            centralBank.getCentralBankAccount().add(centralBankAccount1);
                            return centralBank;
                        }
                    }
                }

            }
        }else if (account.endsWith("cb")) {
            List<StateAndRef<CommercialBankState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(CommercialBankState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
                && stateAndRefList.get(i).getState().getData().getCommercialBank() != null){
                    CommercialBank commercialBank1 = stateAndRefList.get(i).getState().getData().getCommercialBank();
                    for (int j = commercialBank1.getCommercialBankAccounts().size()-1; j >= 0 ; j--) {
                        CommercialBankAccount commercialBankAccount = commercialBank1.getCommercialBankAccounts().get(j);
                        if (commercialBankAccount.getAccountId().equals(account)) {
                            CommercialBank commercialBank = new CommercialBank(new CommercialBankData(),new ArrayList<>());
                            commercialBank.setCommercialBankData(commercialBank1.getCommercialBankData());
                            commercialBank.getCommercialBankAccounts().add(commercialBankAccount);
                            return commercialBank;
                        }
                    }
                }

            }
        }else if (account.endsWith("me")) {
            List<StateAndRef<MerchantState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(MerchantState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
                && stateAndRefList.get(i).getState().getData().getMerchant() != null){
                    Merchant merchant1 = stateAndRefList.get(i).getState().getData().getMerchant();
                    for (int j = merchant1.getMerchantAccounts().size()-1; j >= 0 ; j--) {
                        MerchantAccount merchantAccount = merchant1.getMerchantAccounts().get(j);
                        if (merchantAccount.getAccountId().equals(account)) {
                            Merchant merchant = new Merchant(new MerchantData(), new ArrayList<>());
                            merchant.setMerchantData(merchant1.getMerchantData());
                            merchant.getMerchantAccounts().add(merchantAccount);
                            return merchant;
                        }
                    }
                }
            }
        }else if (account.endsWith("us")) {
            List<StateAndRef<EndUserState>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(EndUserState.class).getStates();

            if (stateAndRefList==null){
                return null;
            }

            for (int i = stateAndRefList.size()-1; i >=0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
                && stateAndRefList.get(i).getState().getData().getEndUser() != null){
                    EndUser endUser1 = stateAndRefList.get(i).getState().getData().getEndUser();
                    for (int j = endUser1.getEndUserAccounts().size()-1; j >= 0 ; j--) {
                        EndUserAccount endUserAccount = endUser1.getEndUserAccounts().get(j);
                        if (endUserAccount.getAccountId().equals(account)) {
                            EndUser endUser = new EndUser(new EndUserData(), new ArrayList<>());
                            endUser.setEndUserData(endUser1.getEndUserData());
                            endUser.getEndUserAccounts().add(endUserAccount);
                            return endUser;
                        }
                    }
                }

            }
        }
        return null;
    }

    // fonction qui retourne la dernière regulation des transferts locales
    // i.e emetteur et recepteur appartient au meme pays

    public static RegulateurTransactionLocale getRegulateurTransactionLocale(String pays, ServiceHub serviceHub) {
        List<StateAndRef<RegulateurTransactionLocaleStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();

        if (stateAndRefList==null){
            return null;
        }

        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null &&
                    stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale() != null){
                if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPays().equals(pays)) {

                    RegulateurTransactionLocale regulateurTransactionLocale = new RegulateurTransactionLocale();
                    regulateurTransactionLocale.setMotifRegulation(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionLocale().getMotifRegulation());
                    regulateurTransactionLocale.setPays(stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPays());
                    regulateurTransactionLocale.setDate(stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getDate());
                    regulateurTransactionLocale.setPeriode(stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPeriode());
                    regulateurTransactionLocale.setBorneMinimum(stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getBorneMinimum());
                    regulateurTransactionLocale.setSeuilMaximumAutresTX(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionLocale().getSeuilMaximumAutresTX());
                    regulateurTransactionLocale.setSeuilMaximumInterbank(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionLocale().getSeuilMaximumInterbank());

                    return regulateurTransactionLocale;
                    //return stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale();
                }
            }

        }
        return null;
    }

    // fonction qui retourne la dernière regulation des transferts inter pays
    // i.e emetteur et recepteur appartient au pays différents

    public static RegulateurTransactionInterPays getRegulateurTransactionInterPays(String pays, ServiceHub serviceHub){
        List<StateAndRef<RegulateurTransactionInterPaysStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();

        if (stateAndRefList==null){
            return null;
        }

        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
            && stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays() != null){
                if (stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPays().equals(pays)) {

                    RegulateurTransactionInterPays transactionInterPays = new RegulateurTransactionInterPays();
                    transactionInterPays.setMotifRegulation(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionInterPays().getMotifRegulation());
                    transactionInterPays.setPays(stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPays());
                    transactionInterPays.setDate(stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getDate());
                    transactionInterPays.setPeriode(stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPeriode());
                    transactionInterPays.setBorneMinimum(stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getBorneMinimum());
                    transactionInterPays.setSeuilMaximumAutresTX(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionInterPays().getSeuilMaximumAutresTX());
                    transactionInterPays.setSeuilMaximumInterbank(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionInterPays().getSeuilMaximumInterbank());
                    transactionInterPays.setPaysBanqueCentral(stateAndRefList.get(i).getState().getData()
                            .getRegulateurTransactionInterPays().getPaysBanqueCentral());

                    return transactionInterPays;
                    //return stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays();
                }
            }

        }
        return null;
    }

    // fonction qui retourne la dernière regulation de la valeur de devise
    //la fonction prend le pays qui ajouter ce regulateur et le nom de la devise
    public static RegulateurDevise getRegulateurDevise(String pays, String nomDevise,ServiceHub serviceHub ){
        List<StateAndRef<RegulateurDeviseStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurDeviseStates.class).getStates();

        if (stateAndRefList == null){
            return null;
        }

        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getRegulateurDevise() != null){
                if (stateAndRefList.get(i).getState().getData().getRegulateurDevise().getPays().equalsIgnoreCase(pays) &&
                        stateAndRefList.get(i).getState().getData().getRegulateurDevise().getNom().equalsIgnoreCase(nomDevise)) {

                    RegulateurDevise regulateurDevise = new RegulateurDevise();
                    regulateurDevise.setDate(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getDate());
                    regulateurDevise.setNom(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getNom());
                    regulateurDevise.setPays(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getPays());
                    regulateurDevise.setMotifVariation(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getMotifVariation());
                    regulateurDevise.setTauxAchat(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getTauxAchat());
                    regulateurDevise.setTauxVente(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getTauxVente());

                    return regulateurDevise;
                }
            }

        }
        return null;
    }

    // fonction qui retourne la dernière regulation des parametres du contrôle de la masse monétaire.

    public static RegulateurMasseMonnetaire getRegulateurMasseMonnetaire(String pays, ServiceHub serviceHub ){
        List<StateAndRef<RegulateurMasseMonnetaireStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();
        if(stateAndRefList == null){
            return null;
        }

        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire() != null) {
                if (stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire()
                        .getPays().equals(pays)) {

                    RegulateurMasseMonnetaire regulateurMasseMonnetaire =
                            stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire();

                    RegulateurMasseMonnetaire masseMonnetaire = new RegulateurMasseMonnetaire();
                    masseMonnetaire.setDate(regulateurMasseMonnetaire.getDate());
                    masseMonnetaire.setTauxReserveObligatoir(regulateurMasseMonnetaire.getTauxReserveObligatoir());
                    masseMonnetaire.setMotifRegulation(regulateurMasseMonnetaire.getMotifRegulation());
                    masseMonnetaire.setTauxDirecteur(regulateurMasseMonnetaire.getTauxDirecteur());
                    masseMonnetaire.setTauxNegatif(regulateurMasseMonnetaire.getTauxNegatif());
                    masseMonnetaire.setPays(regulateurMasseMonnetaire.getPays());

                    return masseMonnetaire;
                }
            }

        }
        return null;
    }

    // fonction qui retourne l'objet content le montant d'un compte donné dans le reseau en detail.
    public static TransactionInterBanks getInterbankBalanceObject(String account, String pays, ServiceHub serviceHub ){

        return getLastBalanceObjectFromTransactionInterBanksStates(account,pays, serviceHub);
    }

    // fonction qui retourne l'objet content le montant d'un compte donné dans le reseau en detail.
    public static RetailTransactions getBalanceObject(String account, String pays, ServiceHub serviceHub ){

        return getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub);
    }
    private static RetailTransactions getLastBalanceObjectFromRetailTransactionsStates(String account, String pays, ServiceHub serviceHub){

        List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();

        if (stateAndRefList == null){
            return null;
        }
        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
            && stateAndRefList.get(i).getState().getData().getRetailTransactions() != null){
                if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equalsIgnoreCase(pays)) {

                    RetailTransactions retailTransactions = new RetailTransactions();

                    retailTransactions.setAccountSender(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender());
                    retailTransactions.setAccountReceiver(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver());

                    retailTransactions.setMotifTransaction(stateAndRefList.get(i).getState().getData().getRetailTransactions().getMotifTransaction());
                    retailTransactions.setPays(stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays());
                    retailTransactions.setDate(stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate());

                    retailTransactions.setDefaultAmount(stateAndRefList.get(i).getState().getData().getRetailTransactions().getDefaultAmount());
                    retailTransactions.setCurrentAmount(stateAndRefList.get(i).getState().getData().getRetailTransactions().getCurrentAmount());
                    retailTransactions.setAmountToTransfert(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert());
                    retailTransactions.setNomDevise(stateAndRefList.get(i).getState().getData().getRetailTransactions().getNomDevise());

                    retailTransactions.setAppFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAppFees());
                    retailTransactions.setCentralBankFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getCentralBankFees());
                    retailTransactions.setGuardianshipBankFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getGuardianshipBankFees());

                    return retailTransactions;
                }
                if (! stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays)) {

                    RetailTransactions retailTransactions = new RetailTransactions();

                    retailTransactions.setAccountSender(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender());
                    retailTransactions.setAccountReceiver(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountReceiver());

                    retailTransactions.setMotifTransaction(stateAndRefList.get(i).getState().getData().getRetailTransactions().getMotifTransaction());
                    retailTransactions.setPays(stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays());
                    retailTransactions.setDate(stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate());

                    retailTransactions.setDefaultAmount(stateAndRefList.get(i).getState().getData().getRetailTransactions().getDefaultAmount());
                    retailTransactions.setCurrentAmount(stateAndRefList.get(i).getState().getData().getRetailTransactions().getCurrentAmount());
                    retailTransactions.setAmountToTransfert(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert());
                    retailTransactions.setNomDevise(stateAndRefList.get(i).getState().getData().getRetailTransactions().getNomDevise());

                    retailTransactions.setAppFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getAppFees());
                    retailTransactions.setCentralBankFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getCentralBankFees());
                    retailTransactions.setGuardianshipBankFees(stateAndRefList.get(i).getState().getData().getRetailTransactions().getGuardianshipBankFees());

                    return retailTransactions;
                }
            }

        }
        return null;
    }

    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné quand elle est consideré comme sender.
    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
    public static Object getCommercialBankBalanceObjectForSending(String account, String pays, ServiceHub serviceHub ){
        //Balance from TransactionsInterBanks
        TransactionInterBanks transactionInterBanksDecidedObject = getLastBalanceObjectFromTransactionInterBanksStates(account,pays,serviceHub);
        if (transactionInterBanksDecidedObject == null){

            return null;
        }
        //Balance from RetailTransactions
        RetailTransactions retailTransactionsBanksDecidedObject = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub);
        if(retailTransactionsBanksDecidedObject == null){
            return transactionInterBanksDecidedObject;
        }
        //comparer la date des deux objets contenant chacun la balance
        if (stringCompare(transactionInterBanksDecidedObject.getDate(),retailTransactionsBanksDecidedObject.getDate()) <= 0){
            return retailTransactionsBanksDecidedObject;
        }
        return transactionInterBanksDecidedObject;
    }

    private static TransactionInterBanks getLastBalanceObjectFromTransactionInterBanksStates(String account, String pays, ServiceHub serviceHub){
        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

        if (stateAndRefList == null){
            return null;
        }
        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null ){
                TransactionInterBanks txInterbank = stateAndRefList.get(i).getState().getData().getTransactionInterBank();
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equalsIgnoreCase(pays)) {
                    TransactionInterBanks transactionInterBanks = new TransactionInterBanks();

                    transactionInterBanks.setAccountSender(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender());
                    transactionInterBanks.setAccountReceiver(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver());

                    transactionInterBanks.setMotifTransaction(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getMotifTransaction());
                    transactionInterBanks.setPays(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays());
                    transactionInterBanks.setDate(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate());

                    transactionInterBanks.setDefaultAmount(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDefaultAmount());
                    transactionInterBanks.setCurrentAmount(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCurrentAmount());
                    transactionInterBanks.setAmountToTransfert(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert());
                    transactionInterBanks.setNameDevise(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getNameDevise());

                    transactionInterBanks.setAppFees(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAppFees());
                    transactionInterBanks.setCentralBankFees(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCentralBankFees());


                    System.out.println("result1 \n"+transactionInterBanks);
                    return transactionInterBanks;
                }
                if (! stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays)) {

                    TransactionInterBanks transactionInterBanks = new TransactionInterBanks();

                    transactionInterBanks.setAccountSender(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender());
                    transactionInterBanks.setAccountReceiver(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver());

                    transactionInterBanks.setMotifTransaction(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getMotifTransaction());
                    transactionInterBanks.setPays(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays());
                    transactionInterBanks.setDate(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate());

                    transactionInterBanks.setDefaultAmount(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDefaultAmount());
                    transactionInterBanks.setCurrentAmount(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCurrentAmount());
                    transactionInterBanks.setAmountToTransfert(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert());
                    transactionInterBanks.setNameDevise(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getNameDevise());

                    transactionInterBanks.setAppFees(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAppFees());
                    transactionInterBanks.setCentralBankFees(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getCentralBankFees());


                    return transactionInterBanks;         }
            }
        }

        return null;
    }

    // fonction qui retourne l'objet content le montant d'un compte de banque commerciale donné quand elle est considerée comme receiver.
    //fonction qui utilise les 2 reseaux: interbankTX et retailTX
    public static Object getCommercialBankBalanceObjectForReceiving(String account, String pays, ServiceHub serviceHub ){
        //Balance from TransactionsInterBanks
        TransactionInterBanks transactionInterBanksDecidedObject = getLastBalanceObjectFromTransactionInterBanksStates(account,pays,serviceHub);

        //Balance from RetailTransactions
        RetailTransactions retailTransactionsBanksDecidedObject = getLastBalanceObjectFromRetailTransactionsStates(account,pays, serviceHub);

        if (transactionInterBanksDecidedObject == null){
            return retailTransactionsBanksDecidedObject;
        }
        if(retailTransactionsBanksDecidedObject == null){
            return transactionInterBanksDecidedObject;
        }
        //comparer la date des deux objets contenant chacun la balance
        if (stringCompare(transactionInterBanksDecidedObject.getDate().toString(),retailTransactionsBanksDecidedObject.getDate().toString()) <= 0){
            return retailTransactionsBanksDecidedObject;
        }
        return transactionInterBanksDecidedObject;
    }

    //Fonction qui retourne un objet banque centrale
    public static CentralBank getCentralBank(String pays, ServiceHub serviceHub){
        List<StateAndRef<CentralBankState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
        if (stateAndRefList == null){
            return null;
        }
        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null &&
                    stateAndRefList.get(i).getState().getData().getCentralBank() != null){
                if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equalsIgnoreCase(pays) &&
                        !stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("admin") &&
                        !stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
                    System.out.println("getCentralbank \n"+stateAndRefList.get(i).getState().getData().getCentralBank());
                    return stateAndRefList.get(i).getState().getData().getCentralBank();
                }
            }

        }
        return null;
    }

    //Fonction qui retourne le compte appcompte pour verser les frais de l'application
    public static CentralBank getAppCompte(String pays, ServiceHub serviceHub){
        List<StateAndRef<CentralBankState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(CentralBankState.class).getStates();
        if (stateAndRefList == null){
            return null;
        }

        for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
            if(stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getCentralBank() != null){
                if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {

                    CentralBank appcompte = new CentralBank(new CentralBankData(),new ArrayList<>());
                    appcompte.getCentralBankData().setNom(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom());
                    appcompte.getCentralBankData().setPays(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays());
                    appcompte.getCentralBankData().setAdresse(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getAdresse());
                    appcompte.getCentralBankData().setEmail(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getEmail());
                    appcompte.getCentralBankData().setLoiCreation(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getLoiCreation());

                    CentralBankAccount account = new CentralBankAccount();
                    account.setAccountId(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().get(
                            stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().size()-1
                    ).getAccountId());
                    account.setAccountType(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().get(
                            stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().size()-1
                    ).getAccountType());
                    account.setPassword(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().get(
                            stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().size()-1
                    ).getPassword());
                    account.setCRUDDate(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().get(
                            stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().size()-1
                    ).getCRUDDate());
                    account.setSuspend(stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().get(
                            stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankAccount().size()-1
                    ).isSuspend());

                    appcompte.getCentralBankAccount().add(account);



                    return appcompte;
                }
            }

        }
        return null;
    }

    // Fonction qui returne le pays de l'emetteur end user de la transaction afin de savoir
    // si ce pays = au pays du recepteur alors c'est une transaction locale sinon c'est une transaction transfrontalière
    public static String getPaysSender(String account, String password, ServiceHub serviceHub ) {
        List<StateAndRef<EndUserState>> stateAndRefList =
                serviceHub.getVaultService().queryBy(EndUserState.class).getStates();

        if (stateAndRefList == null){
            return null;
        }

        String pays = null;
        String bankId = null;
        int out=0;
        for (int i = stateAndRefList.size()-1; i >=0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null){
                EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
                for (int j = endUser.getEndUserAccounts().size()-1; j >=0 ; j--) {
                    EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
                    if (endUserAccount.getAccountId().equals(account) &&
                            endUserAccount.getPassword().equals(password)) {
                        bankId = endUserAccount.getBankIndcation();
                        out=1;
                        System.out.println("bankId\n"+bankId);
                    }
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

        if (stateAndRefList == null){
            return null;
        }

        for (int i =  stateAndRefList.size()-1; i >= 0; i--) {
            if ( stateAndRefList1.get(i) != null && stateAndRefList1.get(i).getState() != null && stateAndRefList1.get(i).getState().getData().getCommercialBank() != null){
                CommercialBank commercialBank = stateAndRefList1.get(i).getState().getData().getCommercialBank();
                for (int j = commercialBank.getCommercialBankAccounts().size()-1; j >=0; j--) {
                    CommercialBankAccount commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
                    if (commercialBankAccount.getAccountId().equals(bankId)) {
                        pays = commercialBank.getCommercialBankData().getPays();
                        out=1;
                        System.out.println("pays \n"+pays);
                    }
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

        if (stateAndRefList == null){
            return null;
        }

        for (int i = stateAndRefList.size()-1; i >=0; i--) {
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null){
                EndUser endUser = stateAndRefList.get(i).getState().getData().getEndUser();
                for (int j = endUser.getEndUserAccounts().size()-1; j >=0 ; j--) {
                    EndUserAccount endUserAccount = endUser.getEndUserAccounts().get(j);
                    if (endUserAccount.getAccountId().equals(account) &&
                            endUserAccount.getPassword().equals(password)) {
                        return endUserAccount.getBankIndcation();
                    }
                }
            }

        }
        return null;
    }

    //Fonction qui calcule le montant transferé localement depuis une durée jusqu'à maintenant
    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
    public static double getSumAmontTransferedDuringSpecificPeriodOfLocalTX(String account, String pays,
                                                                            int period, ServiceHub serviceHub, int txTypeIndex){
        Date now = new Date();
        long periodeDebutControle = now.getTime()-period;
        Date dateDebutControle = new Date(periodeDebutControle);

        double somme = 0;
        if (txTypeIndex==0){//retailTx in local
            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();

            if (stateAndRefList == null){
                return 0;
            }

            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData().getRetailTransactions() != null){
                    if (stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
                            stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                            stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
                        somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
                    }
                }

            }
        }else if(txTypeIndex ==1){//inter bank en local
            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

            if (stateAndRefList == null){
                return 0;
            }

            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
                if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null){
                    if (stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                            stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                            stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
                        somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                    }
                }

            }
        }
        return somme;
    }

    //Fonction qui calcule le montant transferé inter pays depuis une durée jusqu'à maintenant
    //txTypeIndex =0 pour des transactions en detail et txTypeIndex =1 pour des transactions inter banque
    // la variable pays ici doit être le pays de l'émetteur de la transaction
    public static double getSumAmontTransferedDuringSpecificPeriodOfTransfrontalier(String account, String pays, int period, ServiceHub serviceHub,
                                                                                    int txTypeIndex){
        Date now = new Date();
        long periodeDebutControle = now.getTime()-period;
        Date dateDebutControle = new Date(periodeDebutControle);

        double somme = 0;
        if (txTypeIndex==0){//retailTx in local
            List<StateAndRef<RetailTransactionsStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(RetailTransactionsStates.class).getStates();

            if (stateAndRefList == null){
                return 0;
            }

            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
                if ( stateAndRefList.get(i) != null &&  stateAndRefList.get(i).getState().getData() != null &&  stateAndRefList.get(i).getState().getData().getRetailTransactions() != null ){
                    if (! stateAndRefList.get(i).getState().getData().getRetailTransactions().getPays().equals(pays) &&
                            stateAndRefList.get(i).getState().getData().getRetailTransactions().getAccountSender().equals(account) &&
                            stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getRetailTransactions().getDate().toString()) <=0) {
                        somme += stateAndRefList.get(i).getState().getData().getRetailTransactions().getAmountToTransfert();
                    }
                }

            }
        }else if(txTypeIndex ==1){//inter bank en local
            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                    serviceHub.getVaultService().queryBy(TransactionInterBanksStates.class).getStates();

            if (stateAndRefList == null){
                return 0;
            }

            for (int i = stateAndRefList.size() - 1; i >= 0; i--) {
                if ( stateAndRefList.get(i) != null && stateAndRefList.get(i).getState().getData() != null && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null){
                    if (! stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                            stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                            stringCompare(dateDebutControle.toString(), stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate().toString()) <=0) {
                        somme += stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
                    }
                }

            }
        }
        return somme;
    }

    //verifier si le montant à transferer est conforme.
    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
    public static double conformiterTransactionLocalTX(double seuilMax, String pays, int periode, double montantATransferer,
                                                       String account, ServiceHub serviceHub, int txTypeIndex){
        double montantDejaTransfererPendantPeriode =
                getSumAmontTransferedDuringSpecificPeriodOfLocalTX(account, pays, periode,serviceHub,txTypeIndex);
        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
    }

    //verifier si le montant à transferer est conforme entre pays.
    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
    public static double conformiterTransactionInterPays(double seuilMax, String pays, int periode, double montantATransferer,
                                                         String account, ServiceHub serviceHub, int txTypeIndex){
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
        return montantCourant*(tauxReserveObligatoir/100);
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

}
