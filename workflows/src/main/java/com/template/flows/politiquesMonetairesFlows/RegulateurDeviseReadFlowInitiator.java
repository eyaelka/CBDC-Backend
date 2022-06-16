package com.template.flows.politiquesMonetairesFlows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.flows.model.CommonTreatment;
import com.template.model.politiquesMonetaires.RegulateurDevise;
import com.template.model.politiquesMonetaires.RegulateurMasseMonnetaire;
import com.template.states.politiquesMonetairesStates.RegulateurDeviseStates;
import com.template.states.politiquesMonetairesStates.RegulateurMasseMonnetaireStates;
import com.template.states.politiquesMonetairesStates.RegulateurTransactionInterPaysStates;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@InitiatingFlow
@StartableByRPC
public class RegulateurDeviseReadFlowInitiator extends FlowLogic<List<RegulateurDevise>> {

    private final String pays;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public RegulateurDeviseReadFlowInitiator(String pays) {

        this.pays =pays;
    }

    public ProgressTracker getProgressTracker(){
        return progressTracker;
    }

    @Suspendable
    @Override
    public List<RegulateurDevise> call() throws FlowException {


        List<StateAndRef<RegulateurDeviseStates>> allTxLocal =
                getServiceHub().getVaultService().queryBy(RegulateurDeviseStates.class).getStates();
        List<RegulateurDevise> filtered = new ArrayList<>();
        if (allTxLocal == null ){
            return null;
        }
        for (StateAndRef<RegulateurDeviseStates> reg : allTxLocal){
            if (reg.getState() != null && reg.getState().getData() != null && reg.getState().getData().getRegulateurDevise()!= null){
                RegulateurDeviseStates state = reg.getState().getData();
                if (state.getRegulateurDevise().getPays().equals(pays)) {
                    RegulateurDevise regulation = ifExist(filtered, reg.getState().getData().getRegulateurDevise());
                    if ( regulation == null ){
                        filtered.add(reg.getState().getData().getRegulateurDevise());
                    }else{
                        if (CommonTreatment.stringCompare(regulation.getDate(),reg.getState().getData().getRegulateurDevise().getDate()) <= 0){
                            filtered = deleteOldRegulation(filtered,reg.getState().getData().getRegulateurDevise());

                        }

                    }
                }
            }

        }
        System.out.println("filtered \n"+filtered);
        return filtered;
    }


    public RegulateurDevise ifExist(List<RegulateurDevise> list , RegulateurDevise reg){

        if (list != null){
            for (int i =0; i<= list.size()-1; i++){
                if (list.get(i).getNom().equalsIgnoreCase(reg.getNom())){
                    return reg;
                }
            }
        }
        return null;
    }

    public List<RegulateurDevise> deleteOldRegulation (List<RegulateurDevise> listReg, RegulateurDevise reg){
        List<RegulateurDevise> temp = new ArrayList<>();
        if (listReg != null ) {
            for (int i = 0 ; i<= listReg.size()-1; i++){
                if ( ! listReg.get(i).getNom().equalsIgnoreCase(reg.getNom())){
                    temp.add(listReg.get(i));
                }
            }
            temp.add(reg);
            return temp;

        }

        return null;
    }
}



