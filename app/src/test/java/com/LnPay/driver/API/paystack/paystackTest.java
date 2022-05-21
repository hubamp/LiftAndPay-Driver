package com.LnPay.driver.API.paystack;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.content.Context;

import org.junit.Test;


public class paystackTest {

    public static String authUrl;
    public static String paymentRef;

    @Test
    public void postData() {

    }

    @Test
    public void testPostData() {
        Context context = new Activity();
        paystack dataResults = new paystack(context);

        String email = "hubertAmp@gmail.com";
        double amount = 5.2;

        paystack.postDataResults post = dataResults.postData(email, amount, "tp2cpThBQ5eqe6tj4ZLqxv1bFP02", "tp2cpThBQ5eqe6tj4ZLqxv1bFP02 0");

        assertNotNull(post);
    }


    @Test
    public void testPostData1() {

        int i = 0;
        for (int j = 0; j <10 ; j++) {

            if (j%2==0)
            {
                System.out.println("Test "+ i);

            }
            else{
                System.out.println("Read "+i);
            }
            System.out.println(j);
        }

        assertEquals(i,0);
    }

 /*   @Test
    public void fetchData() {
        paystack fetchStack = new paystack();
        System.out.println("Helloaaa");
        assertNull(fetchStack.fetchData("tp2cpThBQ5eqe6tj4ZLqxv1bFP02"));
//       assertEquals("Successful",fetchStack.fetchData("tp2cpThBQ5eqe6tj4ZLqxv1bFP02"));
    }*/


}