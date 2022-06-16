package com.template.flows.centralBankFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.model.centralBank.CentralBankData;
import com.template.model.transactions.TransactionInterBanks;
import com.template.states.transactionsStates.TransactionInterBanksStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@InitiatingFlow
@StartableByRPC
public class CentralBankReadAllTxFlowInitiator extends FlowLogic<List<Double>> {


    private  String sender;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public CentralBankReadAllTxFlowInitiator(String sender) {
        this.sender = sender;
    }

    @Override
    @Suspendable
    public List<Double> call() throws FlowException {

        try {
            return getAllAmountSentByCentralBank(sender);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Double> getAllAmountSentByCentralBank(String sender) throws ParseException {
        List<Double> sumCBDCPerMonth = new ArrayList<>();

        Date date = new Date();
        int moisCourant = date.getMonth();
        int anneeCourante = date.getYear();

        List<StateAndRef<TransactionInterBanksStates>> stateAndRefList =
                getServiceHub().getVaultService().queryBy(TransactionInterBanksStates.class).getStates();
        List<TransactionInterBanks> transactionInterBanksList = new ArrayList<>();
        if(stateAndRefList == null){
            return null;
        }

        for (int i = 0; i < stateAndRefList.size(); i++){
            if (stateAndRefList.get(i) != null && stateAndRefList.get(i).getState() != null && stateAndRefList.get(i).getState().getData() != null
            && stateAndRefList.get(i).getState().getData().getTransactionInterBank() != null){
                if (stateAndRefList.get(i).getState().getData().getTransactionInterBank()
                        .getAccountSender().equals(sender) &&
                        !stateAndRefList.get(i).getState().getData().getTransactionInterBank()
                                .getAccountReceiver().equals(sender)){
                    transactionInterBanksList.add(stateAndRefList.get(i).getState().getData().getTransactionInterBank());
                }
            }

        }
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy", Locale.ENGLISH);

        if (transactionInterBanksList == null){
            return null;
        }
        for (int j = 0; j <= moisCourant; j++) {
            double sumCBDC =0;
            for (int i = 0; i < transactionInterBanksList.size(); i++) {
                if (transactionInterBanksList.get(i) != null){
                    String dateInString = transactionInterBanksList.get(i).getDate();
                    System.out.println("dateInString \n"+dateInString);
                    Date date1 = formatter.parse(dateInString);
                    SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("MMM dd,yyyy HH:mm:ss"); // New Pattern

                    System.out.println(simpleDateFormat1.format(date1)); // Format given String to new pattern


                    System.out.println("date1 \n"+date1);
                    if (date1.getYear() == anneeCourante && date1.getMonth()== j){
                        sumCBDC += transactionInterBanksList.get(i).getAmountToTransfert();
                    }
                }

            }
            sumCBDCPerMonth.add(sumCBDC);
        }
        return sumCBDCPerMonth;
    }
}
