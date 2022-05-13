package com.template.webserver.model;

import com.template.model.merchant.MerchantData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class MerchantUpdateModel {

    private MerchantData merchantData;
    private String merchantAccountId;
}
