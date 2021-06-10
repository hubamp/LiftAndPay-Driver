package com.example.liftandpay_driver.fastClass;

public class StringFunction {

    private final String theString;

    public StringFunction(String theString) {
        this.theString = theString;
    }



    public String removeLastNumberOfCharacter(int numberOfCharacters) {
        return (this.theString == null || this.theString.length() == 0)
                ? null
                : (this.theString.substring(0, this.theString.length() - numberOfCharacters));
    }


    public String splitStringWithAndGet(String sepratedBy, int returnIndex) {
            String[] arrOfStr = theString.split(sepratedBy);
            if (returnIndex < arrOfStr.length)
            {
                return arrOfStr[returnIndex];
            }

            return "OUT_OF_BOUNDS";
        }



}
