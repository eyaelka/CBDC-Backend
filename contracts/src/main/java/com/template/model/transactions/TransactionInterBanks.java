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
public class TransactionInterBanks {
    private String accountSender;
    private double defaultAmount;
    private double currentAmount;
    private double amountToTransfert;
    private String accountReceiver;
    private String motifTransaction;
    private String pays;
    private double appFees;
    private double centralBankFees;// centralBankFees >0 si la TX est transfrontalière
    private String date;
}
