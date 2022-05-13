package com.template.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantData {
    private String agreement;// from the minister
    private String businessName;
    private String businessType;
    private String address;
    private String email;

}
