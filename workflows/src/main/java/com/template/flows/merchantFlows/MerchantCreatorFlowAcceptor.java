package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(MerchantCreatorFlowInitiator.class)
public class MerchantCreatorFlowAcceptor extends FlowLogic<Void> {
    private FlowSession otherPartySession;

    public MerchantCreatorFlowAcceptor(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession otherPartySession ){
                super(otherPartySession);
            }

            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                requireThat(require -> {
                    ContractState merchantState = (MerchantState) stx.getTx().getOutputs().get(0).getData();
                    require.using("invalid state", merchantState instanceof MerchantState);
                    return null;

                });
            }
        }
        SecureHash expectedTxId = subFlow(new SignTxFlow(otherPartySession)).getId();

        subFlow(new ReceiveFinalityFlow(otherPartySession, expectedTxId));
        return null;
    }
}
