package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.transactionsContract.TransactionInterBanksContract;
import com.template.flows.model.CommonTreatment;
import com.template.flows.model.TransactionInterbancaire;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.RetailTransactions;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@InitiatingFlow
@StartableByRPC
public class TransactionInterBanksFlowInitiator extends FlowLogic<TransactionInterBanks> {

    private final TransactionInterbancaire transactionInterbancaire;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public TransactionInterBanksFlowInitiator(TransactionInterbancaire transactionInterbancaire) {
        this.transactionInterbancaire = transactionInterbancaire;
    }

    @Suspendable
    @Override
    public TransactionInterBanks call() throws FlowException {

        TransactionInterBanks transactionInterBanks = transactionInterbancaire.getTransactionInterBanks();
        String password = transactionInterbancaire.getPassword();

        if (transactionInterBanks.getAccountSender() == null){
            return null;
        }
        if (transactionInterBanks.getAccountReceiver() == null){
            return null;
        }
        Object sender = getSenderOrReceiver(transactionInterBanks.getAccountSender(),password, 0);
        if (sender == null){
            return null;
        }
        Object receiverObjet = getSenderOrReceiver(transactionInterBanks.getAccountReceiver(), password, 1);
        if (receiverObjet == null){
            return null;
        }
        RegulateurTransactionLocale regulateurTransactionLocale = null;
        RegulateurTransactionInterPays regulateurTransactionInterPays = null;
        RegulateurDevise regulateurDevise = null;
        RegulateurMasseMonnetaire regulateurMasseMonnetaire = null;
        TransactionInterBanks transactionInterBank = new TransactionInterBanks();
        TransactionInterBanks transactionInterBankReceiver = null;
        double montantConvertiDevise = 0;

        Party receiverNode = getOurIdentity();
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        Party centralBankNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                new CordaX500Name("PartyA","London","GB"));


        if(transactionInterBanks.getAccountSender().endsWith("cb")){
            if (!(transactionInterBanks.getAccountReceiver().endsWith("cb"))){

                receiverNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                        new CordaX500Name("PartyA","London","GB"));
            }
            // si le compte est suspendu ou s'il s'agit d'un compte d'epargne
            if (((CommercialBank) sender).getCommercialBankAccounts().get(0).isSuspend() ||
                    ((CommercialBank) sender).getCommercialBankAccounts().get(0).getAccountType().equalsIgnoreCase("epargne")){
                return null;
            }
            /*
            Lire la masse monétaire de ce pays
            regulateurMasseMonnetaire
                    lire account balance and it's info
            */

            regulateurMasseMonnetaire = CommonTreatment.getRegulateurMasseMonnetaire(((CommercialBank) sender).getCommercialBankData().getPays(),getServiceHub());
            System.out.println(regulateurMasseMonnetaire);

            if (regulateurMasseMonnetaire == null){
                return null;
            }
            //lecture de la balance de l'émetteur
            Object senderBalanceOject = CommonTreatment.getCommercialBankBalanceObject(transactionInterBanks.getAccountSender(),
                    ((CommercialBank) sender).getCommercialBankData().getPays(),getServiceHub());
            System.out.println(senderBalanceOject);
            // S'il n'ya pas de balance alors anuller la TX
            if (senderBalanceOject == null){
                return null;
            }

            if (senderBalanceOject instanceof TransactionInterBanks){
                TransactionInterBanks interBanks = ((TransactionInterBanks) senderBalanceOject);
                transactionInterBank.setCentralBankFees(interBanks.getCentralBankFees());
                transactionInterBank.setMotifTransaction(interBanks.getMotifTransaction());
                transactionInterBank.setAccountSender(interBanks.getAccountSender());
                transactionInterBank.setAccountReceiver(interBanks.getAccountReceiver());
                transactionInterBank.setCurrentAmount(interBanks.getCurrentAmount());
                transactionInterBank.setDefaultAmount(interBanks.getDefaultAmount());
                transactionInterBank.setAmountToTransfert(interBanks.getAmountToTransfert());
                transactionInterBank.setDate(interBanks.getDate());
                transactionInterBank.setPays(interBanks.getPays());
                transactionInterBank.setAppFees(interBanks.getAppFees());
            }else {
                RetailTransactions retailTX = ((RetailTransactions) senderBalanceOject);

                transactionInterBank.setCentralBankFees(retailTX.getCentralBankFees());
                transactionInterBank.setMotifTransaction(retailTX.getMotifTransaction());
                transactionInterBank.setAccountSender(retailTX.getAccountSender());
                transactionInterBank.setAccountReceiver(retailTX.getAccountReceiver());
                transactionInterBank.setCurrentAmount(retailTX.getCurrentAmount());
                transactionInterBank.setDefaultAmount(retailTX.getDefaultAmount());
                transactionInterBank.setAmountToTransfert(retailTX.getAmountToTransfert());
                transactionInterBank.setDate(retailTX.getDate());
                transactionInterBank.setPays(retailTX.getPays());
                transactionInterBank.setAppFees(retailTX.getAppFees());
            }
            System.out.println("transactionInterBank \n"+transactionInterBank);

            if(((CommercialBank) sender).getCommercialBankData().getPays().
                    equals(transactionInterBanks.getPays())){
                /*
                transaction local
                lire la politique:
                - regulateurTransactionLocale
                - verfier la conformité du montant à transferé

                */
                regulateurTransactionLocale =CommonTreatment.getRegulateurTransactionLocale(transactionInterBanks.getPays(), getServiceHub());
                //si la reglementation TX n'est pas respecter
                if (CommonTreatment.conformiterTransactionLocalTX(regulateurTransactionLocale.getSeuilMaximumInterbank(), regulateurTransactionLocale.getPays(),regulateurTransactionLocale.getPeriode(), transactionInterBanks.getAmountToTransfert() ,
                        transactionInterBanks.getAccountSender(), getServiceHub(), 1) < 0){
                    return null;
                }

            }else {
                /*
                transaction inter pays
                lire la politique:
                - regulateurTransactionInterPays
                - regulateurDevise
                - verifier la confirmité du montant à transferé
                */

                regulateurTransactionInterPays = CommonTreatment.getRegulateurTransactionInterPays(((CommercialBank) sender).getCommercialBankData().getPays(),getServiceHub());
                //si la reglementation TX n'est pas respecter, retourner null
                if (CommonTreatment.conformiterTransactionLocalTX(regulateurTransactionInterPays.getSeuilMaximumInterbank(), regulateurTransactionInterPays.getPays(),regulateurTransactionInterPays.getPeriode(), transactionInterBanks.getAmountToTransfert() ,
                        transactionInterBanks.getAccountSender(), getServiceHub(), 1) < 0){
                    return null;
                }
                regulateurDevise = CommonTreatment.getRegulateurDevise(((CommercialBank) sender).getCommercialBankData().getPays(),transactionInterBanks.getNameDevise(),getServiceHub());
                montantConvertiDevise = convertisseurDevise(transactionInterBanks.getAmountToTransfert(), regulateurDevise.getTauxAchat());
            }
            double montantParDefautDansCompte = preparateurMontantDeReserveObligatoir(transactionInterBank.getCurrentAmount(),
                    regulateurMasseMonnetaire.getTauxReserveObligatoir());

            //preparation de la transaction
            // Cas de la TX de mise à jour du compte initiateur de la TX
            TransactionInterBanks newTransactionOutput1 = new TransactionInterBanks();
            newTransactionOutput1.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput1.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput1.setAccountReceiver(transactionInterBanks.getAccountSender());// sender = receiver => mise à jour de son compte
            newTransactionOutput1.setAppFees(transactionInterBank.getAppFees());
            newTransactionOutput1.setDate(new Date().toString());
            newTransactionOutput1.setDefaultAmount(montantParDefautDansCompte);
            newTransactionOutput1.setPays(transactionInterBank.getPays());
            newTransactionOutput1.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
            newTransactionOutput1.setNameDevise(transactionInterBanks.getNameDevise());
            if (montantConvertiDevise !=0){
                newTransactionOutput1.setCentralBankFees(transactionInterBank.getCentralBankFees());
                newTransactionOutput1.setCurrentAmount(transactionInterBank.getCurrentAmount() -
                        (
                                transactionInterBanks.getAmountToTransfert() +
                                        transactionInterBank.getAppFees() +
                                        transactionInterBank.getCentralBankFees()
                        )
                );
            }else{
                newTransactionOutput1.setCentralBankFees(0);//TX  en locale
                newTransactionOutput1.setCurrentAmount(transactionInterBank.getCurrentAmount() -
                        (
                                transactionInterBanks.getAmountToTransfert() +
                                        transactionInterBank.getAppFees()
                        )
                );
            }

            System.out.println("newTransactionOutput1 \n"+newTransactionOutput1);

            // cas du transfert de mise à jour du compte recepteur
            if (transactionInterBanks.getAccountReceiver().endsWith("cb")){
                //lecture de la balance de la banque commercial
                Object receiverBalanceOject1 = CommonTreatment.getCommercialBankBalanceObjectForReceiving(transactionInterBanks.getAccountReceiver(),
                        transactionInterBanks.getPays(),getServiceHub());
                System.out.println("receiverBalanceOject1 \n"+receiverBalanceOject1);
                // S'il n'ya pas de balance alors anuller la TX
                if (receiverBalanceOject1 != null){
                    transactionInterBankReceiver = new TransactionInterBanks();
                    if (receiverBalanceOject1 instanceof TransactionInterBanks){
                        TransactionInterBanks interBanks = ((TransactionInterBanks) receiverBalanceOject1);
                        transactionInterBankReceiver.setCentralBankFees(interBanks.getCentralBankFees());
                        transactionInterBankReceiver.setMotifTransaction(interBanks.getMotifTransaction());
                        transactionInterBankReceiver.setAccountSender(interBanks.getAccountSender());
                        transactionInterBankReceiver.setAccountReceiver(interBanks.getAccountReceiver());
                        transactionInterBankReceiver.setCurrentAmount(interBanks.getCurrentAmount());
                        transactionInterBankReceiver.setDefaultAmount(interBanks.getDefaultAmount());
                        transactionInterBankReceiver.setAmountToTransfert(interBanks.getAmountToTransfert());
                        transactionInterBankReceiver.setDate(interBanks.getDate());
                        transactionInterBankReceiver.setPays(interBanks.getPays());
                        transactionInterBankReceiver.setAppFees(interBanks.getAppFees());
                    }else {
                        RetailTransactions retailTX = ((RetailTransactions) receiverBalanceOject1);

                        transactionInterBankReceiver.setCentralBankFees(retailTX.getCentralBankFees());
                        transactionInterBankReceiver.setMotifTransaction(retailTX.getMotifTransaction());
                        transactionInterBankReceiver.setAccountSender(retailTX.getAccountSender());
                        transactionInterBankReceiver.setAccountReceiver(retailTX.getAccountReceiver());
                        transactionInterBankReceiver.setCurrentAmount(retailTX.getCurrentAmount());
                        transactionInterBankReceiver.setDefaultAmount(retailTX.getDefaultAmount());
                        transactionInterBankReceiver.setAmountToTransfert(retailTX.getAmountToTransfert());
                        transactionInterBankReceiver.setDate(retailTX.getDate());
                        transactionInterBankReceiver.setPays(retailTX.getPays());
                        transactionInterBankReceiver.setAppFees(retailTX.getAppFees());
                    }
                }
            }else {// receiver est un compte de la banque centrale
                transactionInterBankReceiver = CommonTreatment.getInterbankBalanceObject(transactionInterBanks.getAccountReceiver(),
                        transactionInterBanks.getPays(), getServiceHub());
            }

            System.out.println("transactionInterBankReceiver \n"+transactionInterBankReceiver);

            TransactionInterBanks newTransactionOutput2 = new TransactionInterBanks();


            newTransactionOutput2.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput2.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput2.setAppFees(transactionInterBank.getAppFees());
            newTransactionOutput2.setDate(newTransactionOutput1.getDate());
            newTransactionOutput2.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
            newTransactionOutput2.setNameDevise(transactionInterBanks.getNameDevise());

            double receiverOldBalance =0;
            if (transactionInterBankReceiver !=null){
                receiverOldBalance = transactionInterBankReceiver.getCurrentAmount();
                newTransactionOutput2.setDefaultAmount(transactionInterBankReceiver.getDefaultAmount());
            }else {
                newTransactionOutput2.setDefaultAmount(transactionInterBanks.getAmountToTransfert());
            }
            if (montantConvertiDevise !=0 ){
                newTransactionOutput2.setAmountToTransfert(montantConvertiDevise);
            }
            newTransactionOutput2.setPays(transactionInterBanks.getPays());
            newTransactionOutput2.setCurrentAmount(receiverOldBalance + newTransactionOutput2.getAmountToTransfert());
            newTransactionOutput2.setAccountReceiver(transactionInterBanks.getAccountReceiver());

            // en cas de transfert transfrontalié, preparer la Tx des frais pour la banque centrale
            TransactionInterBanks newTransactionOutput3 = null;
            if(montantConvertiDevise !=0){
                // pour transfrontalier
                // chercher le compte de BC à partir du pays du compte emetteur
                // chercher sa balance
                //preparer output.

                newTransactionOutput3 = new TransactionInterBanks();

                newTransactionOutput3.setMotifTransaction(transactionInterBanks.getMotifTransaction());
                newTransactionOutput3.setAccountSender(transactionInterBanks.getAccountSender());
                newTransactionOutput3.setAppFees(transactionInterBank.getAppFees());
                newTransactionOutput3.setDate(newTransactionOutput1.getDate());
                newTransactionOutput3.setAmountToTransfert(transactionInterBanks.getCentralBankFees());
                newTransactionOutput3.setNameDevise(transactionInterBanks.getNameDevise());

                CentralBank centralBank = CommonTreatment.getCentralBank(((CommercialBank) sender).getCommercialBankData().getPays(),getServiceHub());
                if(centralBank == null){
                    return null;
                }
                TransactionInterBanks centralBankBalance = CommonTreatment.getInterbankBalanceObject(centralBank.getCentralBankAccount()
                        .get(0).getAccountId(), ((CommercialBank) sender).getCommercialBankData().getPays(), getServiceHub());
                double receiverOldBalanceBC =0;
                if (centralBankBalance != null){
                    receiverOldBalanceBC = centralBankBalance.getCurrentAmount();
                    newTransactionOutput3.setDefaultAmount(centralBankBalance.getDefaultAmount());
                }else {
                    newTransactionOutput3.setDefaultAmount(transactionInterBanks.getCentralBankFees());
                }
                newTransactionOutput3.setPays(centralBank.getCentralBankData().getPays());
                newTransactionOutput3.setCurrentAmount(receiverOldBalanceBC + newTransactionOutput3.getCentralBankFees());
                newTransactionOutput3.setAccountReceiver(centralBank.getCentralBankAccount().get(0).getAccountId());

            }

            // Preparer la Tx des frais de l'application

            // chercher le compte de BC à partir du pays du compte emetteur
            // chercher sa balance
            //preparer output.
            TransactionInterBanks newTransactionOutput4 = null;

            newTransactionOutput4.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput4.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput4.setAppFees(transactionInterBank.getAppFees());
            newTransactionOutput4.setDate(newTransactionOutput1.getDate());
            newTransactionOutput4.setAmountToTransfert(transactionInterBanks.getAppFees());
            newTransactionOutput4.setNameDevise(transactionInterBanks.getNameDevise());

            CentralBank appCompte = CommonTreatment.getAppCompte(((CommercialBank) sender).getCommercialBankData().getPays(),getServiceHub());
            if(appCompte == null){
                return null;
            }
            TransactionInterBanks centralBankBalance4 = CommonTreatment.getInterbankBalanceObject(
                    appCompte.getCentralBankAccount().get(0).getAccountId(),
                    ((CommercialBank) sender).getCommercialBankData().getPays(), getServiceHub());
            double receiverOldBalance4 =0;
            if (centralBankBalance4 != null){
                receiverOldBalance4 = centralBankBalance4.getCurrentAmount();
                newTransactionOutput4.setDefaultAmount(centralBankBalance4.getDefaultAmount());
            }else {
                newTransactionOutput4.setDefaultAmount(transactionInterBanks.getAppFees());
            }
            newTransactionOutput4.setPays(appCompte.getCentralBankData().getPays());
            newTransactionOutput4.setCurrentAmount(receiverOldBalance4 + newTransactionOutput4.getAppFees());
            newTransactionOutput4.setAccountReceiver(appCompte.getCentralBankAccount().get(0).getAccountId());

            //....... Lancement de la TX...............


            // preparation des intentions.
            final Command<TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand> verifyOwnerRestMonneyTransactionCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(),getOurIdentity().getOwningKey()));

            final Command<TransactionInterBanksContract.VerifyTransactionCommand> verifyTransactionCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyTransactionCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(),receiverNode.getOwningKey()));

            final Command<TransactionInterBanksContract.VerifyCentralBankFeesTXCommand> verifyCentralBankFeesTXCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyCentralBankFeesTXCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(),centralBankNode.getOwningKey()));

            final Command<TransactionInterBanksContract.VerifyAppFeesTXCommand> verifyAppFeesTXCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyAppFeesTXCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(), centralBankNode.getOwningKey()));

            //Preparation des outputs
            TransactionInterBanksStates output1 = new TransactionInterBanksStates(newTransactionOutput1,getOurIdentity(),getOurIdentity(), new UniqueIdentifier());
            TransactionInterBanksStates output2 = new TransactionInterBanksStates(newTransactionOutput2,getOurIdentity(),receiverNode, new UniqueIdentifier());
            TransactionInterBanksStates output3 = new TransactionInterBanksStates(newTransactionOutput3,getOurIdentity(),centralBankNode, new UniqueIdentifier());
            TransactionInterBanksStates output4 = new TransactionInterBanksStates(newTransactionOutput4,getOurIdentity(),centralBankNode, new UniqueIdentifier());

            // build TX
            final TransactionBuilder builder = new TransactionBuilder(notary);
            //adding all output states
            builder.addOutputState(output1);
            builder.addOutputState(output2);
            builder.addOutputState(output3);
            builder.addOutputState(output4);

            builder.addCommand(verifyOwnerRestMonneyTransactionCommand);
            builder.addCommand(verifyTransactionCommand);
            builder.addCommand(verifyCentralBankFeesTXCommand);
            builder.addCommand(verifyAppFeesTXCommand);
            // Verifier si la transaction est valide.
            builder.verify(getServiceHub());
            // signer la transaction par owner kypaire.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
            //initier le canal de communication entre initiateur et le recepteur de la TX
            FlowSession otherPartySession = initiateFlow(receiverNode);
            //collecter toute les signatures
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
            // finaliser la transaction
            subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
            return output1.getTransactionInterBank();
        }


        //si c'est la banque centrale qui initie le TX
        if (sender instanceof CentralBank ){
            if (!(receiverObjet instanceof CentralBank)){
                receiverNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                        new CordaX500Name("PartyB","New York","US"));
            }
            // si le compte est suspendu ou s'il s'agit d'un compte d'epargne
            if (((CentralBank) sender).getCentralBankAccount().get(0).isSuspend() ||
                    ((CentralBank) sender).getCentralBankAccount().get(0).getAccountType().equalsIgnoreCase("epargne")){
                return null;
            }
            /*
            Lire la masse monétaire de ce pays
            regulateurMasseMonnetaire
                    lire account balance and it's info
            */

            regulateurMasseMonnetaire = CommonTreatment.getRegulateurMasseMonnetaire(((CentralBank) sender).getCentralBankData().getPays(),getServiceHub());

            if (regulateurMasseMonnetaire == null){
                return null;
            }
            transactionInterBank = CommonTreatment.getInterbankBalanceObject(transactionInterBanks.getAccountSender(),
                    ((CentralBank) sender).getCentralBankData().getPays(), getServiceHub());
            if (transactionInterBank == null){
                return null;
            }

            if(((CentralBank) sender).getCentralBankData().getPays().equals(transactionInterBanks.getPays())){
                /*
                transaction local
                lire la politique:
                - regulateurTransactionLocale
                - verfier la conformité du montant à transferé
                */

                regulateurTransactionLocale = CommonTreatment.getRegulateurTransactionLocale(transactionInterBanks.getPays(), getServiceHub());


            }else {
               /*
                transaction inter pays
                lire la politique:
                - regulateurTransactionInterPays
                - regulateurDevise
                - verifier la confirmité du montant à transferé

                */

                regulateurDevise = CommonTreatment.getRegulateurDevise(((CommercialBank) sender).getCommercialBankData().getPays(), transactionInterBanks.getNameDevise(), getServiceHub());
                montantConvertiDevise = convertisseurDevise(transactionInterBanks.getAmountToTransfert(), regulateurDevise.getTauxAchat());
            }
            double montantParDefautDansCompte = preparateurMontantDeReserveObligatoir(transactionInterBanks.getCurrentAmount(),
                    regulateurMasseMonnetaire.getTauxReserveObligatoir());



            //preparation de la transaction
            // Cas de la TX de mise à jour du compte initiateur de la TX
            TransactionInterBanks newTransactionOutput1 = new TransactionInterBanks();
            newTransactionOutput1.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput1.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput1.setAccountReceiver(transactionInterBanks.getAccountSender());// sender = receiver => mise à jour de son compte
            newTransactionOutput1.setAppFees(transactionInterBank.getAppFees());
            newTransactionOutput1.setDate(new Date().toString());
            newTransactionOutput1.setDefaultAmount(transactionInterBanks.getDefaultAmount());
            newTransactionOutput1.setPays(transactionInterBank.getPays());
            newTransactionOutput1.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
            newTransactionOutput1.setCentralBankFees(0);//Bc ne paye rien pour elle meme pour ces ces transferts
            newTransactionOutput1.setCurrentAmount(transactionInterBank.getCurrentAmount() -
                    (transactionInterBanks.getAmountToTransfert() + transactionInterBank.getAppFees()));
            newTransactionOutput1.setNameDevise(transactionInterBanks.getNameDevise());
            newTransactionOutput1.setAppFees(transactionInterBanks.getAppFees());

            // cas du transfert d'orignine engagé
            if (receiverObjet instanceof CommercialBank){
                //lecture de la balance de la banque commercial
                Object senderBalanceOject = CommonTreatment.getCommercialBankBalanceObjectForReceiving(transactionInterBanks.getAccountReceiver(),
                        transactionInterBanks.getPays(),getServiceHub());
                // S'il n'ya pas de balance alors anuller la TX
                if (senderBalanceOject != null){
                    transactionInterBankReceiver = new TransactionInterBanks();
                    if (senderBalanceOject instanceof TransactionInterBanks){
                        TransactionInterBanks interBanks = ((TransactionInterBanks) senderBalanceOject);
                        transactionInterBankReceiver.setCentralBankFees(interBanks.getCentralBankFees());
                        transactionInterBankReceiver.setMotifTransaction(interBanks.getMotifTransaction());
                        transactionInterBankReceiver.setAccountSender(interBanks.getAccountSender());
                        transactionInterBankReceiver.setAccountReceiver(interBanks.getAccountReceiver());
                        transactionInterBankReceiver.setCurrentAmount(interBanks.getCurrentAmount());
                        transactionInterBankReceiver.setDefaultAmount(interBanks.getDefaultAmount());
                        transactionInterBankReceiver.setAmountToTransfert(interBanks.getAmountToTransfert());
                        transactionInterBankReceiver.setDate(interBanks.getDate());
                        transactionInterBankReceiver.setPays(interBanks.getPays());
                        transactionInterBankReceiver.setAppFees(interBanks.getAppFees());
                    }else {
                        RetailTransactions retailTX = ((RetailTransactions) senderBalanceOject);

                        transactionInterBankReceiver.setCentralBankFees(retailTX.getCentralBankFees());
                        transactionInterBankReceiver.setMotifTransaction(retailTX.getMotifTransaction());
                        transactionInterBankReceiver.setAccountSender(retailTX.getAccountSender());
                        transactionInterBankReceiver.setAccountReceiver(retailTX.getAccountReceiver());
                        transactionInterBankReceiver.setCurrentAmount(retailTX.getCurrentAmount());
                        transactionInterBankReceiver.setDefaultAmount(retailTX.getDefaultAmount());
                        transactionInterBankReceiver.setAmountToTransfert(retailTX.getAmountToTransfert());
                        transactionInterBankReceiver.setDate(retailTX.getDate());
                        transactionInterBankReceiver.setPays(retailTX.getPays());
                        transactionInterBankReceiver.setAppFees(retailTX.getAppFees());
                    }
                }
            }else {// receiver est un autre compte de la banque centrale
                transactionInterBankReceiver = CommonTreatment.getInterbankBalanceObject(transactionInterBanks.getAccountReceiver(),
                        transactionInterBanks.getPays(),getServiceHub());
            }
            TransactionInterBanks newTransactionOutput2 = new TransactionInterBanks();

            newTransactionOutput2.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput2.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput2.setAccountReceiver(transactionInterBanks.getAccountReceiver());// sender = receiver => mise à jour de son compte
            newTransactionOutput2.setAppFees(transactionInterBank.getAppFees());
            newTransactionOutput2.setDate(newTransactionOutput1.getDate());
            newTransactionOutput2.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
            newTransactionOutput2.setCentralBankFees(0);//Bc ne paye rien pour elle meme pour ces ces transferts
            newTransactionOutput2.setNameDevise(transactionInterBanks.getNameDevise());
            newTransactionOutput2.setAppFees(transactionInterBanks.getAppFees());

            double receiverOldBalance =0;
            if (transactionInterBankReceiver !=null){
                receiverOldBalance = transactionInterBankReceiver.getCurrentAmount();
                newTransactionOutput2.setDefaultAmount(transactionInterBankReceiver.getDefaultAmount());
            }else {
                newTransactionOutput2.setDefaultAmount(transactionInterBanks.getAmountToTransfert());
            }
            if (montantConvertiDevise !=0 ){
                newTransactionOutput2.setAmountToTransfert(montantConvertiDevise);
            }
            newTransactionOutput2.setPays(transactionInterBanks.getPays());
            newTransactionOutput2.setCurrentAmount(receiverOldBalance + newTransactionOutput2.getAmountToTransfert());
            newTransactionOutput2.setAccountReceiver(transactionInterBanks.getAccountReceiver());
            newTransactionOutput2.setAppFees(transactionInterBanks.getAppFees());

            // Preparer la Tx des frais de l'application

            // chercher le compte de BC à partir du pays du compte emetteur
            // chercher sa balance
            //preparer output.
            TransactionInterBanks newTransactionOutput4 = new TransactionInterBanks();
            newTransactionOutput4.setMotifTransaction(transactionInterBanks.getMotifTransaction());
            newTransactionOutput4.setAccountSender(transactionInterBanks.getAccountSender());
            newTransactionOutput4.setDate(newTransactionOutput1.getDate());
            newTransactionOutput4.setAmountToTransfert(transactionInterBanks.getAmountToTransfert());
            newTransactionOutput4.setCentralBankFees(0);//Bc ne paye rien pour elle meme pour ces ces transferts
            newTransactionOutput4.setNameDevise(transactionInterBanks.getNameDevise());
            newTransactionOutput4.setAppFees(transactionInterBanks.getAppFees());

            CentralBank appCompte = CommonTreatment.getAppCompte(((CentralBank) sender).getCentralBankData().getPays(), getServiceHub());
            if(appCompte == null){
                return null;
            }
            TransactionInterBanks centralBankBalance4 = CommonTreatment.getInterbankBalanceObject(
                    appCompte.getCentralBankAccount().get(0).getAccountId(),
                    ((CentralBank) sender).getCentralBankData().getPays(),getServiceHub());

            double receiverOldBalance4 =0;
            if (centralBankBalance4 != null){
                receiverOldBalance4 = centralBankBalance4.getCurrentAmount();
                newTransactionOutput4.setDefaultAmount(centralBankBalance4.getDefaultAmount());
            }else {
                newTransactionOutput4.setDefaultAmount(transactionInterBanks.getAppFees());
            }
            newTransactionOutput4.setPays(appCompte.getCentralBankData().getPays());
            newTransactionOutput4.setCurrentAmount(receiverOldBalance4 + newTransactionOutput4.getAppFees());
            newTransactionOutput4.setAccountReceiver(appCompte.getCentralBankAccount().get(0).getAccountId());
            newTransactionOutput4.setAppFees(transactionInterBanks.getAppFees());

            //....... Lancement de la TX...............

            // preparation des intentions.
            final Command<TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand> verifyOwnerRestMonneyTransactionCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyOwnerRestMonneyTransactionCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(),getOurIdentity().getOwningKey()));

            final Command<TransactionInterBanksContract.VerifyTransactionCommand> verifyTransactionCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyTransactionCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(),receiverNode.getOwningKey()));

            final Command<TransactionInterBanksContract.VerifyAppFeesTXCommand> verifyAppFeesTXCommand
                    = new Command<>(new TransactionInterBanksContract.VerifyAppFeesTXCommand(),
                    Arrays.asList(getOurIdentity().getOwningKey(), centralBankNode.getOwningKey()));

            //Preparation des outputs
            TransactionInterBanksStates output1 = new TransactionInterBanksStates(newTransactionOutput1,getOurIdentity(),getOurIdentity(), new UniqueIdentifier());
            TransactionInterBanksStates output2 = new TransactionInterBanksStates(newTransactionOutput2,getOurIdentity(),receiverNode, new UniqueIdentifier());
            TransactionInterBanksStates output4 = new TransactionInterBanksStates(newTransactionOutput4,getOurIdentity(),centralBankNode, new UniqueIdentifier());

            // build TX
            final TransactionBuilder builder = new TransactionBuilder(notary);
            //adding all output states
            builder.addOutputState(output1);
            builder.addOutputState(output2);
            builder.addOutputState(output4);

            builder.addCommand(verifyOwnerRestMonneyTransactionCommand);
            builder.addCommand(verifyTransactionCommand);
            builder.addCommand(verifyAppFeesTXCommand);
            // Verifier si la transaction est valide.
            builder.verify(getServiceHub());
            // signer la transaction par owner kypaire.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(builder);
            //initier le canal de communication entre initiateur et le recepteur de la TX
            FlowSession otherPartySession = initiateFlow(receiverNode);
            //collecter toute les signatures
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(otherPartySession),CollectSignaturesFlow.tracker()));
            // finaliser la transaction
            subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
            return output1.getTransactionInterBank();
        }
        return null;
    }

    private Object getSenderOrReceiver(@NotNull String account, String password, int index) {
        if (index ==0) {
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
            } else if (account.endsWith("cb")) {
                //si c'est un commercial bank
                List<StateAndRef<CommercialBankState>> stateAndRefList =
                        getServiceHub().getVaultService().queryBy(CommercialBankState.class).getStates();
                CommercialBank commercialBankralBank = null;
                for (int i = 0; i < stateAndRefList.size(); i++) {
                    CommercialBank commercialBank = stateAndRefList.get(i).getState().getData().getCommercialBank();
                    for (int j = 0; j < commercialBank.getCommercialBankAccounts().size(); j++) {
                        CommercialBankAccount commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
                        if (commercialBankAccount.getAccountId().equals(account) &&
                                commercialBankAccount.getPassword().equals(password)) {
                            commercialBankralBank = new CommercialBank();
                            commercialBankralBank.setCommercialBankData(commercialBank.getCommercialBankData());
                            commercialBankralBank.getCommercialBankAccounts().add(commercialBankAccount);
                        }
                    }
                }
                return commercialBankralBank;
            } else {
                return null;
            }
        }else if (index == 1) { //on cherche le recepteur. donc pas besoin de son mot de passe
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
            } else if (account.endsWith("cb")) {
                //si c'est un commercial bank
                List<StateAndRef<CommercialBankState>> stateAndRefList =
                        getServiceHub().getVaultService().queryBy(CommercialBankState.class).getStates();
                CommercialBank commercialBankralBank = null;
                for (int i = 0; i < stateAndRefList.size(); i++) {
                    CommercialBank commercialBank = stateAndRefList.get(i).getState().getData().getCommercialBank();
                    for (int j = 0; j < commercialBank.getCommercialBankAccounts().size(); j++) {
                        CommercialBankAccount commercialBankAccount = commercialBank.getCommercialBankAccounts().get(j);
                        if (commercialBankAccount.getAccountId().equals(account)) {
                            commercialBankralBank = new CommercialBank();
                            commercialBankralBank.setCommercialBankData(commercialBank.getCommercialBankData());
                            commercialBankralBank.getCommercialBankAccounts().add(commercialBankAccount);
                        }
                    }
                }
                return commercialBankralBank;
            } else {
                return null;
            }
        }
        return null;
    }


    //convertisseur
    private double convertisseurDevise(double montantAConvertir, double tauxConversion){
        double converti = montantAConvertir * tauxConversion;
        //prendre 3 chiffres après la virgule
        int convertiEnInt = (int) (converti*1000);
        return convertiEnInt/1000;
    }
    //preparer le montant de reserve obligatoir
    private double preparateurMontantDeReserveObligatoir(double montantCourant, double tauxReserveObligatoir){
        return montantCourant*(tauxReserveObligatoir / 100);
    }
}
