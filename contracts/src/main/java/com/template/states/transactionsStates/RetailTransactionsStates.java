package com.template.states.transactionsStates;

import com.template.contracts.politiquesMonetairesContract.RegulateurTransactionLocaleContract;
import com.template.contracts.transactionsContract.RetailTransactionsContract;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.RetailTransactions;
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
@BelongsToContract(RetailTransactionsContract.class)
public class RetailTransactionsStates implements LinearState/*, QueryableState*/ {
    private final RetailTransactions retailTransactions;
    private final Party senderNode;
    private final Party receiverNode;
    private final UniqueIdentifier linearId;

    public RetailTransactionsStates(RetailTransactions retailTransactions,
                                    Party senderNode, Party receiverNode,
                                    UniqueIdentifier linearId) {
        this.retailTransactions = retailTransactions;
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

