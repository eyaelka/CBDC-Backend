package com.template.model.merchant;

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

public class Merchant {
    private MerchantData merchantData;
    private List<MerchantAccount> merchantAccounts = new ArrayList<>();
}
