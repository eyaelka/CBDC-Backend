package com.template.model.centralBank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class CentralBank {
    private CentralBankData centralBankData;
    List<CentralBankAccount> centralBankAccount = new ArrayList<>();
}
