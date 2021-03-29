package com.x1.xfactor.limbo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<String> keylist;
    List<SignalDataLte> ltelist;
    SignalDataLte obj;
    DatabaseReference q, r, dref;
    Double lat, lon;
    LocationManager mLocationManager;
    LocationListener locListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i("droid", "started");
        keylist = new ArrayList<>();
        ltelist = new ArrayList<>();
        lat=10.416684;
        lon=77.901547;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        r=FirebaseDatabase.getInstance().getReference();
        dref= r.child("reports");
        DatabaseReference georef= FirebaseDatabase.getInstance().getReference("geo/data");
        GeoFire geoFire= new GeoFire(georef);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(lat,lon),0.5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.i("dom",key+"//"+location.latitude+"//"+location.longitude);
        keylist.add(key);
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {
        Log.i("keylistsize",""+keylist.size());
queryData();

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }
});


        mMap = googleMap;
        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("YOUR LOCATION"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //**************



        Log.i("map","test"+ltelist.size());

        mMap.addCircle(new CircleOptions()
                .center(sydney)
                .radius(500)
                .strokeColor(Color.parseColor("#2900cc00")))
                .setFillColor(Color.parseColor("#2200cc00"));
        Log.i("mapstag1","end");

    }

   void queryData(){
       int i;

       for(i=0;i<keylist.size();i++){
           dref.child(keylist.get(i)).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
int s= dataSnapshot.getValue(SignalDataLte.class).getMcc();
ltelist.add(dataSnapshot.getValue(SignalDataLte.class));
Log.i("logmessage","//"+s+"/id/"+ltelist.size());

LatLng reportsloc= new LatLng(dataSnapshot.getValue(SignalDataLte.class).getLat(),dataSnapshot.getValue(SignalDataLte.class).getLon());
mMap.addMarker(new MarkerOptions().position(reportsloc).title(String.valueOf(dataSnapshot.getValue(SignalDataLte.class).getRssi())));


               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

       }
   }

    private class MyLocationListener implements LocationListener{


        @Override
        public void onLocationChanged(Location location) {
            lon=location.getLongitude();
            lat=location.getLatitude();
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
