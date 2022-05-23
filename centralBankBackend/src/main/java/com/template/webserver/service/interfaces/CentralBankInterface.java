package com.template.webserver.service.interfaces;

import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.flows.model.NewCentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.webserver.model.SuspendOrActiveOrSwithAccountTypeModel;
import org.springframework.web.bind.annotation.RequestBody;

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
    RegulateurDevise getLastRegulattionDevise(String pays);

    //for transaction inter pays
    RegulateurTransactionInterPays defineTransactionInterPaysRegulation(RegulateurTransactionInterPays regulateurTransactionInterPays);
    RegulateurMasseMonnetaire getLastRegulationTransactionInterPays(String pays);

    // for local transaction

    RegulateurTransactionLocale defineTransactionLocaleRegulation(RegulateurTransactionLocale regulateurTransactionLocale);
    RegulateurTransactionLocale getLastRegulationTransactionLocaleString (String pays);



}
