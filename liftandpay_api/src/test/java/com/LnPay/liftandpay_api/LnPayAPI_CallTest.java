package com.LnPay.liftandpay_api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LnPayAPI_CallTest extends myObserver {

   driverModel driverModel;

    @Test
    public void getDriver() {

        myObserver newO = new myObserver();
        LnPayAPI_Call callFace = new LnPayAPI_Call();

        assertNotNull(callFace.getDriver());
     //   System.out.println("INFO: " + "tag" + ": " + callFace.hasChanged());

    }



}