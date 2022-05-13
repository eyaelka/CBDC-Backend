package com.template.flows.commercialBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(CommercialBankOtherAccountCreatorFlowInitiator.class)
public class CommercialBankOtherAccountCreatorFlowAcceptor  extends FlowLogic<Void> {
    private FlowSession otherPartySession;

    public CommercialBankOtherAccountCreatorFlowAcceptor(FlowSession otherPartySession) {
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
                    ContractState commercialBankState = (MerchantState) stx.getTx().getOutputs().get(0).getData();
                    require.using("invalid state", commercialBankState instanceof CommercialBankState);
                    return null;

                });
            }
        }
        SecureHash expectedTxId = subFlow(new SignTxFlow(otherPartySession)).getId();

        subFlow(new ReceiveFinalityFlow(otherPartySession, expectedTxId));
        return null;
    }
}

