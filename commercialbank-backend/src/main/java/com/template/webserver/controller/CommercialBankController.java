package com.template.webserver.controller;

import com.template.flows.model.*;
import com.template.model.commercialBank.CommercialBankData;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.webserver.model.CommercialBankUpdateModel;
import com.template.webserver.service.interfaces.CommercialBankInterface;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import net.corda.core.contracts.StateAndRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@Api(description ="Commercial bank API endpoints")
@RestController
public class CommercialBankController {

    private static final Logger logger = LoggerFactory.getLogger(CommercialBankController.class);


    @Autowired
    private CommercialBankInterface commercialBankInterface;

    @PostMapping("/commercialbank/create")
    public AccountIdAndPassword create(@RequestBody CommercialBankAccountInfo commercialBankAccountInfo){
        return commercialBankInterface.create(commercialBankAccountInfo);
    }

    @GetMapping("/commercialbank/{commercialBankAccountId}")
    public CommercialBankData getCommercialBank(@PathVariable(value = "commercialBankAccountId") String commercialBankAccountId){
        return commercialBankInterface.getCommercialBankById(commercialBankAccountId);
    }
    @GetMapping("/commercialbank/all")
    public List<StateAndRef<CommercialBankState>> getAll(){
        return commercialBankInterface.getAll();
    }

    @PostMapping("/commercialbank/update")
    public AccountIdAndPassword update(@RequestBody CommercialBankUpdateModel commercialBankUpdateModel){
        return commercialBankInterface.update(
                commercialBankUpdateModel.getCommercialBankData(),commercialBankUpdateModel.getCommercialBankAccountId());
    }

    @ApiOperation(value = "Endpoint: /commercialbank/deleteoractiveorswithacountytype desactif le compte de la banque, modifie, active dans le réseau. Il retourne un int: 1 desactivation reussie et 0 sinon . En cas desactivation reussie,un email est envoyer au responsable de la banque centrale.")
    @PostMapping("/commercialbank/deleteoractiveorswithacountytype")
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        return commercialBankInterface.suspendOrActiveOrSwithAccountType(suspendOrActiveOrSwithAccountType);
    }


    @ApiOperation(value = "Endpoint: /commercialbank/saveotheraccount crée un autre compte de la banque (donc une filiale) dans le réseau. Il retourne: un objet {AccountId, password } si ajout reussit et sinon null.")
    @PostMapping("/commercialbank/saveotheraccount")
    public AccountIdAndPassword createOtherMerchantCount(@RequestBody NewCommercialBankAccount newCommercialBankAccount){
        return commercialBankInterface.createOtherCommercialBankAccount(newCommercialBankAccount);
    }

}
