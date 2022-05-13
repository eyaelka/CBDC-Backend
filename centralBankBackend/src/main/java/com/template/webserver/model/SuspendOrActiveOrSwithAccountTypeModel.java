package com.template.webserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class SuspendOrActiveOrSwithAccountTypeModel {
    private String centralBankAccountId;
    private boolean suspendFlag;
    private String newAccountType;
}
