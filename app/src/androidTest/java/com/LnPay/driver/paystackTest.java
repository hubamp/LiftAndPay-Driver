package com.LnPay.driver;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.LnPay.driver.API.paystack.paystack;

import org.junit.Test;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;


public class paystackTest {

    public static String authUrl;
    public static String paymentRef;

    @Test
    public void postData() {

    }

    @Test
    public void testPostData() {
        paystack dataResults = new paystack();

        String email = "hubertAmp@gmail.com";
        double amount =5.2;

       paystack.postDataResults post = dataResults.postData(email,amount,"tp2cpThBQ5eqe6tj4ZLqxv1bFP02","tp2cpThBQ5eqe6tj4ZLqxv1bFP02 0");

        assertNotNull(post);
    }


    @Test
    public void testPostData1() {
    }

    @Test
    public void fetchData() {
        paystack fetchStack = new paystack();
        System.out.println("Helloaaa");
        assertNull(fetchStack.fetchData("tp2cpThBQ5eqe6tj4ZLqxv1bFP02"));
//       assertEquals("Successful",fetchStack.fetchData("tp2cpThBQ5eqe6tj4ZLqxv1bFP02"));
    }


}