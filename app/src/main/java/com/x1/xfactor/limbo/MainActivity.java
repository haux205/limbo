package com.x1.xfactor.limbo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    //Variables for signal data
    TextView tvOperator1, tvSimState1, tvServState1, tvNetType1, tvData1, tvMccMnc1, tvRoaming1;
    TextView tvOperator2, tvSimState2, tvServState2, tvNetType2, tvData2, tvMccMnc2, tvRoaming2;

    private SignalStrength signalStrength;
    private TelephonyManager telephonyManager;
    private SubscriptionManager subManager;

    List<CellInfo> allCellInfo, regCellinfo;
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

    DatabaseReference mData,mData2, georef;
    String keyvalue;

    LocationManager mLocationManager;
    LocationListener locListener;
    Double lon, lat;
    SignalDataLte lteReport;


    MyPhoneStateListener mPhoneStatelistener;

    private final static String LTE_TAG = "LTE_Tag";
    private final static String LTE_SIGNAL_STRENGTH = "getLteSignalStrength";
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};

    int i;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
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
        tvOperator1 = findViewById(R.id.tvOperator1);
        tvSimState1 = findViewById(R.id.tvSim1State);
        tvServState1 = findViewById(R.id.tvServ_State1);
        tvNetType1 = findViewById(R.id.tvNetworkType1);
        tvData1 = findViewById(R.id.tvMobileData1);
        tvMccMnc1 = findViewById(R.id.tvMccMnc1);
        tvRoaming1 = findViewById(R.id.tvRoaming1);

        tvOperator2 = findViewById(R.id.tvOperator2);
        tvSimState2 = findViewById(R.id.tvSim2State);
        tvServState2 = findViewById(R.id.tvServ_State2);
        tvNetType2 = findViewById(R.id.tvNetworkType2);
        tvData2 = findViewById(R.id.tvMobileData2);
        tvMccMnc2 = findViewById(R.id.tvMccMnc2);
        tvRoaming2 = findViewById(R.id.tvRoaming2);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locListener);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStatelistener = new MyPhoneStateListener();


        subManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        regCellinfo = new ArrayList<>();
        networkTypeList = new ArrayList<>();
        subInfo = new ArrayList<>();
        dbm = new ArrayList<>();
        subInfo.add(subManager.getActiveSubscriptionInfoForSimSlotIndex(0));
        subInfo.add(subManager.getActiveSubscriptionInfoForSimSlotIndex(1));
        for (SubscriptionInfo info : subInfo) {
            info.getCarrierName();
            info.getCountryIso();
            info.getDataRoaming();
            info.getMcc();
            info.getMnc();

        }
        telephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        allCellInfo = telephonyManager.getAllCellInfo();
        for (i = 0; i < allCellInfo.size(); i++) {

            if (allCellInfo.get(i).isRegistered()) {
                regCellinfo.add(allCellInfo.get(i));
            }
        }


        simCardDetails(); //gets the sim card details and assigns to the cards
        allCellInfo.clear();
        regCellinfo.clear();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mData = FirebaseDatabase.getInstance().getReference();

                mData.child("reports");
                keyvalue = mData.push().getKey();
                mData2=FirebaseDatabase.getInstance().getReference().child("reports").child(keyvalue);

                mData2.setValue(lteReport);
                georef = FirebaseDatabase.getInstance().getReference().child("geo").child("data");
                GeoFire geoFire = new GeoFire(georef);
                geoFire.setLocation(keyvalue, new GeoLocation(lat, lon), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });

                Snackbar.make(view, "Signal report updated to the database ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



//****************>> end of onCreate <<*********************************
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
                         //rssi
                                    //**** add mcc mnc in signal cards ***
                        lteReport=new SignalDataLte(lteSignal.getDbm(),lteSignal.getRsrp(),lteSignal.getRsrq(),lteSignal.getRssnr(),lteSignal.getAsuLevel(),405,869,lat,lon);

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

   private class MyLocationListener implements LocationListener{


       @Override
       public void onLocationChanged(Location location) {
           lon=location.getLongitude();
           lat=location.getLatitude();
           Log.i("Domout","test2");
       }

       @Override
       public void onStatusChanged(String s, int i, Bundle bundle) {

       }

       @Override
       public void onProviderEnabled(String s) {

       }

       @Override
       public void onProviderDisabled(String s) {

       }
   }
}


