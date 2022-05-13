package com.template.model.commercialBank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable

public class CommercialBankAccount {

    private String accountId;
    private String password;
    private boolean suspend = false; // suspend = true the account is suspend, else false. false is default value
    private String accountType ="courant"; // accountType = epargne or courant. courant by default
    private Date CRUDDate = new Date(); // CRUDDate = date creation, update date, supend
}
