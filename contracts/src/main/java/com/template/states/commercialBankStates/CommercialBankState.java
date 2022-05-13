package com.template.states.commercialBankStates;

import com.template.contracts.commercialBankContracts.CommercialBankContract;
import com.template.model.commercialBank.CommercialBank;
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
@BelongsToContract(CommercialBankContract.class)

public class CommercialBankState implements LinearState {

    private final CommercialBank commercialBank;
    private final Party addedBy;
    private final Party owner;

    private final UniqueIdentifier linearId;

    public CommercialBankState(CommercialBank commercialBank, Party addedBy, Party owner, UniqueIdentifier linearId) {
        this.commercialBank = commercialBank;
        this.owner = owner;
        this.addedBy = addedBy;
        this.linearId = linearId;
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(addedBy,owner);
    }
}
