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
    List<CellInfo> allCellInfo,regCellinfo;
    List<SubscriptionInfo> subInfo;
    Object mainObj = null;

    CellSignalStrengthCdma cdmaSignal;
    CellSignalStrengthLte lteSignal;
    CellSignalStrengthGsm gsmSignal;
    CellSignalStrengthWcdma wcdmaSignal;
    CellIdentityGsm gsmId;
    CellIdentityLte lteId;
    CellIdentityWcdma wcdmaid;
    CellIdentityCdma cdmaId;



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

        permissionCheck(2);

        SubscriptionManager s = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        allCellInfo = telephonyManager.getAllCellInfo();
        regCellinfo=new ArrayList<>();
   subInfo = s.getActiveSubscriptionInfoList();
        for (SubscriptionInfo info : subInfo) {
            info.getCarrierName();
            info.getCountryIso();
            info.getDataRoaming();
            info.getMcc();
            info.getMnc();
        }

       for(i=0;i<allCellInfo.size();i++){

           if(allCellInfo.get(i).isRegistered()){
               regCellinfo.add(allCellInfo.get(i));
           }
       }




        // ##########################Listener for the signal strength###########################
        final PhoneStateListener mListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength sStrength) {
                signalStrength = sStrength;
                getSignalStrength();
            }
        };

        // Register the listener for the telephony manager
        telephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        Log.i("Dom","out"+subInfo.size());

simCardDetails(); //gets the sim card detiails and assigns to the cards

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

                        lteSignal = ((CellInfoLte) regCellinfo.get(i)).getCellSignalStrength();
                        lteSignal.getDbm(); //rssi
                        lteSignal.getRsrp();            //**** add mcc mnc in signal cards ***
                        lteSignal.getRsrq();
                        lteSignal.getRssnr();
                        lteSignal.getAsuLevel();


                    } else if (regCellinfo.get(i) instanceof CellInfoCdma) {
                        cdmaSignal = ((CellInfoCdma) regCellinfo.get(i)).getCellSignalStrength();
                        cdmaSignal.getCdmaDbm();
                        cdmaSignal.getAsuLevel();


                    } else if (regCellinfo.get(i) instanceof CellInfoGsm) {

                        gsmSignal = ((CellInfoGsm) regCellinfo.get(i)).getCellSignalStrength();
                        gsmSignal.getDbm(); //rssi
                        gsmSignal.getAsuLevel();


                    }
                    else if(regCellinfo.get(i) instanceof CellInfoWcdma){
                        wcdmaSignal=((CellInfoWcdma) regCellinfo.get(i)).getCellSignalStrength();
                        wcdmaSignal.getDbm();
                        wcdmaSignal.getAsuLevel();

                    }
                }

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
        tvOperator1.setText(subInfo.get(0).getCarrierName());
        tvMccMnc1.setText(Integer.toString(subInfo.get(0).getMcc())+Integer.toString(subInfo.get(0).getMnc()));

        tvOperator2.setText(subInfo.get(1).getCarrierName());
        tvMccMnc2.setText(Integer.toString(subInfo.get(1).getMcc())+Integer.toString(subInfo.get(1).getMnc()));

    }


}
