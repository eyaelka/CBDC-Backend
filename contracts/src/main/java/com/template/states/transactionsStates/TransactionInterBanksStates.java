package com.template.states.transactionsStates;

import com.template.contracts.transactionsContract.TransactionInterBanksContract;
import com.template.model.transactions.TransactionInterBanks;
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
@BelongsToContract(TransactionInterBanksContract.class)
public class TransactionInterBanksStates implements LinearState/*, QueryableState*/ {
    private final TransactionInterBanks transactionInterBank;
    private final Party senderNode;
    private final Party receiverNode;
    private final UniqueIdentifier linearId;

    public TransactionInterBanksStates(TransactionInterBanks transactionInterBank,
                                       Party senderNode, Party receiverNode,
                                       UniqueIdentifier linearId) {
        this.transactionInterBank = transactionInterBank;
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

