package com.template.model.commercialBank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommercialBankData {
    private String name;
    private String abreviation;
    private String email;
    private String fax;
    private String address;
    private String pays;


}
