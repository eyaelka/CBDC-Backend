package com.template.states.centralBanqueStates;

import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.model.centralBank.CentralBank;
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
@BelongsToContract(CentralBankContract.class)
public class CentralBankState implements LinearState {

    private final CentralBank centralBank; // central bank dadta + Account
    private final Party centralBankNode; //the node where we store central bank info
    private final UniqueIdentifier linearId;

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(centralBankNode);
    }
}
