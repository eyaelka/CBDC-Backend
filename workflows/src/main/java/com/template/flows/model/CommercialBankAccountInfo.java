package com.template.flows.model;

import com.template.model.commercialBank.CommercialBankData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable

public class CommercialBankAccountInfo {
    private CommercialBankData commercialBankData;
    private boolean suspend;
    private String accountType;
}
