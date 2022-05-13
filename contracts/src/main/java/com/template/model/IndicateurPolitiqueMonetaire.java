package com.template.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurPolitiqueMonetaire {
    private double tauxDirecteur;
    private double tauxReserveObligatoir;
    private double prixVenteTitre;
    private double prixAchatTitre;
}
