package com.x1.xfactor.limbo;

public class SignalDataLte {
int rssi,rssnr,rssrp,rssrq,asu,mcc,mnc;
    SignalDataLte(int rssi , int rssrp,int rssrq,int rssnr,int asu,int mcc,int mnc ){
        this.rssi=rssi;
        this.rssnr=rssnr;
        this.rssrp=rssrp;
        this.rssrq=rssrq;
        this.mcc=mcc;
        this.mnc=mnc;
    }


    public int getRssi() {
        return rssi;
    }

    public int getRssnr() {
        return rssnr;
    }

    public int getRssrp() {
        return rssrp;
    }

    public int getRssrq() {
        return rssrq;
    }

    public int getAsu() {
        return asu;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }


}
