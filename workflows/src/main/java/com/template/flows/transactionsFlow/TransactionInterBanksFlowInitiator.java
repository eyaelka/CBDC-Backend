package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.transactionsContract.TransactionInterBanksContract;
import com.template.flows.model.TransactionInterbancaire;
import com.template.model.centralBank.CentralBank;
import com.template.model.centralBank.CentralBankAccount;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.commercialBank.CommercialBankAccount;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.centralBanqueStates.CentralBankState;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionLocaleStates;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.template.flows.model.CompareDate.stringCompare;

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

        String password = transactionInterbancaire.getPassword();
        TransactionInterBanks transactionInterBanks = transactionInterbancaire.getTransactionInterBanks();
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
        TransactionInterBanks transactionInterBank = null;
        TransactionInterBanks transactionInterBankReceiver = null;
        double montantConvertiDevise = 0;

        Party receiverNode = getOurIdentity();
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        Party centralBankNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                new CordaX500Name("PartyA","London","GB"));
        if(sender instanceof CommercialBank){
            if (!(receiverObjet instanceof CommercialBank)){
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
            regulateurMasseMonnetaire = getRegulateurMasseMonnetaire(((CommercialBank) sender).getCommercialBankData().getPays());

            if (regulateurMasseMonnetaire == null){
                return null;
            }
            transactionInterBank = getTransactionInterBanksBalanceAndBalanceInfo(transactionInterBanks.getAccountSender(),
                    ((CommercialBank) sender).getCommercialBankData().getPays());
            if (transactionInterBank == null){
                return null;
            }

            if(((CommercialBank) sender).getCommercialBankData().getPays().
                    equals(transactionInterBanks.getPays())){
                /*
                transaction local
                lire la politique:
                - regulateurTransactionLocale
                - verfier la conformité du montant à transferé
                 */
                regulateurTransactionLocale = getRegulateurTransactionLocale(transactionInterBanks.getPays());
                //si la reglementation TX n'est pas respecter
                if (conformiterTransaction(regulateurTransactionLocale.getSeuilMaximumInterbank(),regulateurTransactionLocale.getPays(),
                        regulateurTransactionLocale.getPeriode(),transactionInterBanks.getAmountToTransfert(),
                        transactionInterBanks.getAccountSender()) <0){
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
                regulateurTransactionInterPays = getRegulateurTransactionInterPays(((CommercialBank) sender).getCommercialBankData().getPays());
                //si la reglementation TX n'est pas respecter, retourner null
                if (conformiterTransaction(regulateurTransactionInterPays.getSeuilMaximumInterbank(),regulateurTransactionInterPays.getPays(),
                        regulateurTransactionInterPays.getPeriode(),transactionInterBanks.getAmountToTransfert(),
                        transactionInterBanks.getAccountSender()) <0){
                    return null;
                }
                regulateurDevise = getRegulateurDevise(((CommercialBank) sender).getCommercialBankData().getPays());
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
            System.out.println("first output \n"+newTransactionOutput1);

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

            // cas du transfert engageé
            transactionInterBankReceiver = getTransactionInterBanksBalanceAndBalanceInfo(transactionInterBanks.getAccountReceiver(),
                    transactionInterBanks.getPays());
            System.out.println("reciever balance\n"+transactionInterBankReceiver);
            TransactionInterBanks newTransactionOutput2 = newTransactionOutput1;
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

            System.out.println("second output\n"+newTransactionOutput2);

            // en cas de transfert transfrontalié, preparer la Tx des frais pour la banque centrale
            TransactionInterBanks newTransactionOutput3 = null;
            if(montantConvertiDevise !=0){
                // pour transfrontalier
                // chercher le compte de BC à partir du pays du compte emetteur
                // chercher sa balance
                //preparer output.
                CentralBank centralBank = getCentralBank(((CommercialBank) sender).getCommercialBankData().getPays());
                if(centralBank == null){
                    return null;
                }
                TransactionInterBanks centralBankBalance = getTransactionInterBanksBalanceAndBalanceInfo(centralBank.getCentralBankAccount()
                                .get(0).getAccountId(), ((CommercialBank) sender).getCommercialBankData().getPays());
                newTransactionOutput3 = newTransactionOutput1;
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
            System.out.println("third output \n"+newTransactionOutput3);

            // Preparer la Tx des frais de l'application

            // chercher le compte de BC à partir du pays du compte emetteur
            // chercher sa balance
            //preparer output.
            TransactionInterBanks newTransactionOutput4 = null;
            CentralBank appCompte = getAppCompte(((CommercialBank) sender).getCommercialBankData().getPays());
            if(appCompte == null){
                    return null;
                }
            TransactionInterBanks centralBankBalance4 = getTransactionInterBanksBalanceAndBalanceInfo(
                    appCompte.getCentralBankAccount().get(0).getAccountId(),
                        ((CommercialBank) sender).getCommercialBankData().getPays());
            System.out.println("\n"+centralBankBalance4+"\n");
            newTransactionOutput4 = newTransactionOutput1;
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
            System.out.println("4th output \n"+newTransactionOutput4);
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
            regulateurMasseMonnetaire = getRegulateurMasseMonnetaire(((CentralBank) sender).getCentralBankData().getPays());

            if (regulateurMasseMonnetaire == null){
                return null;
            }
            transactionInterBank = getTransactionInterBanksBalanceAndBalanceInfo(transactionInterBanks.getAccountSender(),
                    ((CentralBank) sender).getCentralBankData().getPays());
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
                regulateurTransactionLocale = getRegulateurTransactionLocale(transactionInterBanks.getPays());


            }else {
               /*
                transaction inter pays
                lire la politique:
                - regulateurTransactionInterPays
                - regulateurDevise
                - verifier la confirmité du montant à transferé
                 */
                regulateurTransactionInterPays = getRegulateurTransactionInterPays(((CentralBank) sender).getCentralBankData().getPays());

                regulateurDevise = getRegulateurDevise(((CentralBank) sender).getCentralBankData().getPays());
                montantConvertiDevise = convertisseurDevise(transactionInterBanks.getAmountToTransfert(), regulateurDevise.getTauxAchat());
                System.out.println(regulateurDevise);
                System.out.println(montantConvertiDevise);
            }
            double montantParDefautDansCompte = preparateurMontantDeReserveObligatoir(transactionInterBank.getCurrentAmount(),
                    /*regulateurMasseMonnetaire.getTauxReserveObligatoir()*/4);

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
            newTransactionOutput1.setCentralBankFees(0);//Bc ne paye rien pour elle meme pour ces ces transferts

            System.out.println("montant courant issue du backend: "+transactionInterBanks+"\n\n");

            System.out.println("montant courant venant du ledger: "+transactionInterBank+"\n\n");
            double reste = transactionInterBanks.getCurrentAmount()-(transactionInterBanks.getAmountToTransfert() + transactionInterBank.getAppFees());

            System.out.println("\n\n difference .... "+ reste);

            newTransactionOutput1.setCurrentAmount(reste);
            System.out.println("tx output 1 \n"+newTransactionOutput1);

            // cas du transfert d'orignine engagé
            transactionInterBankReceiver = getTransactionInterBanksBalanceAndBalanceInfo(transactionInterBanks.getAccountReceiver(),
                    transactionInterBanks.getPays());
            TransactionInterBanks newTransactionOutput2 = newTransactionOutput1;
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

            System.out.println("tx output2 \n"+newTransactionOutput2);

            // Preparer la Tx des frais de l'application

            // chercher le compte de BC à partir du pays du compte emetteur
            // chercher sa balance
            //preparer output.
            TransactionInterBanks newTransactionOutput4 = null;
            CentralBank appCompte = getAppCompte(((CentralBank) sender).getCentralBankData().getPays());
            System.out.println("appCompte\n"+appCompte);
            if(appCompte == null){
                return null;
            }
            TransactionInterBanks centralBankBalance4 = getTransactionInterBanksBalanceAndBalanceInfo(
                    appCompte.getCentralBankAccount().get(0).getAccountId(),
                    ((CentralBank) sender).getCentralBankData().getPays());
            System.out.println("centralBankBalance4\n"+centralBankBalance4);
            newTransactionOutput4 = newTransactionOutput1;
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

            System.out.println("tx output 4 \n"+newTransactionOutput4);
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
                    Arrays.asList(getOurIdentity().getOwningKey(), getOurIdentity().getOwningKey()));

            //Preparation des outputs
            TransactionInterBanksStates output1 = new TransactionInterBanksStates(newTransactionOutput1,getOurIdentity(),getOurIdentity(), new UniqueIdentifier());
            TransactionInterBanksStates output2 = new TransactionInterBanksStates(newTransactionOutput2,getOurIdentity(),receiverNode, new UniqueIdentifier());
            TransactionInterBanksStates output4 = new TransactionInterBanksStates(newTransactionOutput4,getOurIdentity(),centralBankNode, new UniqueIdentifier());

            System.out.println("states outpu1: \n"+output1.getTransactionInterBank());
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

    private RegulateurTransactionLocale getRegulateurTransactionLocale(String pays){
        List<StateAndRef<RegulateurTransactionLocaleStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionLocaleStates.class).getStates();

//        Stream<RegulateurTransactionLocale> regulateurTransactionLocaleStream = stateAndRefList.stream()
//                .map(regulateurTransactionLocaleStatesStateAndRef ->
//                        regulateurTransactionLocaleStatesStateAndRef.getState().getData().getRegulateurTransactionLocale())
//                .filter(regulateurTransactionLocale ->
//                        regulateurTransactionLocale.getPays().equals(pays))
//                .sorted(Comparator.comparing(RegulateurTransactionLocale::getDate));
//
//        return regulateurTransactionLocaleStream.collect(Collectors.toList())
//                .get(regulateurTransactionLocaleStream.collect(Collectors.toList()).size()-1);

        if (stateAndRefList == null) {
            return null;
        }

        RegulateurTransactionLocale regulateurTransactionLocale = null;
        for (int i = 1; i < stateAndRefList.size(); i++){
            if(stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale().getPays().equals(pays)) {

                regulateurTransactionLocale = stateAndRefList.get(i).getState().getData().getRegulateurTransactionLocale();

            }
        }
        return regulateurTransactionLocale;


    }

    private RegulateurTransactionInterPays getRegulateurTransactionInterPays(String pays){
        List<StateAndRef<RegulateurTransactionInterPaysStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurTransactionInterPaysStates.class).getStates();

//        Stream<RegulateurTransactionInterPays> regulateurTransactionInterPaysStream = stateAndRefList.stream()
//                .map(regulateurTransactionInterPaysStatesStateAndRef ->
//                        regulateurTransactionInterPaysStatesStateAndRef.getState().getData().getRegulateurTransactionInterPays())
//                .filter(regulateurTransactionInterPays ->
//                        regulateurTransactionInterPays.getPays().equals(pays))
//                .sorted(Comparator.comparing(RegulateurTransactionInterPays::getDate));
//
//        return regulateurTransactionInterPaysStream.collect(Collectors.toList())
//                .get(regulateurTransactionInterPaysStream.collect(Collectors.toList()).size()-1);
///////////////////////////
        if (stateAndRefList == null) {
            return null;
        }

        RegulateurTransactionInterPays regulateurTransactionInterPays = null;
        for (int i = 1; i < stateAndRefList.size(); i++){
            if(stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays().getPays().equals(pays)) {

                regulateurTransactionInterPays = stateAndRefList.get(i).getState().getData().getRegulateurTransactionInterPays();

            }
        }
        return regulateurTransactionInterPays;
    }

    private RegulateurDevise getRegulateurDevise(String pays){
        List<StateAndRef<RegulateurDeviseStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurDeviseStates.class).getStates();

//        Stream<RegulateurDevise> regulateurDeviseStream = stateAndRefList.stream()
//                .map(regulateurDeviseStatesStateAndRef ->
//                        regulateurDeviseStatesStateAndRef.getState().getData().getRegulateurDevise())
//                .filter(regulateurDevise ->
//                        regulateurDevise.getPays().equals(pays))
//                .sorted(Comparator.comparing(RegulateurDevise::getDate));
//
//        return regulateurDeviseStream.collect(Collectors.toList())
//                .get(regulateurDeviseStream.collect(Collectors.toList()).size()-1);
///////////////////
        if (stateAndRefList == null) {
            return null;
        }

        RegulateurDevise regulateurDevise = null;
        for (int i = 1; i < stateAndRefList.size(); i++){
            if(stateAndRefList.get(i).getState().getData().getRegulateurDevise().getPays().equals(pays)) {

                regulateurDevise = stateAndRefList.get(i).getState().getData().getRegulateurDevise();

            }
        }
        return regulateurDevise;
    }


    private RegulateurMasseMonnetaire getRegulateurMasseMonnetaire(String pays){
        List<StateAndRef<RegulateurMasseMonnetaireStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(RegulateurMasseMonnetaireStates.class).getStates();

//        Stream<RegulateurMasseMonnetaire> regulateurDeviseStream = stateAndRefList.stream()
//                .map(regulateurMasseMonnetaireStatesStateAndRef ->
//                        regulateurMasseMonnetaireStatesStateAndRef.getState().getData().getRegulateurMasseMonnetaire())
//                .filter(regulateurMasseMonnetaire ->
//                        regulateurMasseMonnetaire.getPays().equals(pays))
//                .sorted(Comparator.comparing(RegulateurMasseMonnetaire::getDate));
//
//
//        return regulateurDeviseStream.collect(Collectors.toList())
//                .get(regulateurDeviseStream.collect(Collectors.toList()).size()-1);

        //////////////////
        if (stateAndRefList == null) {
            return null;
        }

        RegulateurMasseMonnetaire regulateurMasseMonnetaire = null;
        for (int i = 1; i < stateAndRefList.size(); i++){
            if(stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire().getPays().equals(pays)) {

                regulateurMasseMonnetaire = stateAndRefList.get(i).getState().getData().getRegulateurMasseMonnetaire();

            }
        }
        return regulateurMasseMonnetaire;
    }

    private TransactionInterBanks getTransactionInterBanksBalanceAndBalanceInfo(String account, String pays){
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
    }

    private CentralBank getCentralBank(String pays) {
        List<StateAndRef<CentralBankState>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(CentralBankState.class).getStates();
        if (stateAndRefList == null) {
            return null;
        }

        CentralBank centralBank = null;
        for (int i = 1; i < stateAndRefList.size(); i++) {
            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
                    !stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("admin") &&
                    !stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {

                centralBank = stateAndRefList.get(i).getState().getData().getCentralBank();

            }
        }
        return centralBank;


//        Stream<CentralBank> centralBankStream = stateAndRefList.stream()
//                .map(centralBankStateStateAndRef ->
//                        centralBankStateStateAndRef.getState().getData().getCentralBank())
//                .filter(centralBank ->
//                        centralBank.getCentralBankData().getPays().equals(pays) &&
//                        !centralBank.getCentralBankData().getNom().equals("admin") &&
//                        !centralBank.getCentralBankData().getNom().equals("appcompte"));
//        return centralBankStream.collect(Collectors.toList())
//                .get(centralBankStream.collect(Collectors.toList()).size()-1);

    }

    private CentralBank getAppCompte(String pays){
        System.out.println("\n\nLe pays du compte app est: "+pays+"\n");
        List<StateAndRef<CentralBankState>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(CentralBankState.class).getStates();
        if (stateAndRefList == null) {
            return null;
        }
        CentralBank centralBank = null;
        for (int i = 1; i < stateAndRefList.size(); i++) {
            if (stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getPays().equals(pays) &&
                    stateAndRefList.get(i).getState().getData().getCentralBank().getCentralBankData().getNom().equals("appcompte")) {
                centralBank = stateAndRefList.get(i).getState().getData().getCentralBank();

            }
        }
        return centralBank;
    }

    private double getSumAmontTransferedDuringSpecificPeriod(String account, String pays, int period){
        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
        if (stateAndRefList == null){
            return 0;
        }

        Date now = new Date();
        long dateDebutControle = now.getTime()-period;

        String dateStart = new Date(dateDebutControle).toString();
        double balance=0;
        for (int i = 1; i < stateAndRefList.size(); i++){
            if(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getPays().equals(pays) &&
                    stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountSender().equals(account) &&
                    !stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAccountReceiver().equals(account) &&
            stringCompare(stateAndRefList.get(i).getState().getData().getTransactionInterBank().getDate(),dateStart)>=0) {
                balance = balance+stateAndRefList.get(i).getState().getData().getTransactionInterBank().getAmountToTransfert();
            }
        }
        return balance;


//        Stream<TransactionInterBanks> transactionInterBanksStream1 = stateAndRefList.stream()
//                .map(transactionInterBanksStatesStateAndRef ->
//                        transactionInterBanksStatesStateAndRef.getState().getData().getTransactionInterBank())
//                .filter(transactionInterBanks ->
//                        transactionInterBanks.getPays().equals(pays) &&
//                                transactionInterBanks.getAccountSender().equals(account) &&
//                                transactionInterBanks.getAccountSender().equals(transactionInterBanks.getAccountReceiver()) &&
//                        transactionInterBanks.getDate().getTime() >= dateDebutControle);
//
//        return transactionInterBanksStream1.mapToDouble(transactionInterBanks1 -> transactionInterBanks1.getAmountToTransfert())
//                .sum();
    }
    //verifier si le montant à transferer est conforme.
    // retourne seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer)
    private double conformiterTransaction(double seuilMax, String pays, int periode, double montantATransferer, String account){
        double montantDejaTransfererPendantPeriode =
                    getSumAmontTransferedDuringSpecificPeriod(account, pays, periode);
        return seuilMax - (montantDejaTransfererPendantPeriode + montantATransferer);
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
        return montantCourant*tauxReserveObligatoir;
    }
}

