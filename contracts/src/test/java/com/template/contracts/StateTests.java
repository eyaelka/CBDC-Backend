package com.template.contracts;

import org.junit.Test;

public class StateTests {

    //Mock State test check for if the state has correct parameters type
    @Test
    public void hasFieldOfCorrectType() throws NoSuchFieldException {
        CentralBankStoreState.class.getDeclaredField("msg");
        assert (CentralBankStoreState.class.getDeclaredField("msg").getType().equals(String.class));
    }
}