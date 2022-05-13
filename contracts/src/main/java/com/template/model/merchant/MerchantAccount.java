package com.template.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class MerchantAccount {

    private String accountId;
    private String password;
    private boolean suspend = false; // suspend = true the account is suspend, else false. false is default value
    private String accountType ="courant"; // accountType = epargne or courant. courant by default
    private String bankIndcation; // indication de la banque: numero compte banque
    private Date CRUDDate = new Date(); // CRUDDate = date creation, update date, supend date
}
