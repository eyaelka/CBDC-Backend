package com.template.webserver.service.interfaces;

import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.flows.model.NewCentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.webserver.model.SuspendOrActiveOrSwithAccountTypeModel;
import org.springframework.web.bind.annotation.RequestBody;

public interface CentralBankInterface {
    AccountIdAndPassword save(CentralBankAccountInfo centralBankAccountInfo);
    AccountIdAndPassword createOtherBankCount(NewCentralBankAccount newCentralBankAccount);
    AccountIdAndPassword update(CentralBankData banqueCentrale, String centralBankAccountId);
    int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountTypeModel suspendOrActiveOrSwithAccountTypeModel);
    CentralBankData read(String centralBankAccountId);
//    public String getToken( AccountIdAndPassword accountIdAndPassword);

}
