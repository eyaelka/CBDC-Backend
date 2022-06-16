package com.template.webserver.service.interfaces;

import com.template.flows.model.*;
import com.template.model.commercialBank.CommercialBankData;
import com.template.model.endUser.EndUser;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.contracts.StateAndRef;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CommercialBankInterface {

    AccountIdAndPassword create(CommercialBankAccountInfo commercialBankAccountInfo);
    CommercialBankData getCommercialBankById(String commercialBankAccountId);

    AccountIdAndPassword update(CommercialBankData commercialBankData, String commercialBankAccountId);

    //int deactivate(String commercialBankAccountId);

    int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType);
    AccountIdAndPassword createOtherCommercialBankAccount(NewCommercialBankAccount newCommercialBankAccount);

    List<StateAndRef<CommercialBankState>> getAll();

     List<EndUser> getAllUsers();
    List<RegulateurDevise> getLastRegulattionDevise(String pays);


    RegulateurTransactionInterPays getLastRegulationTransactionInterPays(String pays);


    RegulateurTransactionLocale getLastRegulationTransactionLocaleString(String pays);

     RegulateurMasseMonnetaire getLastRegulationMasseMonnetaire( String pays);
    public List<Double> getUpdatesBalance(String sender);

    Double getCurrentBalance(String sender);
     List<Double> getAllTx(String sender);

    List<TransactionInterBanks> getAlltxByCommercialBank(String sender);
    TransactionInterBanks createTransaction(TransactionInterbancaire transactionInterbancaire);






}
