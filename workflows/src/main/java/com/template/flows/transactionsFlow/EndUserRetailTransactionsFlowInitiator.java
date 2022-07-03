
package com.template.flows.transactionsFlow;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.transactionsContract.RetailTransactionsContract;
import com.template.flows.model.CommonTreatment;
import com.template.flows.model.TransactionDetail;
import com.template.model.centralBank.CentralBank;
import com.template.model.commercialBank.CommercialBank;
import com.template.model.endUser.EndUser;
import com.template.model.merchant.Merchant;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.model.politiquesMonetaires.RegulateurTransactionInterPays;
import com.template.model.politiquesMonetaires.RegulateurTransactionLocale;
import com.template.model.transactions.RetailTransactions;
import com.template.states.transactionsStates.RetailTransactionsStates;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.Date;

@InitiatingFlow
@StartableByRPC
public class EndUserRetailTransactionsFlowInitiator extends FlowLogic<RetailTransactions> {

    private final TransactionDetail transactionDetail;
//    private final RetailTransactions retailTransaction;
//    private final String password;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public EndUserRetailTransactionsFlowInitiator(TransactionDetail transactionDetail) {
        this.transactionDetail = transactionDetail;

    }

    @Suspendable
    @Override
    public RetailTransactions call() throws FlowException {
        System.out.println("TX Data in flow :"+transactionDetail);
        RetailTransactions retailTransaction = transactionDetail.getRetailTransaction();
        String password = transactionDetail.getPassword();

        // si l'émétteur est null, annuler le transfert
        if (retailTransaction.getAccountSender() == null){
            return null;
        }
        // si le recepteur est null, annuler le transfert
        if (retailTransaction.getAccountReceiver() == null){
            return null;
        }
        // lecture de l'émetteur à partir du ledger
        Object sender = CommonTreatment.getSenderObject(retailTransaction.getAccountSender(),password, getServiceHub());
        System.out.println("sender \n"+sender);
        // si l'émetteur n'existe pas dans le ledger, annuller la transaction
        if (sender == null){
            return null;
        }
        // lecture du recepteur à partir du ledger
        Object receiverObjet = CommonTreatment.getReceiverObject(retailTransaction.getAccountReceiver(), getServiceHub());
        System.out.println("receiverObjet \n"+receiverObjet);
        // si le recepteur n'existe pas dans le ledger, annuller la transaction.
        if (receiverObjet == null){
            return null;
        }
        //declaration des variables de politiques monétaires
        RegulateurTransactionLocale regulateurTransactionLocale = null;
        RegulateurTransactionInterPays regulateurTransactionInterPays = null;
        RegulateurDevise regulateurDevise = null;
        RetailTransactions senderBalanceOject = null;
        RetailTransactions receiverBalanceOject = null;
        double montantConvertiEnDevise = 0;

        /*
        la variable receiverNode contient le noeud contenant le compte recepteur
        le compte recepteur est:
            - soit dans le noeud endUser(TX reflexive),
            - soit dans le noeud commercial bank (Tx vers banque commercial),
            -  soit dans le noeud merchant (Tx vers merchant)

         */

        // Si le compte recepteur est dans le noeud endUser(TX reflexive)
        Party receiverNode = getOurIdentity();

        // Si le compte recepteur est dans le noeud commercial bank (Tx vers banque commercial)
        if (receiverObjet instanceof CommercialBank){
            receiverNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                    new CordaX500Name("PartyB","New York","US"));
        }
        // Si le compte recepteur est le noeud merchant (Tx vers merchant)
        else if (receiverObjet instanceof Merchant){
            receiverNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                    new CordaX500Name("merchant","Tunisie","TN"));
        }
        if(receiverNode== null){
            return null;
        }

        //Lecture de la banque centrale pour payer les frais si c'est transfrontalier
        Party centralBankNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                new CordaX500Name("PartyA","London","GB"));

        //Lecture du node de la banque commercial pour payer les frais de l'émessions de la TX au commercial bank
        Party commercialBankNode = getServiceHub().getNetworkMapCache().getPeerByLegalName(
                new CordaX500Name("PartyB","New York","US"));
        if (commercialBankNode == null){
            return null;
        }
        // lecture du noeud notary
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // si le compte est suspendu ou s'il s'agit d'un compte d'epargne, annuler la TX
        if (((EndUser) sender).getEndUserAccounts().get(0).isSuspend() ||
                ((EndUser) sender).getEndUserAccounts().get(0).getAccountType().equalsIgnoreCase("epargne")){
            return null;
        }

        // Lecture du pays de l'émetteur de la TX. ce pays peut être différent à la nationnalité du client.
        String paysSender = CommonTreatment.getPaysSender(retailTransaction.getAccountSender(), password,getServiceHub());
        System.out.println("paysSender \n"+paysSender);
        //Si le pays est introuvable, annuller la TX
        if(paysSender == null)
            return null;

        //lecture de la balance de l'émetteur
        senderBalanceOject = CommonTreatment.getBalanceObject(retailTransaction.getAccountSender(), paysSender,getServiceHub());
        System.out.println("senderBalanceOject\n"+senderBalanceOject);
        // S'il n'ya pas de balance alors anuller la TX
        if (senderBalanceOject == null){
            return null;
        }

        // si la transaction est locale, i.e pays de l'émetteur = pays du recepteur
        if(paysSender.equals(retailTransaction.getPays())){
            /*
            lire la politique:
               - regulateurTransactionLocale
               - verifier la conformité du montant à transferé
           */
            regulateurTransactionLocale = CommonTreatment.getRegulateurTransactionLocale(retailTransaction.getPays(),getServiceHub());
            //si la reglementation TX n'est pas conforme, alors retourner anuller la TX.
            // i.e: si seuilLocalMax - (montantDejaTransfererPendantPeriode + montantATransferer) <0
            if (CommonTreatment.conformiterTransactionLocalTX(regulateurTransactionLocale.getSeuilMaximumInterbank(),
                    regulateurTransactionLocale.getPays(), regulateurTransactionLocale.getPeriode(),
                    retailTransaction.getAmountToTransfert(), retailTransaction.getAccountSender(),getServiceHub(),0) <0){
                return null;
            }
        }else {
            /*
                si c'est le cas de la transaction inter pays, alors
                lire la politique:
                - regulateurTransactionInterPays
                - regulateurDevise
                - verifier la confirmité du montant à transferé
            */

            regulateurTransactionInterPays = CommonTreatment.getRegulateurTransactionInterPays(paysSender,getServiceHub());
            //si la reglementation TX n'est pas respecter, alors retourner anuller la TX.
            // i.e: si seuilInterPaysMax - (montantDejaTransfererPendantPeriode + montantATransferer) <0
            if (CommonTreatment.conformiterTransactionInterPays(regulateurTransactionInterPays.getSeuilMaximumInterbank(),
                    regulateurTransactionInterPays.getPays(), regulateurTransactionInterPays.getPeriode(), retailTransaction.getAmountToTransfert(),
                    retailTransaction.getAccountSender(), getServiceHub(),0) <0){
                return null;
            }
            //Lecture du regulateur devise
            regulateurDevise = CommonTreatment.getRegulateurDevise(paysSender,retailTransaction.getNomDevise(), getServiceHub());
            //convertir le montant à transferer en montant du devise du recepteur
            montantConvertiEnDevise = CommonTreatment.convertisseurDevise(retailTransaction.getAmountToTransfert(), regulateurDevise.getTauxAchat());
        }

        //preparation de la transaction

        // Cas de l'output de la TX de mise à jour du compte initiateur de la TX
        RetailTransactions newTransactionOutput1 = new RetailTransactions();
        newTransactionOutput1.setMotifTransaction(retailTransaction.getMotifTransaction());
        newTransactionOutput1.setAccountSender(retailTransaction.getAccountSender());
        newTransactionOutput1.setAccountReceiver(retailTransaction.getAccountSender());// sender = receiver => mise à jour de son compte
        newTransactionOutput1.setAppFees(senderBalanceOject.getAppFees());
        newTransactionOutput1.setDate(new Date().toString());
        newTransactionOutput1.setDefaultAmount(senderBalanceOject.getDefaultAmount());
        newTransactionOutput1.setPays(senderBalanceOject.getPays());
        newTransactionOutput1.setAmountToTransfert(retailTransaction.getAmountToTransfert());
        newTransactionOutput1.setNomDevise(retailTransaction.getNomDevise());
        //si c'est transfrontalier, deduire les frais
        if (montantConvertiEnDevise !=0){
            newTransactionOutput1.setCentralBankFees(senderBalanceOject.getCentralBankFees());
            newTransactionOutput1.setCentralBankFees(retailTransaction.getCentralBankFees());
            newTransactionOutput1.setCurrentAmount(senderBalanceOject.getCurrentAmount() -
                    (retailTransaction.getAmountToTransfert() + senderBalanceOject.getAppFees() + senderBalanceOject.getCentralBankFees())
            );
        }else{//Si c'est une TX locale
            newTransactionOutput1.setCentralBankFees(0);//TX  en locale
            newTransactionOutput1.setCurrentAmount(senderBalanceOject.getCurrentAmount() -
                    (retailTransaction.getAmountToTransfert() + senderBalanceOject.getAppFees())
            );
        }

        // cas de l'output de l'incrementation du compte du recepteur de la TX
        //Lecture de l'ancien montant du recepteur
        receiverBalanceOject = CommonTreatment.getBalanceObject(retailTransaction.getAccountReceiver(),retailTransaction.getPays(),getServiceHub());

        RetailTransactions newTransactionOutput2 = newTransactionOutput1;
        double receiverOldBalance =0;
        //S'il a dejà un montant
        if (receiverBalanceOject !=null){
            receiverOldBalance = receiverBalanceOject.getCurrentAmount();
            newTransactionOutput2.setDefaultAmount(receiverBalanceOject.getDefaultAmount());
        }else {
            newTransactionOutput2.setDefaultAmount(retailTransaction.getAmountToTransfert());
        }
        if (montantConvertiEnDevise !=0 ){
            newTransactionOutput2.setAmountToTransfert(montantConvertiEnDevise);
        }
        newTransactionOutput2.setCurrentAmount(receiverOldBalance + newTransactionOutput2.getAmountToTransfert());
        newTransactionOutput2.setAccountReceiver(retailTransaction.getAccountReceiver());

        //preparation de l'output des frais de la banque commerciale de l'émetteur
        //Lecture du numero de compte de la banque commerciale
        String commercialBankAccountId = CommonTreatment.getSenderCommercialBankAccount(retailTransaction.getAccountSender(),password,getServiceHub());
        if (commercialBankAccountId == null){
            return null;
        }

        RetailTransactions commercialBankBalance = CommonTreatment.getBalanceObject(commercialBankAccountId, paysSender,getServiceHub());
        RetailTransactions newTransactionOutput3 = newTransactionOutput1;
        double receiverOldBalanceCommercialBank =0;
        if (commercialBankBalance != null){
            receiverOldBalanceCommercialBank = commercialBankBalance.getCurrentAmount();
            newTransactionOutput3.setDefaultAmount(commercialBankBalance.getDefaultAmount());
        }else {
            newTransactionOutput3.setDefaultAmount(retailTransaction.getGuardianshipBankFees());
        }
        newTransactionOutput3.setCurrentAmount(receiverOldBalanceCommercialBank + newTransactionOutput3.getGuardianshipBankFees());
        newTransactionOutput3.setAccountReceiver(commercialBankAccountId);

        // en cas de transfert transfrontalié, preparer la Tx des frais pour la banque centrale
        RetailTransactions newTransactionOutput4 = null;
        if(montantConvertiEnDevise !=0){
            // pour transfrontalier
            // chercher le compte de BC à partir du pays du compte emetteur
            // chercher sa balance
            //preparer output.
            CentralBank centralBank = CommonTreatment.getCentralBank(paysSender,getServiceHub());
            //Si aucune banque centrale ne coorespond à ce pays, alors annuler la TX
            if(centralBank == null){
                return null;
            }
            //Lecture de la balance de la banque centrale
            RetailTransactions centralBankBalance = CommonTreatment.getBalanceObject(
                    centralBank.getCentralBankAccount().get(0).getAccountId(), paysSender,getServiceHub());
            newTransactionOutput4 = newTransactionOutput1;
            double receiverOldBalanceBC =0;
            if (centralBankBalance != null){
                receiverOldBalanceBC = centralBankBalance.getCurrentAmount();
                newTransactionOutput4.setDefaultAmount(centralBankBalance.getDefaultAmount());
            }else {
                newTransactionOutput4.setDefaultAmount(retailTransaction.getCentralBankFees());
            }
            newTransactionOutput4.setAccountSender(commercialBankAccountId);
            newTransactionOutput4.setCurrentAmount(receiverOldBalanceBC + newTransactionOutput4.getCentralBankFees());
            newTransactionOutput4.setAccountReceiver(centralBank.getCentralBankAccount().get(0).getAccountId());

        }

        // Preparer l'output contenant les frais de l'app pour la Tx.

        // chercher le compte de l'app à partir du pays du compte emetteur
        // chercher sa balance
        //preparer output.
        CentralBank appCompte = CommonTreatment.getAppCompte(paysSender,getServiceHub());
        // si le compte pour les frais de l'app est null alors rejeter la TX
        if(appCompte == null){
            return null;
        }
        RetailTransactions centralBankBalance5 = CommonTreatment.getBalanceObject(appCompte.getCentralBankAccount().get(0).getAccountId(), paysSender,getServiceHub());
        RetailTransactions newTransactionOutput5 = newTransactionOutput1;
        double receiverOldBalance5 =0;
        if (centralBankBalance5 != null){
            receiverOldBalance5 = centralBankBalance5.getCurrentAmount();
            newTransactionOutput5.setDefaultAmount(centralBankBalance5.getDefaultAmount());
        }else {
            newTransactionOutput5.setDefaultAmount(retailTransaction.getAppFees());
        }
        newTransactionOutput5.setAccountSender(commercialBankAccountId);
        newTransactionOutput5.setCurrentAmount(receiverOldBalance5 + newTransactionOutput5.getAppFees());
        newTransactionOutput5.setAccountReceiver(appCompte.getCentralBankAccount().get(0).getAccountId());

        //....... Lancement de la TX...............


        // preparation des intentions.
        final Command<RetailTransactionsContract.VerifyOwnerRestMonneyTransactionCommand> verifyOwnerRestMonneyTransactionCommand
                = new Command<>(new RetailTransactionsContract.VerifyOwnerRestMonneyTransactionCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(),getOurIdentity().getOwningKey()));

        final Command<RetailTransactionsContract.VerifyTransactionCommand> verifyTransactionCommand
                = new Command<>(new RetailTransactionsContract.VerifyTransactionCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(),receiverNode.getOwningKey()));

        final Command<RetailTransactionsContract.VerifyCommercialBankFeesTXCommand> verifyCommercialBankFeesTXCommand
                = new Command<>(new RetailTransactionsContract.VerifyCommercialBankFeesTXCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(),receiverNode.getOwningKey()));

        final Command<RetailTransactionsContract.VerifyCentralBankFeesTXCommand> verifyCentralBankFeesTXCommand
                = new Command<>(new RetailTransactionsContract.VerifyCentralBankFeesTXCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(),centralBankNode.getOwningKey()));

        final Command<RetailTransactionsContract.VerifyAppFeesTXCommand> verifyAppFeesTXCommand
                = new Command<>(new RetailTransactionsContract.VerifyAppFeesTXCommand(),
                Arrays.asList(getOurIdentity().getOwningKey(), centralBankNode.getOwningKey()));

        //Preparation des outputs
        RetailTransactionsStates output1 = new RetailTransactionsStates(newTransactionOutput1,getOurIdentity(),getOurIdentity(), new UniqueIdentifier());
        RetailTransactionsStates output2 = new RetailTransactionsStates(newTransactionOutput2,getOurIdentity(),receiverNode, new UniqueIdentifier());
        RetailTransactionsStates output3 = new RetailTransactionsStates(newTransactionOutput3,getOurIdentity(),receiverNode, new UniqueIdentifier());
        //pour les frais pour la BC: emetteur = commercial bank
        RetailTransactionsStates output4 = new RetailTransactionsStates(newTransactionOutput4,commercialBankNode,centralBankNode, new UniqueIdentifier());
        //pour les frais de l'app: emetteur = commercial bank
        RetailTransactionsStates output5 = new RetailTransactionsStates(newTransactionOutput5,commercialBankNode,centralBankNode, new UniqueIdentifier());

        // build TX
        final TransactionBuilder builder = new TransactionBuilder(notary);
        //adding all output states
        builder.addOutputState(output1);
        builder.addOutputState(output2);
        builder.addOutputState(output3);
        builder.addOutputState(output4);
        builder.addOutputState(output5);

        builder.addCommand(verifyOwnerRestMonneyTransactionCommand);
        builder.addCommand(verifyTransactionCommand);
        builder.addCommand(verifyCommercialBankFeesTXCommand);
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

        return output1.getRetailTransactions();
    }
}
