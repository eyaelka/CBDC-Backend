package com.template.model.centralBank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class CentralBankData {
    private String nom;
    private String pays;
    private String adresse;
    private String loiCreation;
    private String email;
}
