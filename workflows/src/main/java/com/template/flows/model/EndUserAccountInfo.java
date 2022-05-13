package com.template.flows.model;

import com.template.model.endUser.EndUserData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
public class EndUserAccountInfo {
    private EndUserData endUserData;
    private boolean suspend;
    private String accountType;
    private String bankIndcation;
}
