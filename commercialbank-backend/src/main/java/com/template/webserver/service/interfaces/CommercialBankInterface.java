package com.template.webserver.service.interfaces;

import com.template.flows.model.*;
import com.template.model.commercialBank.CommercialBankData;
import com.template.states.commercialBankStates.CommercialBankState;
import net.corda.core.contracts.StateAndRef;

import java.util.List;

public interface CommercialBankInterface {

    AccountIdAndPassword create(CommercialBankAccountInfo commercialBankAccountInfo);
    CommercialBankData getCommercialBankById(String commercialBankAccountId);

    AccountIdAndPassword update(CommercialBankData commercialBankData, String commercialBankAccountId);

    //int deactivate(String commercialBankAccountId);

    int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType);
    AccountIdAndPassword createOtherCommercialBankAccount(NewCommercialBankAccount newCommercialBankAccount);

    List<StateAndRef<CommercialBankState>> getAll();

}
