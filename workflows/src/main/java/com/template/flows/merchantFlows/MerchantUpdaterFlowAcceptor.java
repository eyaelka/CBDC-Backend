package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(MerchantUpdaterFlowInitiator.class)

public class MerchantUpdaterFlowAcceptor extends FlowLogic<Void> {
    private FlowSession otherPartySession;

    public MerchantUpdaterFlowAcceptor(FlowSession otherPartySession) {
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
                    ContractState merchantState = stx.getTx().getOutputs().get(0).getData();
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
