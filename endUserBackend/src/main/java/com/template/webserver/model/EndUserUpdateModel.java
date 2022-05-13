package com.template.webserver.model;


import com.template.model.endUser.EndUserData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class EndUserUpdateModel {
    private EndUserData endUserData;
    private String endUserAccountId;
}
