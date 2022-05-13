package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.NewCentralBankAccount;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.centralBank.CentralBankData;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CentralBankOtherAccountCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private NewCentralBankAccount newCentralBankAccount;
    private Party owner;

    private ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankOtherAccountCreatorFlowInitiator(NewCentralBankAccount newCentralBankAccount) {
        this.newCentralBankAccount = newCentralBankAccount;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        CentralBankState centralBankState = getLastCentralBankModification(
                newCentralBankAccount.getCentralBankName(), newCentralBankAccount.getCentralBankCountry());
        if (centralBankState == null){
            return null;
        }
        owner = getOurIdentity();
        //extraire les données de la state
        CentralBank oldCentralBank = centralBankState.getCentralBank();
        List<CentralBankAccount> oldCentralBankAccounts = oldCentralBank.getCentralBankAccount();
        CentralBankData centralBankData = oldCentralBank.getCentralBankData();

        //generate accountId and password
        AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly(centralBankState);

        CentralBankAccount centralBankAccount = new CentralBankAccount();
        centralBankAccount.setAccountType(newCentralBankAccount.getAccountType());
        centralBankAccount.setSuspend(newCentralBankAccount.isSuspend());
        centralBankAccount.setAccountId(accountIdAndPassword.getCompteId());
        centralBankAccount.setPassword(accountIdAndPassword.getPassword());

        List<CentralBankAccount> newCentralBankAccounts = new ArrayList<>();
        for (int i = 0; i < oldCentralBankAccounts.size(); i++){
            newCentralBankAccounts.add(oldCentralBankAccounts.get(i));
        }
        newCentralBankAccounts.add(centralBankAccount);
        CentralBank newCentralBank = new CentralBank();
        newCentralBank.setCentralBankData(centralBankData);
        newCentralBank.setCentralBankAccount(newCentralBankAccounts);
        //create new state
        CentralBankState newcentralBankState = new CentralBankState(
                newCentralBank,owner,new UniqueIdentifier());
        //build TX
        final Command<CentralBankContract.Create> txCommand = new Command<>(new CentralBankContract.Create(), Arrays.asList(owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newcentralBankState);
        builder.addCommand(txCommand);
        // Verifier si la transaction est valide.
        builder.verify(getServiceHub());
        // signer la transaction par owner kypaire.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        subFlow(new FinalityFlow(signedTx));
        return accountIdAndPassword;
    }

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(CentralBankState centralBankState){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("bc").generateAccountIdAdnPassword();

        while (containsAccountIdOrPassword(centralBankState.getCentralBank().getCentralBankAccount(), accountIdAndPassword) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("bc").generateAccountIdAdnPassword();
        }
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(List<CentralBankAccount>centralBankAccounts, AccountIdAndPassword accountIdAndPassword){
        for (int i =0; i < centralBankAccounts.size(); i++){
            if (centralBankAccounts.get(i).getAccountId().equals(accountIdAndPassword.getCompteId()) ||
                    centralBankAccounts.get(i).getAccountId().equals(accountIdAndPassword.getPassword())){
                return 1;
            }
        }
        return 0;
    }

    public CentralBankState getLastCentralBankModification(String centralBankName, String centralBankCountry){
        CentralBankState centralBankState = null;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (int i = 0; i < signedTransactions.size(); i++){
            try {
                if (signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CentralBankState) {
                    CentralBankState centralBankState1 = (CentralBankState) signedTransactions.get(i).toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (centralBankState1 != null && centralBankState1.getCentralBank() != null &&
                            centralBankState1.getCentralBank().getCentralBankData() != null &&
                            centralBankState1.getCentralBank().getCentralBankData().getPays().equals(centralBankCountry) &&
                            centralBankState1.getCentralBank().getCentralBankData().getNom().equals(centralBankName)) {
                        centralBankState = centralBankState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return centralBankState;
    }

}
