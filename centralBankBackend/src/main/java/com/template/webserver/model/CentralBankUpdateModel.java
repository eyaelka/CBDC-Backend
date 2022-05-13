package com.template.webserver.model;


import com.template.model.centralBank.CentralBankData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class CentralBankUpdateModel {
    private CentralBankData banqueCentrale;
    private String centralBankAccountId;
}
