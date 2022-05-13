package com.template.flows.model;

import com.template.model.merchant.MerchantData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class MerchantAccountInfo {
    private MerchantData merchantData;
    private boolean suspend;
    private String accountType;
    private String bankIndcation;
}
