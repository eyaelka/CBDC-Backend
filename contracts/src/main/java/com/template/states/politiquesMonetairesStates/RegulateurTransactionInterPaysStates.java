package com.template.states.politiquesMonetairesStates;

import com.template.contracts.politiquesMonetairesContract.RegulateurTransactionInterPaysContract;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
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
@BelongsToContract(RegulateurTransactionInterPaysContract.class)
public class RegulateurTransactionInterPaysStates  implements LinearState/*, QueryableState*/ {
    private final RegulateurTransactionInterPays regulateurTransactionInterPays;
    private final Party senderNode;
    private final Party receiverNode;
    private final UniqueIdentifier linearId;

    public RegulateurTransactionInterPaysStates(RegulateurTransactionInterPays regulateurTransactionInterPays,
                                  Party senderNode, Party receiverNode,
                                  UniqueIdentifier linearId) {
        this.regulateurTransactionInterPays = regulateurTransactionInterPays;
        this.senderNode = senderNode;
        this.receiverNode = receiverNode;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(senderNode, receiverNode);
    }
}
