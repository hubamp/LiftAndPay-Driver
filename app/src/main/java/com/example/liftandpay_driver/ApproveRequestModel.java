package com.example.liftandpay_driver;

import android.widget.CheckBox;

public class ApproveRequestModel {
    private String  PASSENGERNAME;
    private String PASSENGERNUMBER;
    private CheckBox APPROVECHECK;

    public ApproveRequestModel(String PASSENGERNAME, String PASSENGERNUMBER) {
        this.PASSENGERNAME = PASSENGERNAME;
        this.PASSENGERNUMBER = PASSENGERNUMBER;
        this.APPROVECHECK = APPROVECHECK;
    }

    public String getPASSENGERNAME() {
        return PASSENGERNAME;
    }

    public void setPASSENGERNAME(String PASSENGERNAME) {
        this.PASSENGERNAME = PASSENGERNAME;
    }

    public String getPASSENGERNUMBER() {
        return PASSENGERNUMBER;
    }

    public void setPASSENGERNUMBER(String PASSENGERNUMBER) {
        this.PASSENGERNUMBER = PASSENGERNUMBER;
    }

    public CheckBox getAPPROVECHECK() {
        return APPROVECHECK;
    }

    public void setAPPROVECHECK(CheckBox APPROVECHECK) {
        this.APPROVECHECK = APPROVECHECK;
    }
}
