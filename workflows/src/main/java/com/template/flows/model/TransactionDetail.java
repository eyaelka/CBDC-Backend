package com.template.flows.model;

import com.template.model.transactions.RetailTransactions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class TransactionDetail {
    private RetailTransactions retailTransaction;
    private String password;
}
