package com.template.flows.model;

import com.template.model.centralBank.CentralBankData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class CentralBankAccountInfo {
    private CentralBankData centralBankData;
    private boolean suspend;
    private String accountType;
}
