package com.template.contracts.transactionsContract;

import com.template.model.transactions.TransactionInterBanks;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class TransactionInterBanksContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
       if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("Aucune entrée n'est à utilisé pour pour l'enregistrement");
        }
        if(tx.getCommands().size() == 1){
            createurCBDC(tx, tx.getCommands().get(0).getSigners());
        }else {
            if(tx.getCommands().size() == 3) {
                // vérifications du contenu de la transaction: montants, compte, fais, et autres
                tx.getCommands().forEach(commandDataCommandWithParties -> {
                    if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand) {
                        //cas de l'output 1
                        // verify owner rest monney transaction
                        verifyOwnerRestMonney(tx, commandDataCommandWithParties.getSigners());
                    } else if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyTransactionCommand) {
                        //cas de l'output 2
                        // verify real transaction
                        verifyTX(tx, commandDataCommandWithParties.getSigners());
                    } else if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyAppFeesTXCommand) {
                        //cas de output 5
                        // verify appFees tx
                        verifyAppFees(tx, commandDataCommandWithParties.getSigners());
                    } else {
                        throw new IllegalArgumentException("Intentio (command) invalide");
                    }
                });
            }else{
                // vérifications du contenu de la transaction: montants, compte, fais, et autres
                tx.getCommands().forEach(commandDataCommandWithParties -> {
                    if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand) {
                        //cas de l'output 1
                        // verify owner rest monney transaction
                        verifyOwnerRestMonney(tx, commandDataCommandWithParties.getSigners());
                    } else if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyTransactionCommand) {
                        //cas de l'output 2
                        // verify real transaction
                        verifyTX(tx, commandDataCommandWithParties.getSigners());
                    }else if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyCentralBankFeesTXCommand) {
                        //cas de l'output 3
                        // verify central bank fees tranction
                        verifyCentralBankFees(tx, commandDataCommandWithParties.getSigners());
                    }else if (commandDataCommandWithParties.getValue() instanceof
                            TransactionInterBanksContract.VerifyAppFeesTXCommand) {
                        //cas de output 3
                        // verify appFees tx
                        verifyAppFees(tx, commandDataCommandWithParties.getSigners());
                    } else {
                        throw new IllegalArgumentException("Intentio (command) invalide");
                    }
                });
            }
        }
    }

    public static class VerifyOwnerRestMonneyTransactionCommand implements CommandData {
    }
    public static class VerifyTransactionCommand implements CommandData {
    }
    public static class VerifyCentralBankFeesTXCommand implements CommandData {
    }
    public static class VerifyAppFeesTXCommand implements CommandData {
    }

    public static class CreateCBDCCommand implements CommandData {
    }

    // verifier's functions

    private void verifyOwnerRestMonney(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        TransactionInterBanksStates output1 = tx.outputsOfType(TransactionInterBanksStates.class).get(0);
        //verification de l'output de la transaction
        if (output1 == null || output1.getTransactionInterBank()==null){
            throw new IllegalArgumentException("Output lié à l'émetteur est null");
        }
//        if (! requiredSigners.containsAll(output1.getParticipants())){
//            throw new IllegalArgumentException("signature manquante");
 //       }
        TransactionInterBanks transactionInterBank = output1.getTransactionInterBank();
        if (transactionInterBank.getMotifTransaction() == null){
            throw new IllegalArgumentException("Le motif de la transaction est null");
        }
        if (transactionInterBank.getAccountSender() == null){
            throw new IllegalArgumentException("Le compte emetteur de la transaction est null");
        }
        if (transactionInterBank.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur de la transaction est null");
        }
        if (transactionInterBank.getDefaultAmount() < 0){
            throw new IllegalArgumentException("Le montant par defaut qui doit rester dans le compte doit être >=0");
        }
        if (transactionInterBank.getCurrentAmount() <= 0){
            throw new IllegalArgumentException("Le montant actuel du compte de l'emmeteur doit être > 0");
        }
        if (transactionInterBank.getDefaultAmount() > transactionInterBank.getCurrentAmount()){
            throw new IllegalArgumentException("Montant insuffisant");
        }
        if (transactionInterBank.getAmountToTransfert() <= 0){
            throw new IllegalArgumentException("Le montant à transférer doit être >0");
        }
        if (transactionInterBank.getPays() == null){
            throw new IllegalArgumentException("Le pays du recepteur est null");
        }
        if (transactionInterBank.getAppFees() < 0){
            throw new IllegalArgumentException("Les frais de l'application >=0");
        }
        if (transactionInterBank.getDate() == null){
            throw new IllegalArgumentException("La date de transfert est null");
        }
    }

    private void verifyTX(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){

        TransactionInterBanksStates output2 = tx.outputsOfType(TransactionInterBanksStates.class).get(1);
        if (output2 == null || output2.getTransactionInterBank()==null){
            throw new IllegalArgumentException("Output lié au recepteur est null");
        }
//        if (! requiredSigners.containsAll(output2.getParticipants())){
//            throw new IllegalArgumentException("signature manquante");
//        }
        TransactionInterBanks transactionInterBank = output2.getTransactionInterBank();
        if (transactionInterBank.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");
        }
        if (transactionInterBank.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (transactionInterBank.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (transactionInterBank.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (transactionInterBank.getAmountToTransfert() <=0){
            throw new IllegalArgumentException("Le montant à transferer est <=0");
        }
        if (transactionInterBank.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur est null");
        }
    }

    private void verifyCentralBankFees(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        TransactionInterBanksStates output3 = tx.outputsOfType(TransactionInterBanksStates.class).get(2);
        if(output3 != null){// s'il s'agit d'une Tx fransfrontalière
            TransactionInterBanks transactionInterBank = output3.getTransactionInterBank();
//            if (! requiredSigners.containsAll(output3.getParticipants())){
//                throw new IllegalArgumentException("signature manquante");
//            }
            if (output3.getTransactionInterBank()==null){
                throw new IllegalArgumentException("Output lié à la banque centrale est null");
            }
            if (transactionInterBank.getAccountSender() == null){
                throw new IllegalArgumentException("sender account est null");
            }
            if (transactionInterBank.getAccountSender().endsWith("cb")){
                throw new IllegalArgumentException("sender account doit être une banque commerciale");
            }
            if (transactionInterBank.getMotifTransaction() == null){
                throw new IllegalArgumentException("Moditif est null");
            }
            if (transactionInterBank.getDate() == null){
                throw new IllegalArgumentException("Date du transfert est null");
            }
            if (transactionInterBank.getPays() == null){
                throw new IllegalArgumentException("Pays receiver est null");
            }
            if (transactionInterBank.getCentralBankFees() <0){
                throw new IllegalArgumentException("Les frais de banque centrale sont <0");
            }
            if (transactionInterBank.getAccountReceiver() == null){
                throw new IllegalArgumentException("Le compte recepteur est null ");
            }
            if (transactionInterBank.getAccountReceiver().endsWith("bc")){
                throw new IllegalArgumentException("Le compte recepteur doit être le compte d'une banque centrale ");
            }
        }
    }

    private void verifyAppFees(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        TransactionInterBanksStates output4;
        if (tx.getOutputs().size() == 3){
            output4 = tx.outputsOfType(TransactionInterBanksStates.class).get(2);

        }else{
             output4 = tx.outputsOfType(TransactionInterBanksStates.class).get(3);

        }
        if (output4 == null || output4.getTransactionInterBank()==null){
            throw new IllegalArgumentException("Output lié à la banque de tutelle est null");
        }
//        if (! requiredSigners.containsAll(output4.getParticipants())){
//            throw new IllegalArgumentException("signature manquante");
//        }
        TransactionInterBanks transactionInterBank = output4.getTransactionInterBank();
        if (transactionInterBank.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");
        }
        if (transactionInterBank.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (transactionInterBank.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (transactionInterBank.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (transactionInterBank.getAppFees() <= 0){
            throw new IllegalArgumentException("Les frais de l'app sont <= 0");
        }
        if (transactionInterBank.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur des frais de l'app est null ");
        }
    }

    private void createurCBDC(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){

        TransactionInterBanksStates output0 = tx.outputsOfType(TransactionInterBanksStates.class).get(0);
        if (output0 == null || output0.getTransactionInterBank()==null){
            throw new IllegalArgumentException("Output lié au recepteur est null");
        }
//        if (! requiredSigners.containsAll(output0.getParticipants())){
//            throw new IllegalArgumentException("signature manquante");
//        }
        TransactionInterBanks transactionInterBank = output0.getTransactionInterBank();
        if (transactionInterBank.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");

        }
        if (output0.getSenderNode() != output0.getReceiverNode()){
            throw new IllegalArgumentException("sender doit être = au receiver");
        }
        if (transactionInterBank.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (transactionInterBank.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (transactionInterBank.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (transactionInterBank.getAmountToTransfert() <=0){
            throw new IllegalArgumentException("Le montant à transferer est <=0");
        }
        if (transactionInterBank.getCurrentAmount() <0){
            throw new IllegalArgumentException("Le montant courant est insuffisant");
        }

        if (transactionInterBank.getDefaultAmount() <0){
            throw new IllegalArgumentException("Le montant courant est insuffisant");
        }
        if (transactionInterBank.getDefaultAmount() > transactionInterBank.getCurrentAmount()){
            throw new IllegalArgumentException("Montant insuffisant");
        }
        if (transactionInterBank.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur est null");
        }
    }
    }

