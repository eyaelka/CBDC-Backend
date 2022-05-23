package com.template.model.politiquesMonetaires;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegulateurTransactionLocale {
    private String pays;
    private double seuilMaximumInterbank;
    private double seuilMaximumAutresTX;//le cas des bank et end user ou end user end user ...
    private double borneMinimum; //montant minimum sans ça pendant une dure, il faut desactiver le compte.
   private int periode;
   private String date;
    private String motifRegulation;
}
