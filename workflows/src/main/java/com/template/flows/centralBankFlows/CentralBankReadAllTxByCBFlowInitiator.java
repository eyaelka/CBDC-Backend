package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@InitiatingFlow
@StartableByRPC
public class CentralBankReadAllTxByCBFlowInitiator extends FlowLogic<List<TransactionInterBanks>> {


    private  String sender;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankReadAllTxByCBFlowInitiator(String sender) {
        this.sender = sender;
    }

    @Override
    @Suspendable
    public  List<TransactionInterBanks> call() throws FlowException {

        try {
            return getAllAmountSentByCentralBank(sender);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public
    List<TransactionInterBanks> getAllAmountSentByCentralBank(String sender) throws ParseException {

        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
        List<TransactionInterBanks> transactionInterBanksList = new ArrayList<>();
        if(stateAndRefList == null){
            return null;
        }

        for (int i = 0; i < stateAndRefList.size(); i++){
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
                    && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null){
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank()
                        .getAccountSender().equals(sender) &&
                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank()
                                .getAccountReceiver().equals(sender)){
                    transactionInterBanksList.add(stateAndRefList.get(i).getState().getData().getTransactionInterBank());
                }
            }

        }
        return transactionInterBanksList;
    }
}

