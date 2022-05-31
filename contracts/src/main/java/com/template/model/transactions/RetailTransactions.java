package com.template.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.Date;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetailTransactions {
    private String AccountSender;
    private double defaultAmount;
    private double currentAmount;
    private double amountToTransfert;
    private String AccountReceiver;
    private String motifTransaction;
    private String pays;
    private double centralBankFees;//transfert transfrontalier
    private double appFees;
    private double guardianshipBankFees;
    private String date;
}
