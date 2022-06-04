package com.template.webserver.service.interfaces;

import com.template.flows.model.*;
import com.template.model.endUser.EndUserData;
import com.template.model.transactions.RetailTransactions;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.StateAndRef;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface EndUserInterface {
    AccountIdAndPassword save(EndUserAccountInfo endUserAccountInfo);
    AccountIdAndPassword createOtherEndUserCount(NewEndUserAccount newEndUserAccount);
    AccountIdAndPassword update(EndUserData endUser, String endUserAccountId);
    int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType);
    EndUserData read(String endUserAccountId);

    List<StateAndRef<EndUserState>> getAll();

    int sendEmailVerification(String email) throws MessagingException, IOException;

    int notifyAdmin(EndUserData endUserData);
    RetailTransactions doTransaction(TransactionDetail transactionDetail);


}
