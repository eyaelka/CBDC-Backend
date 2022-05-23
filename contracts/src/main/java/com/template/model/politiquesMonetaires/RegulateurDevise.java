package com.template.model.politiquesMonetaires;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegulateurDevise {
    private String pays;
    private String nom;
    private double tauxAchat;
    private double tauxVente;
    private String date;
    private String motifVariation;
}
