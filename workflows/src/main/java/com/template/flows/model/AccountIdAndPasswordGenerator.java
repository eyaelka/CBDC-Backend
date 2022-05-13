package com.template.flows.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.corda.core.serialization.CordaSerializable;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CordaSerializable
@EqualsAndHashCode
public class AccountIdAndPasswordGenerator {

    private String indicator;

    public AccountIdAndPassword generateAccountIdAdnPassword(){
        Random random = new Random();
        String specialeCaracters = ",;:!.*/+-@_£$%?&#|<>{}])[(=";
        int accountId = 10000000 + random.nextInt(100000000);
        int passwordInt = 10000000 + random.nextInt(100000000);

        int specialeCaracterToPutInPassowordIndex =0 + random.nextInt(specialeCaracters.length());

        String centralBankAccountId = accountId+indicator;

        Character specialeCaracterToPutInPassoword = specialeCaracters.charAt(specialeCaracterToPutInPassowordIndex);

        int specialeCaracterPositionInPassoword = specialeCaracterToPutInPassowordIndex % 8;

        String pw = String.valueOf(passwordInt);

        String subPassword1 =  pw.substring(0,specialeCaracterPositionInPassoword);

        String subPassword2 =  pw.substring(specialeCaracterPositionInPassoword);
        subPassword1 = subPassword1+specialeCaracterToPutInPassoword;
        String password = subPassword1+subPassword2;
        return new AccountIdAndPassword(centralBankAccountId, password);
    }
}
