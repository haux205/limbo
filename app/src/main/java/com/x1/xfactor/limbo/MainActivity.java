package com.x1.xfactor.limbo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.kirianov.multisim.MultiSimTelephonyManager;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import android.telephony.SignalStrength;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    //Variables for signal data
    TextView tvOperator1,tvSimState1,tvServState1,tvNetType1,tvData1,tvMccMnc1,tvRoaming1;
    TextView tvOperator2,tvSimState2,tvServState2,tvNetType2,tvData2,tvMccMnc2,tvRoaming2;

    private SignalStrength signalStrength;
    private TelephonyManager telephonyManager;
    private SubscriptionManager subManager;

    List<CellInfo> allCellInfo,regCellinfo;
    List<String> networkTypeList;
    List<SubscriptionInfo> subInfo;
    List<Integer> dbm;
    Object mainObj = null;

    CellSignalStrengthCdma cdmaSignal;
    CellSignalStrengthLte lteSignal;
    CellSignalStrengthGsm gsmSignal;
    CellSignalStrengthWcdma wcdmaSignal;
    CellIdentityGsm gsmId;
    CellIdentityLte lteId;
    CellIdentityWcdma wcdmaid;
    CellIdentityCdma cdmaId;

MyPhoneStateListener mPhoneStatelistener;

    private final static String LTE_TAG = "LTE_Tag";
    private final static String LTE_SIGNAL_STRENGTH = "getLteSignalStrength";

    int i;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //  NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //  NavigationUI.setupWithNavController(navigationView, navController);

//###############################Start of main part of coding#########################################
        tvOperator1=findViewById(R.id.tvOperator1);
        tvSimState1=findViewById(R.id.tvSim1State);
        tvServState1=findViewById(R.id.tvServ_State1);
        tvNetType1=findViewById(R.id.tvNetworkType1);
        tvData1=findViewById(R.id.tvMobileData1);
        tvMccMnc1=findViewById(R.id.tvMccMnc1);
        tvRoaming1=findViewById(R.id.tvRoaming1);

        tvOperator2=findViewById(R.id.tvOperator2);
        tvSimState2=findViewById(R.id.tvSim2State);
        tvServState2=findViewById(R.id.tvServ_State2);
        tvNetType2=findViewById(R.id.tvNetworkType2);
        tvData2=findViewById(R.id.tvMobileData2);
        tvMccMnc2=findViewById(R.id.tvMccMnc2);
        tvRoaming2=findViewById(R.id.tvRoaming2);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStatelistener = new MyPhoneStateListener();

        permissionCheck(2);

        subManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        regCellinfo=new ArrayList<>();
        networkTypeList=new ArrayList<>();
        subInfo=new ArrayList<>();
        dbm= new ArrayList<>();
        subInfo.add(subManager.getActiveSubscriptionInfoForSimSlotIndex(0));
        subInfo.add(subManager.getActiveSubscriptionInfoForSimSlotIndex(1));
        for (SubscriptionInfo info : subInfo) {
            info.getCarrierName();
            info.getCountryIso();
            info.getDataRoaming();
            info.getMcc();
            info.getMnc();

        }




PhoneStateListener mListener= new PhoneStateListener(){
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        Log.i("Domout","test");


    }
};



        // Register the listener for the telephony manager
        telephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        allCellInfo = telephonyManager.getAllCellInfo();
        for(i=0;i<allCellInfo.size();i++){

            if(allCellInfo.get(i).isRegistered()){
                regCellinfo.add(allCellInfo.get(i));
            }
        }


        simCardDetails(); //gets the sim card details and assigns to the cards
allCellInfo.clear();
regCellinfo.clear();
//****************>> end of onCreate <<*********************************
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

  /*  @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    } */

    @SuppressLint("MissingPermission")
    private void getSignalStrength() {
        try {
            Method[] methods = android.telephony.SignalStrength.class.getMethods();
            allCellInfo = telephonyManager.getAllCellInfo();
            for(i=0;i<allCellInfo.size();i++){

                if(allCellInfo.get(i).isRegistered()){
                    regCellinfo.add(allCellInfo.get(i));
                }
            }

            for (Method mthd : methods) {
                /*if (mthd.getName().equals(LTE_SIGNAL_STRENGTH))
                {
                    Log.i("test",mthd.getName());
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    Log.i(LTE_TAG, "signalStrength = " + LTEsignalStrength);
                    return;
                }*/


                int i = 0;

                for (i = 0; i < regCellinfo.size(); i++) {

                    if (regCellinfo.get(i) instanceof CellInfoLte) {
                        networkTypeList.add(i,"lte");

                        lteSignal = ((CellInfoLte) regCellinfo.get(i)).getCellSignalStrength();
                        dbm.add(i,lteSignal.getDbm());
                        lteSignal.getDbm(); //rssi
                        lteSignal.getRsrp();            //**** add mcc mnc in signal cards ***
                        lteSignal.getRsrq();
                        lteSignal.getRssnr();
                        lteSignal.getAsuLevel();


                    } else if (regCellinfo.get(i) instanceof CellInfoCdma) {
                        networkTypeList.add(i,"cdma");
                        cdmaSignal = ((CellInfoCdma) regCellinfo.get(i)).getCellSignalStrength();
                        cdmaSignal.getCdmaDbm();
                        cdmaSignal.getAsuLevel();


                    } else if (regCellinfo.get(i) instanceof CellInfoGsm) {
                        networkTypeList.add(i,"gsm");

                        gsmSignal = ((CellInfoGsm) regCellinfo.get(i)).getCellSignalStrength();
                        dbm.add(i,gsmSignal.getDbm());
                        gsmSignal.getDbm(); //rssi
                        gsmSignal.getAsuLevel();


                    }
                    else if(regCellinfo.get(i) instanceof CellInfoWcdma){
                        networkTypeList.add(i,"wcdma");
                        wcdmaSignal=((CellInfoWcdma) regCellinfo.get(i)).getCellSignalStrength();
                        wcdmaSignal.getDbm();
                        wcdmaSignal.getAsuLevel();

                    }


                }
                Log.i("marty",Integer.toString(dbm.get(0))+Integer.toString(dbm.get(1)));
                regCellinfo.clear();
                allCellInfo.clear();

            }
        }
        catch (Exception e)
        {
            Log.e(LTE_TAG, "Exception: " + e.toString());
        }
    }
void permissionCheck(int flag){
        switch (flag) {
            case 1:   if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;

            }
            case 2: if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.


                return;
            }
        }
}
    private void simCardDetails() {
        int state1,state2;
        tvOperator1.setText(subInfo.get(0).getCarrierName());
        tvMccMnc1.setText(Integer.toString(subInfo.get(0).getMcc())+Integer.toString(subInfo.get(0).getMnc()));

        tvOperator2.setText(subInfo.get(1).getCarrierName());
        tvMccMnc2.setText(Integer.toString(subInfo.get(1).getMcc())+Integer.toString(subInfo.get(1).getMnc()));

        // Roaming status for sim 1 and 2
            if(subInfo.get(0).getDataRoaming()==SubscriptionManager.DATA_ROAMING_ENABLE){
            // data roaming is enabled for sim 1
                tvRoaming1.setText("roaming");
            }else{
                // data roaming disabled sim 1
                tvRoaming1.setText("Not roaming");
            }

            if(subInfo.get(1).getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE){
                // data roaming enabled for sim 2
                tvRoaming2.setText("roaming");
            }
            else{
                // data roaming disabled for sim 2
                tvRoaming2.setText("Not roaming");
            }
         state1=telephonyManager.getSimState(0);  //>>deprecated>>getDeviceId(int slotIndex)
         state2=telephonyManager.getSimState(1);

         // Sim states

        switch (state1) {
            case TelephonyManager.SIM_STATE_ABSENT:
                tvSimState1.setText("Sim state absent");
                break;
            case TelephonyManager.SIM_STATE_READY:
                tvSimState1.setText("Sim State Ready");
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                tvSimState1.setText("Sim state unknown");
                break;
        }
        switch (state2) {
            case TelephonyManager.SIM_STATE_ABSENT:
                tvSimState2.setText("Sim state absent");
                break;
            case TelephonyManager.SIM_STATE_READY:
                tvSimState2.setText("Sim State Ready");
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                tvSimState2.setText("Sim state unknown");
                break;
        }

        // network type fo the sims

        for (i = 0; i < regCellinfo.size(); i++) {

            if (regCellinfo.get(i) instanceof CellInfoLte) {
                networkTypeList.add(i,"lte");
            } else if (regCellinfo.get(i) instanceof CellInfoCdma) {
                networkTypeList.add(i,"cdma");

            } else if (regCellinfo.get(i) instanceof CellInfoGsm) {
                networkTypeList.add(i,"gsm");
            }
            else if(regCellinfo.get(i) instanceof CellInfoWcdma){
                networkTypeList.add(i,"wcdma");
            }
        }
        tvNetType1.setText(networkTypeList.get(0));
        tvNetType2.setText(networkTypeList.get(1));

        //Data connection status

    }

void assignDbm(){
        tvServState1.setText(Integer.toString(dbm.get(0)));
        tvServState2.setText(Integer.toString(dbm.get(1)));


}

    class MyPhoneStateListener extends PhoneStateListener {
        MainActivity obj;
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            obj=new MainActivity();
            obj.getSignalStrength();
            Log.i("Domout","test");
            getSignalStrength();
        }
    }
}


