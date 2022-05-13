package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankData;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CentralBankUpdaterFlowInitiator extends FlowLogic<AccountIdAndPassword> {
    private CentralBankData banqueCentrale;
    private String centralBankAccountId;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankUpdaterFlowInitiator(CentralBankData banqueCentrale, String centralBankAccountId) {
        this.banqueCentrale = banqueCentrale;
        this.centralBankAccountId = centralBankAccountId;
        System.out.println("constructor");
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public AccountIdAndPassword call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        CentralBank centralBank = new CentralBank();
        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPassword();
        //chercher le compte dans le ledger
        for (int i = 0; i < signedTransactions.size(); i++){
            try {
                //if (signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0)
                   //     instanceof CentralBankState) {
                    CentralBankState centralBankState1 = (CentralBankState) signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0);
                System.out.println(centralBankState1);
                    if (centralBankState1 != null && centralBankState1.getCentralBank() != null &&
                            centralBankState1.getCentralBank().getCentralBankData() != null &&
                            centralBankState1.getCentralBank().getCentralBankAccount() != null) {
                        for (int j = 0; j < centralBankState1.getCentralBank().getCentralBankAccount().size(); j++) {
                            if (centralBankState1.getCentralBank().getCentralBankAccount().get(j).getAccountId().equals(centralBankAccountId)) {
                                centralBank = centralBankState1.getCentralBank();
                                accountIdAndPassword.setCompteId(centralBankAccountId);
                                accountIdAndPassword.setPassword(centralBankState1.getCentralBank().getCentralBankAccount().get(j).getPassword());
                            }
                        }
                    }
                //}
            } catch (Exception e) {
                return null;
            }
        }
        //s'il n'existe pas alors retourner null
        if(centralBank == null){
            return null;
        }
        //S'il existe, alors le mettre à jours
        centralBank.setCentralBankData(banqueCentrale);
        final CentralBankState newCentralBankState =
                new CentralBankState(centralBank,getOurIdentity(),new UniqueIdentifier());

        final Command<CentralBankContract.Create> txCommand = new Command<>(
                new CentralBankContract.Create(), Arrays.asList(getOurIdentity().getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newCentralBankState);
        builder.addCommand(txCommand);
        builder.verify(getServiceHub());
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        subFlow(new FinalityFlow(signedTx)).getId().toString();
        return accountIdAndPassword;
    }
}
