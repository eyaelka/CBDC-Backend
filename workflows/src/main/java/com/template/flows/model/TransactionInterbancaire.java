package com.template.flows.model;

import com.template.model.transactions.TransactionInterBanks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class TransactionInterbancaire {
    private TransactionInterBanks transactionInterBanks;
    private String password;
}
