package com.template.contracts.transactionsContract;

import com.template.model.transactions.RetailTransactions;

import com.template.states.transactionsStates.RetailTransactionsStates;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;


public class RetailTransactionsContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if (!tx.getInputs().isEmpty()) {
            throw new IllegalArgumentException("Aucune entrée n'est à utilisé pour pour l'enregistrement");
        }
        if (tx != null && tx.getCommands().size() < 3) {
            throw new IllegalArgumentException("Il faut au moins 3 command pour faire cette TX");
        }
        // vérifications du contenu de la transaction: montants, compte, fais, et autres
        tx.getCommands().forEach(commandDataCommandWithParties -> {
            if (commandDataCommandWithParties.getValue() instanceof
                    VerifyOwnerRestMonneyTransactionCommand){
                //cas de l'output 1
                // verify owner rest monney transaction
                verifyOwnerRestMonney(tx, commandDataCommandWithParties.getSigners());
            }
            else if (commandDataCommandWithParties.getValue() instanceof
                    VerifyTransactionCommand){
                //cas de l'output 2
                // verify real transaction
                verifyTX(tx,commandDataCommandWithParties.getSigners());
            }
            else if (commandDataCommandWithParties.getValue() instanceof
                    VerifyCommercialBankFeesTXCommand){
                //cas de l'output 3
                // Verify commercial bank fees transaction
                verifyCommercialBankFees(tx, commandDataCommandWithParties.getSigners());
            }
            else if (commandDataCommandWithParties.getValue() instanceof
                    VerifyCentralBankFeesTXCommand){
                //cas de l'output 4
                // verify central bank fees tranction
                verifyCentralBankFees(tx, commandDataCommandWithParties.getSigners());
            }
            /*else if (commandDataCommandWithParties.getValue() instanceof
                    VerifyAppFeesTXCommand){
                //cas de output 5
                // verify appFees tx
                verifyAppFees(tx, commandDataCommandWithParties.getSigners());

            }*/
        });

    }

    public static class VerifyOwnerRestMonneyTransactionCommand implements CommandData {
    }
    public static class VerifyTransactionCommand implements CommandData {
    }
    public static class VerifyCommercialBankFeesTXCommand implements CommandData {
    }
    public static class VerifyCentralBankFeesTXCommand implements CommandData {
    }
    public static class VerifyAppFeesTXCommand implements CommandData {
    }

    // verifier's functions

    private void verifyOwnerRestMonney(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        RetailTransactionsStates output1 = tx.outputsOfType(RetailTransactionsStates.class).get(0);
        //verification de l'output de la transaction
        if (output1 == null || output1.getRetailTransactions()==null){
            throw new IllegalArgumentException("Output lié à l'émetteur est null");
        }
        if (! requiredSigners.containsAll(output1.getParticipants())){
            throw new IllegalArgumentException("signature manquante");
        }
        RetailTransactions retailTransaction = output1.getRetailTransactions();
        if (retailTransaction.getMotifTransaction() == null){
            throw new IllegalArgumentException("Le motif de la transaction est null");
        }
        if (retailTransaction.getAccountSender() == null){
            throw new IllegalArgumentException("Le compte emetteur de la transaction est null");
        }
        if (retailTransaction.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur de la transaction est null");
        }
        if (retailTransaction.getDefaultAmount() < 0){
            throw new IllegalArgumentException("Le montant par defaut qui doit rester dans le compte doit être >=0");
        }
        if (retailTransaction.getCurrentAmount() <= 0){
            throw new IllegalArgumentException("Le montant actuel du compte de l'emmeteur doit être > 0");
        }
        if (retailTransaction.getCurrentAmount() < retailTransaction.getDefaultAmount()){
            throw new IllegalArgumentException("Le montant actuel du compte est insuffisant");
        }
        if (retailTransaction.getAmountToTransfert() <= 0){
            throw new IllegalArgumentException("Le montant à transférer doit être >0");
        }
        if (retailTransaction.getPays() == null){
            throw new IllegalArgumentException("Le pays du recepteur est null");
        }
        if (retailTransaction.getAppFees() < 0){
            throw new IllegalArgumentException("Les frais de l'application >=0");
        }
        if (retailTransaction.getGuardianshipBankFees() < 0){
            throw new IllegalArgumentException("Les frais de la banque de tutelle doivent être >=0");
        }
        if (retailTransaction.getDate() == null){
            throw new IllegalArgumentException("La date de transfert est null");
        }
    }

    private void verifyTX(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){

        RetailTransactionsStates output2 = tx.outputsOfType(RetailTransactionsStates.class).get(1);
        if (output2 == null || output2.getRetailTransactions()==null){
            throw new IllegalArgumentException("Output lié au recepteur est null");
        }
        if (! requiredSigners.containsAll(output2.getParticipants())){
            throw new IllegalArgumentException("signature manquante");
        }
        RetailTransactions retailTransaction2 = output2.getRetailTransactions();
        if (retailTransaction2.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");
        }
        if (retailTransaction2.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (retailTransaction2.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (retailTransaction2.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (retailTransaction2.getAmountToTransfert() <=0){
            throw new IllegalArgumentException("Le montant à transferer est <=0");
        }
        if (retailTransaction2.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur est null");
        }
    }

    private void verifyCommercialBankFees(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        RetailTransactionsStates output3 = tx.outputsOfType(RetailTransactionsStates.class).get(3);
        if (output3 == null || output3.getRetailTransactions()==null){
            throw new IllegalArgumentException("Output lié à la banque de tutelle est null");
        }
        if (! requiredSigners.containsAll(output3.getParticipants())){
            throw new IllegalArgumentException("signature manquante");
        }
        RetailTransactions retailTransaction3 = output3.getRetailTransactions();
        if (retailTransaction3.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");
        }
        if (retailTransaction3.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (retailTransaction3.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (retailTransaction3.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (retailTransaction3.getGuardianshipBankFees() <0){
            throw new IllegalArgumentException("Les frais doivent être >0");
        }
        if (retailTransaction3.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur est null ");
        }
        if (!retailTransaction3.getAccountReceiver().endsWith("cb")){
            throw new IllegalArgumentException("Le compte recepteur doit être le compte d'une banque commerciale ");
        }
    }

    private void verifyCentralBankFees(@NotNull LedgerTransaction tx, List<PublicKey> requiredSigners){
        RetailTransactionsStates output4 = tx.outputsOfType(RetailTransactionsStates.class).get(4);
        /*if(output4 != null){// s'il s'agit d'une Tx fransfrontalière
            RetailTransactions retailTransaction4 = output4.getRetailTransactions();
            if (! requiredSigners.containsAll(output4.getParticipants())){
                throw new IllegalArgumentException("signature manquante");
            }
            if (output4.getRetailTransactions()==null){
                throw new IllegalArgumentException("Output lié à la banque centrale est null");
            }
            if (retailTransaction4.getAccountSender() == null){
                throw new IllegalArgumentException("sender account est null");
            }
            if (retailTransaction4.getAccountSender().endsWith("cb")){
                throw new IllegalArgumentException("sender account doit être une banque commerciale");
            }
            if (retailTransaction4.getMotifTransaction() == null){
                throw new IllegalArgumentException("Moditif est null");
            }
            if (retailTransaction4.getDate() == null){
                throw new IllegalArgumentException("Date du transfert est null");
            }
            if (retailTransaction4.getPays() == null){
                throw new IllegalArgumentException("Pays receiver est null");
            }
            if (retailTransaction4.getCentralBankFees() <0){
                throw new IllegalArgumentException("Les frais de banque centrale sont <0");
            }
            if (retailTransaction4.getAccountReceiver() == null){
                throw new IllegalArgumentException("Le compte recepteur est null ");
            }
            if (retailTransaction4.getAccountReceiver().endsWith("bc")){
                throw new IllegalArgumentException("Le compte recepteur doit être le compte d'une banque centrale ");
            }
            if (retailTransaction4.getAccountSender().endsWith("cb")){
                throw new IllegalArgumentException("Le compte emetteur doit être le compte d'une banque commerciale ");
            }
        }
    }

    private void verifyAppFees(@NotNull LedgerTransaction tx, @NotNull List<PublicKey> requiredSigners){
        RetailTransactionsStates output5 = tx.outputsOfType(RetailTransactionsStates.class).get(5);
        if (output5 == null || output5.getRetailTransactions()==null){
            throw new IllegalArgumentException("Output lié à la banque de tutelle est null");
        }
        if (! requiredSigners.containsAll(output5.getParticipants())){
            throw new IllegalArgumentException("signature manquante");
        }
        RetailTransactions retailTransaction5 = output5.getRetailTransactions();
        if (retailTransaction5.getAccountSender() == null){
            throw new IllegalArgumentException("sender account est null");
        }
        if (retailTransaction5.getMotifTransaction() == null){
            throw new IllegalArgumentException("Moditif est null");
        }
        if (retailTransaction5.getDate() == null){
            throw new IllegalArgumentException("Date du transfert est null");
        }
        if (retailTransaction5.getPays() == null){
            throw new IllegalArgumentException("Pays receiver est null");
        }
        if (retailTransaction5.getAppFees() <= 0){
            throw new IllegalArgumentException("Les frais de l'app sont <= 0");
        }
        if (retailTransaction5.getAccountReceiver() == null){
            throw new IllegalArgumentException("Le compte recepteur des frais de l'app est null ");
        }*/
    }

    }

