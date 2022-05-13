package com.template.model.commercialBank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class CommercialBank {

    private CommercialBankData commercialBankData;
    private List<CommercialBankAccount> commercialBankAccounts = new ArrayList<>();
}
