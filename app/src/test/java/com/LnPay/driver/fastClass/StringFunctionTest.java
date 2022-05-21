package com.LnPay.driver.fastClass;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringFunctionTest {

    @Test
    public void splitStringWithAndGet() {

       String myString = "https://checkout.paystack.com/fzu78mttodbqjvl";

       assertNotEquals("fzu78mttodbqjvl",new StringFunction(myString).splitStringWithAndGet("paystack.com/",1));
    }
}