package com.template.model.endUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class EndUser {
    private EndUserData endUserData;
    private List<EndUserAccount> endUserAccounts = new ArrayList<>();
}
