package com.template.webserver.controllers;

import com.template.flows.model.*;
import com.template.model.endUser.EndUserData;
import com.template.model.transactions.RetailTransactions;
import com.template.states.commercialBankStates.CommercialBankState;
import com.template.states.endUserStates.EndUserState;
import com.template.webserver.model.EndUserUpdateModel;
import com.template.webserver.service.interfaces.EndUserInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.corda.core.contracts.StateAndRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 *  End user API endpoints.
 */

@Api(description="End User API endpoints")
@CrossOrigin("*")
@RestController
public class EndUserController {
    //private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(EndUserController.class);

    @Autowired
    private EndUserInterface endUserInterface;

    @ApiOperation(value = "Endpoint: /enduser/save ajoute un utilisateur final dans le réseau. Il retourne: un objet {AccountId, password } si ajout reussit et sinon null.")
    @PostMapping("/enduser/save")
    public AccountIdAndPassword save(@RequestBody EndUserAccountInfo endUserAccountInfo) {
      return endUserInterface.save(endUserAccountInfo);
    }

    @ApiOperation(value = "Endpoint: /enduser/saveotheraccount crée un autre compte d'un end user dans le réseau. Il retourne: un objet {AccountId, password } si ajout reussit et sinon null.")
    @PostMapping("/enduser/saveotheraccount")
    public AccountIdAndPassword createOtherEndUserCount(@RequestBody NewEndUserAccount newEndUserAccount){
        return endUserInterface.createOtherEndUserCount(newEndUserAccount);
    }

    @ApiOperation(value = "Endpoint: /enduser/update met à jour un utilisateur final dans le réseau. Il retourne : un objet {AccountId, password } et null sinon.")
    @PostMapping("/enduser/update")
    public AccountIdAndPassword update(@RequestBody EndUserUpdateModel endUserUpdateModel) {
        return endUserInterface.update(
                endUserUpdateModel.getEndUserData(),endUserUpdateModel.getEndUserAccountId());
    }

    @ApiOperation(value = "Endpoint: /enduser/deleteoractiveorswithacountytype desactif le compte du end user dans le réseau. Il retourne un int: 1 desactivation reussie et 0 sinon . En cas desactivation reussie,un email est envoyer au responsable de la banque centrale.")
    @PostMapping("/enduser/deleteoractiveorswithacountytype")
    public int suspendOrActiveOrSwithAccountType(@RequestBody SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
         return endUserInterface.suspendOrActiveOrSwithAccountType(suspendOrActiveOrSwithAccountType);
    }

    @ApiOperation(value = "Endpoint: /enduser/read lue un utilisateur final du réseau. Il retourne null en cas de lecture non reussie")
    @PostMapping("/enduser/read")
    public EndUserData getEndUser(@RequestBody String endUserAccountId) {
        return endUserInterface.read(endUserAccountId);
    }

    @GetMapping("/enduser/all")
    public List<StateAndRef<EndUserState>> getAll(){
        return endUserInterface.getAll();
    }

    @PostMapping("/enduser/sendcodeverification")
    public int sendEmailVerification(@RequestBody String email) throws MessagingException, IOException {
        return endUserInterface.sendEmailVerification(email);

    }

    @PostMapping("/enduser/notifyAdmin")

    public int sendNotification(@RequestBody EndUserData endUserData){
        return  endUserInterface.notifyAdmin(endUserData);
    }

    @PostMapping("/enduser/transaction")
    public RetailTransactions doTransaction(@RequestBody TransactionDetail transactionDetail){
        System.out.println("TX Data in controler :"+transactionDetail);
        return endUserInterface.doTransaction(transactionDetail);
    }
}
