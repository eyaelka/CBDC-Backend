package com.template.webserver.service.interfaces;

import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.flows.model.NewCentralBankAccount;
import com.template.flows.model.TransactionInterbancaire;
import com.template.model.centralBank.CentralBankData;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.webserver.model.SuspendOrActiveOrSwithAccountTypeModel;
import net.corda.core.contracts.StateAndRef;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CentralBankInterface {

    AccountIdAndPassword save(CentralBankAccountInfo centralBankAccountInfo);
    AccountIdAndPassword createOtherBankCount(NewCentralBankAccount newCentralBankAccount);
    AccountIdAndPassword update(CentralBankData banqueCentrale, String centralBankAccountId);
    int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountTypeModel suspendOrActiveOrSwithAccountTypeModel);
    CentralBankData read(String centralBankAccountId);
//    public String getToken( AccountIdAndPassword accountIdAndPassword);
    AccountIdAndPassword superAdmin(CentralBankAccountInfo centralBankAccountInfo);

    //for masse monetaire
    RegulateurMasseMonnetaire defineMasseMonnetaireRegulation(RegulateurMasseMonnetaire regulateurMasseMonnetaire);
    RegulateurMasseMonnetaire getLastRegulationMasseMonnetaire(String pays);

    //for devise
    RegulateurDevise defineDeviseRegulation(RegulateurDevise regulateurDevise);
    List<RegulateurDevise> getLastRegulattionDevise(String pays);

    //for transaction inter pays
    RegulateurTransactionInterPays defineTransactionInterPaysRegulation(RegulateurTransactionInterPays regulateurTransactionInterPays);
    RegulateurTransactionInterPays getLastRegulationTransactionInterPays(String pays);

    // for local transaction

    RegulateurTransactionLocale defineTransactionLocaleRegulation(RegulateurTransactionLocale regulateurTransactionLocale);
    RegulateurTransactionLocale getLastRegulationTransactionLocaleString (String pays);

    //create CBDC
    TransactionInterBanks createMoney(TransactionInterbancaire transactionInterBancaire);
    TransactionInterBanks createTransaction(TransactionInterbancaire transactionInterbancaire);

    List<CommercialBank> getCommercialBankByCountry(String pays);

    List<CommercialBank> getAllCommercialBanks();

    Double getCurrentBalance(String sender);
    List<Double> getAllAmountSentByCentralBank(String sender);

    List<String> getAllDateByCentralBank(String sender);

    List<TransactionInterBanks> getAlltxByCentralBank(String sender);

    public List<Double> getAllTx(String sender);
    public List<Double> getUpdatesBalance(String sender);









    }
