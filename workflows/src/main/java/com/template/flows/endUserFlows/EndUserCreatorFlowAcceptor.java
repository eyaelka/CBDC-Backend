package com.template.flows.endUserFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.endUserStates.EndUserState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(EndUserCreatorFlowInitiator.class)
public class EndUserCreatorFlowAcceptor extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public EndUserCreatorFlowAcceptor(FlowSession otherPartySession) {
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
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("Ce n'est pas une transaction d'enregistrement de endUser.", output instanceof EndUserState);
                        return null;
                    });
                }
            }
            final SecureHash txId = subFlow(new SignTxFlow(otherPartySession)).getId();
            subFlow(new ReceiveFinalityFlow(otherPartySession, txId));

        return  null;
    }
}
