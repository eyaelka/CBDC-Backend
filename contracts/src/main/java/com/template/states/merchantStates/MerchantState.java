package com.template.states.merchantStates;

import com.template.contracts.merchantContracts.MerchantContract;
import com.template.model.merchant.Merchant;
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
@BelongsToContract(MerchantContract.class)
public class MerchantState implements LinearState  {
    private final Merchant merchant;
    private final Party addedBy;
    private final Party owner;
    private final UniqueIdentifier linearId;
    public MerchantState(Merchant merchant, Party addedBy, Party owner, UniqueIdentifier linearId) {
        this.merchant = merchant;
        this.addedBy = addedBy;
        this.owner = owner;
        this.linearId = linearId;
    }
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(addedBy,owner);
    }

}
