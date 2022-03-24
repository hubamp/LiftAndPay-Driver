package com.LnPay.driver.History;

public class historyModel {
    private String JOURNEY;
    private String RIDEDATE;
    private String RIDETIME;
    private String RIDESTATUS;
    private String DOCUMENTID;
    private String STLAT,STLON,ENDLAT,ENDLON;


    public historyModel(String JOURNEY,
                              String RIDEDATE,
                              String RIDETIME,
                              String NUMBEROFREQUESTS,
                              String DOCUMENTID,
                              String STLAT,
                              String STLON,
                              String ENDLAT,
                              String ENDLON)
    {
        this.JOURNEY = JOURNEY;
        this.RIDEDATE = RIDEDATE;
        this.RIDETIME = RIDETIME;
        this.RIDESTATUS = NUMBEROFREQUESTS;
        this.DOCUMENTID = DOCUMENTID;
        this.STLAT = STLAT;
        this.STLON = STLON;
        this.ENDLAT = ENDLAT;
        this.ENDLON = ENDLON;
    }

    public String getJOURNEY() {
        return JOURNEY;
    }

    public void setJOURNEY(String JOURNEY) {
        this.JOURNEY = JOURNEY;
    }

    public String getRIDEDATE() {
        return RIDEDATE;
    }

    public void setRIDEDATE(String RIDEDATE) {
        this.RIDEDATE = RIDEDATE;
    }

    public String getRIDETIME() {
        return RIDETIME;
    }

    public void setRIDETIME(String RIDETIME) {
        this.RIDETIME = RIDETIME;
    }

    public String getRIDESTATUS() {
        return RIDESTATUS;
    }

    public void setRIDESTATUS(String RIDESTATUS) {
        this.RIDESTATUS = RIDESTATUS;
    }

    public String getDOCUMENTID() {
        return DOCUMENTID;
    }

    public void setDOCUMENTID(String DOCUMENTID) {
        this.DOCUMENTID = DOCUMENTID;
    }

    public String getSTLAT() {
        return STLAT;
    }

    public void setSTLAT(String STLAT) {
        this.STLAT = STLAT;
    }

    public String getSTLON() {
        return STLON;
    }

    public void setSTLON(String STLON) {
        this.STLON = STLON;
    }

    public String getENDLAT() {
        return ENDLAT;
    }

    public void setENDLAT(String ENDLAT) {
        this.ENDLAT = ENDLAT;
    }

    public String getENDLON() {
        return ENDLON;
    }

    public void setENDLON(String ENDLON) {
        this.ENDLON = ENDLON;
    }
}
