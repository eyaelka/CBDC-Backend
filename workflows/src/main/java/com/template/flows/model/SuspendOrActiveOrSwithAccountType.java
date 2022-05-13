package com.template.flows.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class SuspendOrActiveOrSwithAccountType {
    private String bankAccountId;
    private String endUserAccountId;
    private boolean suspendFlag;
    private String newAccountType;
}
