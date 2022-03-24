package com.LnPay.driver.API.paystack;

import junit.framework.TestCase;

import org.junit.Test;

public class paystackTest extends TestCase {

    public static String authUrl;
    public static String paymentRef;

    @Test
    public void postData() {



    }

    @Test
    public void testPostData() {
        paystack dataResults;

        String email = "hubertAmp";
        double amount =1.2;

        dataResults = new paystack().postData(email,amount);

        assertNotNull(dataResults);
    }
}