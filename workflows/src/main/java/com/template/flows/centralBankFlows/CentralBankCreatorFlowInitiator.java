package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.flows.model.AccountIdAndPassword;
import com.template.flows.model.AccountIdAndPasswordGenerator;
import com.template.flows.model.CentralBankAccountInfo;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.states.centralBanqueStates.CentralBankState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CentralBankCreatorFlowInitiator extends FlowLogic<AccountIdAndPassword> {

    private final CentralBankAccountInfo centralBankAccountInfo;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankCreatorFlowInitiator(CentralBankAccountInfo centralBankAccountInfo) {
        this.centralBankAccountInfo = centralBankAccountInfo;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Override
    @Suspendable
    public AccountIdAndPassword call() throws FlowException {

        CentralBankState centralBankState = null;
        int index =0;
        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();


        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CentralBankState) {
                    CentralBankState centralBankState1 = (CentralBankState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (centralBankState1.getCentralBank() != null && centralBankState1.getCentralBank().getCentralBankData() != null && centralBankState1.getCentralBank().getCentralBankData().getPays().equals(centralBankAccountInfo.getCentralBankData().getPays()) && centralBankState1.getCentralBank().getCentralBankData().getNom().equals(centralBankAccountInfo.getCentralBankData().getNom())) {
                        index = 1;
                        centralBankState = centralBankState1;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }

        if (index == 0){

            //créer le mot de passe
            AccountIdAndPassword accountIdAndPassword = generateAccountIdAndPasswordRandomly();
            if (accountIdAndPassword==null)
                return null;

            Party owner = getOurIdentity();

            CentralBank centralBank = new CentralBank();
            centralBank.setCentralBankData(centralBankAccountInfo.getCentralBankData());
            CentralBankAccount centralBankAccount = new CentralBankAccount();
            centralBankAccount.setAccountType(centralBankAccountInfo.getAccountType());
            centralBankAccount.setSuspend(centralBankAccountInfo.isSuspend());
            centralBankAccount.setAccountId(accountIdAndPassword.getCompteId());
            centralBankAccount.setPassword(accountIdAndPassword.getPassword());
            centralBank.getCentralBankAccount().add(centralBankAccount);
           CentralBankState centralBankState2 = new CentralBankState(centralBank, owner, new UniqueIdentifier());

            final Command<CentralBankContract.Create> txCommand = new Command<>(new CentralBankContract.Create(), Arrays.asList(owner.getOwningKey()));

            final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
            builder.addOutputState(centralBankState2);
            builder.addCommand(txCommand);
            // Verifier si la transaction est valide.
            builder.verify(getServiceHub());
            // signer la transaction par owner kypaire.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
            subFlow(new FinalityFlow(signedTx)).getId().toString();
            return accountIdAndPassword;
        }
        String accountId =  centralBankState.getCentralBank().getCentralBankAccount().get(
                centralBankState.getCentralBank().getCentralBankAccount().size()-1).getAccountId();
        String pw =  centralBankState.getCentralBank().getCentralBankAccount().get(
                centralBankState.getCentralBank().getCentralBankAccount().size()-1).getPassword();
        return new AccountIdAndPassword(accountId,pw);
    }

    private AccountIdAndPassword generateAccountIdAndPasswordRandomly(){

        AccountIdAndPassword accountIdAndPassword = new AccountIdAndPasswordGenerator("bc").generateAccountIdAdnPassword();

        int index;
        while ((index = containsAccountIdOrPassword(accountIdAndPassword)) ==1){
            accountIdAndPassword = new AccountIdAndPasswordGenerator("bc").generateAccountIdAdnPassword();
        }
        if (index == -1)
            return null;
        return accountIdAndPassword;
    }

    int containsAccountIdOrPassword(AccountIdAndPassword accountIdAndPassword){

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof CentralBankState) {
                    CentralBankState centralBankState = (CentralBankState) signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (centralBankState.getCentralBank() != null && centralBankState.getCentralBank().getCentralBankData() != null) {
                        for (int i =0; i < centralBankState.getCentralBank().getCentralBankAccount().size(); i++){
                            if (centralBankState.getCentralBank().getCentralBankAccount().get(i).getAccountId().equals(accountIdAndPassword.getCompteId()) &&
                                    centralBankState.getCentralBank().getCentralBankAccount().get(i).getPassword().equals(accountIdAndPassword.getPassword())){
                                return 1;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

}
