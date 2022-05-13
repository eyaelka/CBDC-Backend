package com.template.states.endUserStates;

import com.template.contracts.endUserContracts.EndUserContract;
import com.template.model.endUser.EndUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@BelongsToContract(EndUserContract.class)
public class EndUserState  implements LinearState {

    private final EndUser endUser; //end user data
    private final Party bankNodeWhoAddUser; // sender
    private final Party endUserNode; //receiver
    private final UniqueIdentifier linearId;

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(bankNodeWhoAddUser,endUserNode);
    }
}
