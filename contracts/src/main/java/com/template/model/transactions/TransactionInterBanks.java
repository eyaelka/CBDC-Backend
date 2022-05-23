package com.template.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInterBanks {
    private String AccountSender;
    private double defaultAmount;
    private double currentAmount;
    private double amountToTransfert;
    private String AccountReceiver;
    private String motifTransaction;
    private String pays;
    private double appFees;
    private String date;
}
