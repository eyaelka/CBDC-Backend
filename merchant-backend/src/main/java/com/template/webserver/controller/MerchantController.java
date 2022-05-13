package com.template.webserver.controller;

import com.template.flows.model.*;
import com.template.model.endUser.EndUserData;
import com.template.model.merchant.MerchantData;
import com.template.webserver.model.MerchantAccountModel;
import com.template.webserver.model.MerchantUpdateModel;
import com.template.webserver.service.interfaces.MerchantInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@Api(description ="Merchant API endpoints")
@CrossOrigin("*")
@RestController
public class MerchantController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);

    @Autowired
    private MerchantInterface merchantInterface;

    @PostMapping("/merchant/create")
    public AccountIdAndPassword create(@RequestBody MerchantAccountInfo merchantAccountInfo){

        return merchantInterface.create(merchantAccountInfo);
    }

    @GetMapping("/merchant/{merchantAccountId}")
    public MerchantData getMerchant(@PathVariable(value = "merchantAccountId") String merchantAccountId){
        return merchantInterface.getMerchantById(merchantAccountId);
    }

    @PostMapping("/merchant/update")
    public AccountIdAndPassword update(@RequestBody MerchantUpdateModel merchantUpdateModel){
        return merchantInterface.update(
                merchantUpdateModel.getMerchantData(),merchantUpdateModel.getMerchantAccountId());
    }

    @PostMapping("/merchant/sendcodeverification")
    public int sendEmailVerification(@RequestBody String email) throws MessagingException, IOException {
        return merchantInterface.sendEmailVerification(email);

    }

    @ApiOperation(value = "Endpoint: /merchant/saveotheraccount crée un autre compte d'une entreprise dans le réseau. Il retourne: un objet {AccountId, password } si ajout reussit et sinon null.")
    @PostMapping("/merchant/saveotheraccount")
    public AccountIdAndPassword createOtherMerchantCount(@RequestBody NewMerchantAccount newMerchantAccount){
        return merchantInterface.createOtherMerchantAccount(newMerchantAccount);
    }

    @ApiOperation(value = "Endpoint: /merchant/deleteoractiveorswithacountytype desactif le compte du merchant dans le réseau. Il retourne un int: 1 desactivation reussie et 0 sinon . En cas desactivation reussie,un email est envoyer au responsable de la banque centrale.")
    @PostMapping("/merchant/deleteoractiveorswithacountytype")
    public int suspendOrActiveOrSwithAccountType(SuspendOrActiveOrSwithAccountType suspendOrActiveOrSwithAccountType) {
        return merchantInterface.suspendOrActiveOrSwithAccountType(suspendOrActiveOrSwithAccountType);
    }

    @PostMapping("/merchant/notifyAdmin")
    //changer le modele enduser pour ajouter les champs manquant
    //ajoute une nouvelle classe (modele dans le front et dans le back)
    public int sendNotification(@RequestBody MerchantAccountModel merchantAccountModel){
        return  merchantInterface.notifyAdmin(merchantAccountModel);
    }
}
