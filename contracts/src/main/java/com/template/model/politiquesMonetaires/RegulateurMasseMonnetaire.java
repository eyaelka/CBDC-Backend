package com.template.model.politiquesMonetaires;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegulateurMasseMonnetaire {
    private String pays;
    private double tauxReserveObligatoir;
    private double tauxDirecteur;
    private double tauxNegatif;
    private String date;
    private String motifRegulation;
}
