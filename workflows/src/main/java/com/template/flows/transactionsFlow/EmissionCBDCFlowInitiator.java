package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.transactionsContract.TransactionInterBanksContract;
import com.template.flows.model.TransactionInterbancaire;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.template.flows.model.CompareDate.stringCompare;

@InitiatingFlow
@StartableByRPC
public class EmissionCBDCFlowInitiator extends FlowLogic<TransactionInterBanks> {


    private  final TransactionInterbancaire transactionInterbancaire;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public EmissionCBDCFlowInitiator(TransactionInterbancaire transactionInterbancaire) {
        this.transactionInterbancaire = transactionInterbancaire;
    }

    @Suspendable
    @Override
    public TransactionInterBanks call() throws FlowException {

        String password = transactionInterbancaire.getPassword();
        TransactionInterBanks transactionInterBanks = transactionInterbancaire.getTransactionInterBanks();
        CentralBank senderInfo = getSenderOrReceiver(transactionInterBanks.getAccountSender(), password, 0);
        if (senderInfo == null) {
            return null;
        }
        CentralBank receiverInfo = getSenderOrReceiver(transactionInterBanks.getAccountReceiver(), password, 1);
        if (receiverInfo == null) {
            return null;
        }
        TransactionInterBanks transactionInterBankReceiver = null;

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        //preparation de la transaction

        // Cas de la TX de mise à jour du compte initiateur de la TX
        TransactionInterBanks newTransactionOutput1 = new TransactionInterBanks();
        newTransactionOutput1.setMotifTransaction(transactionInterBanks.getMotifTransaction());
        newTransactionOutput1.setAccountSender(transactionInterBanks.getAccountReceiver());
        newTransactionOutput1.setAccountReceiver(transactionInterBanks.getAccountReceiver());
        newTransactionOutput1.setAppFees(0);
        newTransactionOutput1.setDate(new Date().toString());
        newTransactionOutput1.setDefaultAmount(0);
        newTransactionOutput1.setPays(transactionInterBanks.getPays());
        newTransactionOutput1.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
        newTransactionOutput1.setCentralBankFees(0);//Bc ne paye rien pour elle meme pour ces ces transferts
        newTransactionOutput1.setCurrentAmount(transactionInterBanks.getCurrentAmount());
//////////////////////////////////////////////////////////// ça marche
        transactionInterBankReceiver = getTransactionInterBanksBalanceAndBalanceInfo(transactionInterBanks.getAccountReceiver(),
                transactionInterBanks.getPays());
        if (transactionInterBankReceiver != null) {
            newTransactionOutput1.setCurrentAmount(transactionInterBankReceiver.getCurrentAmount() + newTransactionOutput1.getAmountToTransfert());
            newTransactionOutput1.setDefaultAmount(transactionInterBankReceiver.getDefaultAmount());
        }

        //....... Lancement de la TX...............

        // preparation des intentions.
        final Command<TransactionInterBanksContract.CreateCBDCCommand> createCBDCCommand
                = new Command<>(new TransactionInterBanksContract.CreateCBDCCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(), getOurIdentity().getOwningKey()));

        //Preparation des outputs
        TransactionInterBanksStates output1 = new TransactionInterBanksStates(newTransactionOutput1,
                getOurIdentity(), getOurIdentity(), new UniqueIdentifier());

        // build TX
        final TransactionBuilder builder = new TransactionBuilder(notary);
        //adding all output states
        builder.addOutputState(output1);
        builder.addCommand(createCBDCCommand);
        // Verifier si la transaction est valide.
        builder.verify(getServiceHub());
        // signer la transaction par owner kypaire.
        //final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        //initier le canal de communication entre initiateur et le recepteur de la TX
        //FlowSession otherPartySession = initiateFlow(getOurIdentity());
        //collecter toute les signatures
        //SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.tracker()));
        // finaliser la transaction

        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
        subFlow(new FinalityFlow(signedTx));

        //subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
        return output1.getTransactionInterBank();




    }
        private CentralBank getSenderOrReceiver(String account, String password,int index){
            if (index == 0) {
                if (account.endsWith("bc")) {
                    List<StateAndRef<CentralBankState>> stateAndRefList =
                            getServiceHub().getVaultService().queryBy(CentralBankState.class).getStates();
                    CentralBank centralBank = null;
                    for (int i = 0; i < stateAndRefList.size(); i++) {
                        CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                        for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
                            CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                            if (centralBankAccount1.getAccountId().equals(account) &&
                                    centralBankAccount1.getPassword().equals(password)) {
                                centralBank = new CentralBank();
                                centralBank.setCentralBankData(centralBank1.getCentralBankData());
                                centralBank.getCentralBankAccount().add(centralBankAccount1);
                            }
                        }
                    }
                    return centralBank;
                }
                return null;

            } else if (index == 1) { //on cherche le recepteur. donc pas besoin de son mot de passe
                if (account.endsWith("bc")) {
                    List<StateAndRef<CentralBankState>> stateAndRefList =
                            getServiceHub().getVaultService().queryBy(CentralBankState.class).getStates();
                    CentralBank centralBank = null;
                    for (int i = 0; i < stateAndRefList.size(); i++) {
                        CentralBank centralBank1 = stateAndRefList.get(i).getState().getData().getCentralBank();
                        for (int j = 0; j < centralBank1.getCentralBankAccount().size(); j++) {
                            CentralBankAccount centralBankAccount1 = centralBank1.getCentralBankAccount().get(j);
                            if (centralBankAccount1.getAccountId().equals(account)) {
                                centralBank = new CentralBank();
                                centralBank.setCentralBankData(centralBank1.getCentralBankData());
                                centralBank.getCentralBankAccount().add(centralBankAccount1);
                            }
                        }
                    }
                    return centralBank;
                }
            }
            return null;
        }
        private TransactionInterBanks getTransactionInterBanksBalanceAndBalanceInfo (String account, String pays){
            List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                    getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
            if (stateAndRefList == null) {
                return null;
            }

            // Création de CBDC ( S'auto-alimenter)
            TransactionInterBanks transactionBanks1 = null;
            for (int i = 1; i < stateAndRefList.size(); i++){
                if(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                        stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account)) {

                    transactionBanks1 = stateAndRefList.get(i).getState().getData().getTransactionInterBank();

                }
            }

            // Recevoir des transfert d'autre compte
            TransactionInterBanks transactionBanks2 = null;
            for (int i = 1; i < stateAndRefList.size(); i++){
                if(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                        ! stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                         stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account)) {

                    transactionBanks2 = stateAndRefList.get(i).getState().getData().getTransactionInterBank();

                }
            }

            //Update Le solde
            if (transactionBanks1 == null && transactionBanks2 == null) {
                return null;
            } else if (transactionBanks1 == null ) {
                return transactionBanks2;
            }else if (transactionBanks2 == null) {
                return transactionBanks1;
            }else{
                int compare = stringCompare(transactionBanks1.getDate(),transactionBanks2.getDate());
                if (compare <0){
                    return  transactionBanks2;
                }else{
                    return transactionBanks1;
                }
            }

























//            Stream<TransactionInterBanks> transactionInterBanksStream1 = stateAndRefList.stream()
//                    .map(transactionInterBanksStatesStateAndRef ->
//                            transactionInterBanksStatesStateAndRef.getState().getData().getTransactionInterBank())
//                    .filter(transactionInterBanks ->
//                            transactionInterBanks.getPays().equals(pays) &&
//                                    transactionInterBanks.getAccountSender().equals(account) &&
//                                    transactionInterBanks.getAccountSender().equals(transactionInterBanks.getAccountReceiver()));
//                    //.sorted();
//                    //.sorted(Comparator.comparing(TransactionInterBanks::getDate));
//
//            Stream<TransactionInterBanks> transactionInterBanksStream2 = stateAndRefList.stream()
//                    .map(transactionInterBanksStatesStateAndRef ->
//                            transactionInterBanksStatesStateAndRef.getState().getData().getTransactionInterBank())
//                    .filter(transactionInterBanks ->
//                            transactionInterBanks.getPays().equals(pays) &&
//                                    !transactionInterBanks.getAccountSender().equals(account) &&
//                                    transactionInterBanks.getAccountReceiver().equals(account));
//                    //.sorted();
//                    //.sorted(Comparator.comparing(TransactionInterBanks::getDate));
//            String date1 = transactionInterBanksStream1.collect(Collectors.toList())
//                    .get(transactionInterBanksStream1.collect(Collectors.toList()).size() - 1).getDate();
//            String date2 = transactionInterBanksStream2.collect(Collectors.toList())
//                    .get(transactionInterBanksStream2.collect(Collectors.toList()).size() - 1).getDate();
//            if (stringCompare(date1,date2) <0 ) {
//                return transactionInterBanksStream2.collect(Collectors.toList())
//                        .get(0);
//            }
//            return transactionInterBanksStream1.collect(Collectors.toList())
//                    .get(0);
        }


}
