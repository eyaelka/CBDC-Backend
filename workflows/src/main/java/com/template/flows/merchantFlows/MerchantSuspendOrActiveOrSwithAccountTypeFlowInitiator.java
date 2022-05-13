package com.template.flows.merchantFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.merchantContracts.MerchantContract;
import com.template.flows.model.SuspendOrActiveOrSwithAccountType;
import com.template.model.merchant.Merchant;
import com.template.model.merchant.MerchantAccount;
import com.template.states.merchantStates.MerchantState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class MerchantSuspendOrActiveOrSwithAccountTypeFlowInitiator  extends FlowLogic<String> {

    private SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType;
    private final ProgressTracker progressTracker = new ProgressTracker();
    public MerchantSuspendOrActiveOrSwithAccountTypeFlowInitiator(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        this.suspendOrActiveOrSwithAccountType = suspendOrActiveOrSwithAccountType;
    }
    @Override
    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }


    @Override
    @Suspendable
    public String call() throws FlowException {

        List<SignedTransaction> signedTransactions = getServiceHub().getValidatedTransactions().track().getSnapshot();

        Merchant merchant = new Merchant();
        Party addedBy = null, owner = null;
        List<MerchantAccount> merchantAccounts = new ArrayList<>();
        MerchantAccount merchantAccount = new MerchantAccount();

        //chercher le compte dans le ledger
        for (SignedTransaction signedTransaction : signedTransactions) {
            try {
                if (signedTransaction.toLedgerTransaction(getServiceHub()).getOutput(0)
                        instanceof MerchantState) {
                    MerchantState merchantState = (MerchantState) signedTransaction.
                            toLedgerTransaction(getServiceHub()).getOutput(0);
                    if (merchantState.getMerchant() != null && merchantState.getMerchant().getMerchantData() != null &&
                            merchantState.getMerchant().getMerchantAccounts() != null) {
                        for (int j = 0; j < merchantState.getMerchant().getMerchantAccounts().size(); j++) {
                            if (merchantState.getMerchant().getMerchantAccounts().get(j).getAccountId()
                                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId()) &&
                                    merchantState.getMerchant().getMerchantAccounts().get(j).getBankIndcation()
                                            .equals(suspendOrActiveOrSwithAccountType.getBankAccountId())) {
                                merchant = merchantState.getMerchant();
                                addedBy = merchantState.getAddedBy();
                                owner = merchantState.getOwner();
                                merchantAccount = merchant.getMerchantAccounts().get(j);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        //s'il n'existe pas alors retourner null
        if(merchant == null){
            return null;
        }
        //S'il existe, alors le mettre à jours
        for(int i = 0; i < merchant.getMerchantAccounts().size(); i++ ){
            if (merchant.getMerchantAccounts().get(i).getAccountId()
                    .equals(suspendOrActiveOrSwithAccountType.getEndUserAccountId()) &&
                    merchant.getMerchantAccounts().get(i).getBankIndcation()
                            .equals(suspendOrActiveOrSwithAccountType.getBankAccountId())){
                merchantAccount.setSuspend(suspendOrActiveOrSwithAccountType.isSuspendFlag());
                merchantAccount.setCRUDDate(new Date());
                if (suspendOrActiveOrSwithAccountType.getNewAccountType() != null){
                    merchantAccount.setAccountType(suspendOrActiveOrSwithAccountType.getNewAccountType());
                }
                merchantAccounts.add(merchantAccount);
            }else{
                merchantAccounts.add(merchant.getMerchantAccounts().get(i));
            }
        }
        merchant.setMerchantAccounts(merchantAccounts);
        final MerchantState newMerchantState =
                new MerchantState(merchant,addedBy, owner,new UniqueIdentifier());

        final Command<MerchantContract.Create> txCommand = new Command<>(
                new MerchantContract.Create(), Arrays.asList(addedBy.getOwningKey(), owner.getOwningKey()));
        final TransactionBuilder builder = new TransactionBuilder(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));
        builder.addOutputState(newMerchantState);
        builder.addCommand(txCommand);
        builder.verify(getServiceHub());
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        FlowSession otherPartySession = initiateFlow(owner);
        //collecter toute les signatures
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
        // finaliser la transaction
        subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession))).getId().toString();

        return merchant.getMerchantData().getEmail();

    }

}
