
package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.transactionsStates.RetailTransactionsStates;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(EndUserRetailTransactionsFlowInitiator.class)
public class EndUserRetailTransactionFlowAcceptor extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public EndUserRetailTransactionFlowAcceptor(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(@NotNull FlowSession otherSideSession) {
                super(otherSideSession);
            }

            @Override
            protected void checkTransaction(SignedTransaction stx) {
                requireThat(require -> {
                    ContractState output1 = stx.getTx().getOutputs().get(0).getData();
                    ContractState output2 = stx.getTx().getOutputs().get(1).getData();
                    ContractState output3 = stx.getTx().getOutputs().get(2).getData();
                    ContractState output4 = stx.getTx().getOutputs().get(3).getData();
                    ContractState output5 = stx.getTx().getOutputs().get(4).getData();
                    require.using("Ce n'est pas une transaction RetailTransactionsStates .",
                            output1 instanceof RetailTransactionsStates);
                    require.using("Ce n'est pas une transaction RetailTransactionsStates .",
                            output2 instanceof RetailTransactionsStates);
                    require.using("Ce n'est pas une transaction RetailTransactionsStates .",
                            output3 instanceof RetailTransactionsStates);
                    require.using("Ce n'est pas une transaction RetailTransactionsStates .",
                            output4 instanceof RetailTransactionsStates);
                    require.using("Ce n'est pas une transaction RetailTransactionsStates .",
                            output5 instanceof RetailTransactionsStates);
                    return null;
                });
            }
        }
        final SecureHash txId = subFlow(new SignTxFlow(otherPartySession)).getId();
        subFlow(new ReceiveFinalityFlow(otherPartySession, txId));

        return  null;
    }
}

