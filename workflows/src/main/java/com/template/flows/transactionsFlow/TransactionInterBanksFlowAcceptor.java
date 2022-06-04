package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.transactionsStates.RetailTransactionsStates;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(TransactionInterBanksFlowInitiator.class)
public class TransactionInterBanksFlowAcceptor extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public TransactionInterBanksFlowAcceptor(FlowSession otherPartySession) {
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
                    if (stx.getTx().getOutputs().size() == 1){
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        System.out.println("Bonjour EYA la princesse");
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output instanceof TransactionInterBanksStates);
                    }else if (stx.getTx().getOutputs().size() ==3){
                        ContractState output1 = stx.getTx().getOutputs().get(0).getData();
                        ContractState output2 = stx.getTx().getOutputs().get(1).getData();
                        ContractState output3 = stx.getTx().getOutputs().get(2).getData();
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output1 instanceof TransactionInterBanksStates);
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output2 instanceof TransactionInterBanksStates);
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output3 instanceof TransactionInterBanksStates);
                    }else {
                        ContractState output1 = stx.getTx().getOutputs().get(0).getData();
                        ContractState output2 = stx.getTx().getOutputs().get(1).getData();
                        ContractState output3 = stx.getTx().getOutputs().get(2).getData();
                        ContractState output4 = stx.getTx().getOutputs().get(3).getData();
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output1 instanceof TransactionInterBanksStates);
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output2 instanceof TransactionInterBanksStates);
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output3 instanceof TransactionInterBanksStates);
                        require.using("Ce n'est pas une transaction TransactionInterBanksStates .",
                                output4 instanceof TransactionInterBanksStates);

                    }
                    return null;
                });
            }
        }
        final SecureHash txId = subFlow(new SignTxFlow(otherPartySession)).getId();
        subFlow(new ReceiveFinalityFlow(otherPartySession, txId));

        return  null;
    }
}

