package com.template.states.politiquesMonetairesStates;

import com.template.contracts.politiquesMonetairesContract.RegulateurMasseMonnetaireContract;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
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
@BelongsToContract(RegulateurMasseMonnetaireContract.class)
public class RegulateurMasseMonnetaireStates implements LinearState/*, QueryableState*/ {
    private final RegulateurMasseMonnetaire regulateurMasseMonnetaire;
    private final Party senderNode;
    private final Party receiverNode;
    private final UniqueIdentifier linearId;

    public RegulateurMasseMonnetaireStates(RegulateurMasseMonnetaire regulateurMasseMonnetaire,
                                           Party senderNode, Party receiverNode,
                                           UniqueIdentifier linearId) {
        this.regulateurMasseMonnetaire = regulateurMasseMonnetaire;
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
