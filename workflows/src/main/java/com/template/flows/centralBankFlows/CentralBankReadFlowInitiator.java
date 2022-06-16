package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CentralBankReadFlowInitiator extends FlowLogic<CentralBankData> {

    private final String centralBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankReadFlowInitiator(String centralBankAccountId) {
        this.centralBankAccountId = centralBankAccountId;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public CentralBankData call() throws FlowException {
        System.out.println(centralBankAccountId);
        CentralBankData centralBankData = null;
        List<StateAndRef<CentralBankState>> allTxLocal =
                getServiceHub().getVaultService().queryBy(CentralBankState.class).getStates();
        
        for (StateAndRef<CentralBankState> regulateurTxLocal : allTxLocal){
            CentralBankState states = regulateurTxLocal.getState().getData();
            for (CentralBankAccount centralBankAccount: states.getCentralBank().getCentralBankAccount()){
                if (centralBankAccount.getAccountId().equals(centralBankAccountId)){
                    centralBankData = states.getCentralBank().getCentralBankData();
                    System.out.println(centralBankData);
                }                
            }
        }
        return centralBankData;
    }
}
