package com.template.webserver.model;

import com.template.model.commercialBank.CommercialBankData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@NoArgsConstructor
@CordaSerializable
@AllArgsConstructor
public class CommercialBankUpdateModel {
    private CommercialBankData commercialBankData;
    private String commercialBankAccountId;
}
