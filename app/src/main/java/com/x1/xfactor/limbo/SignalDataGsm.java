package com.x1.xfactor.limbo;

public class SignalDataGsm {
    int rssi,asu,mcc,mnc;

    SignalDataGsm(int rssi,int asu,int mnc,int mcc){
        this.rssi=rssi;
        this.asu=asu;
        this.mcc=mcc;
        this.mnc=mnc;
    }

    public int getMnc() {
        return mnc;
    }

    public int getMcc() {
        return mcc;
    }

    public int getAsu() {
        return asu;
    }

    public int getRssi() {
        return rssi;
    }
}
