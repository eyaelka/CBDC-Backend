package com.template.model.endUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.corda.core.serialization.CordaSerializable;

import java.util.Date;

@CordaSerializable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EndUserData {
    private String cin;
    private String nom;
    private Date dateNaissance;
    private String adresse;
    private String nationalite;
    private String telephone;
    private String email;
    private String bankWhoAddUser; // bankWhoAddUser = the first bank's accoundId who adds user
}
