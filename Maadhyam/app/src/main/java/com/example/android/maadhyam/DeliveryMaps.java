package com.example.android.maadhyam;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DeliveryMaps extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager loc_gps;
    LatLng current_location;
    Button confirm_location;
    Intent i_data;
    Location loc;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_maps);

         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirm_location = findViewById(R.id.confirm_location);
        confirm_location.setEnabled(false);
        i_data = new Intent();

        confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1,i_data);
                finish();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        View mapView = mapFragment.getView();
        if (mapView != null && mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 0, 200);
        }


        loc_gps = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        else {
            mMap.setMyLocationEnabled(true);
            mMap.setMinZoomPreference(5);

            loc = loc_gps.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null)
                loc_gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            else {
                current_location = new LatLng(loc.getLatitude(), loc.getLongitude());
                MarkerOptions s = new MarkerOptions().position(current_location).title("Your Location").draggable(true);
                mMap.addMarker(s);
                i_data.putExtra("latitude", "" + current_location.latitude);
                i_data.putExtra("longitude", "" + current_location.longitude);
                confirm_location.setEnabled(true);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
            }
            UiSettings mUiSet = mMap.getUiSettings();
            mUiSet.setAllGesturesEnabled(true);
            mUiSet.setMyLocationButtonEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    current_location = latLng;
                    mMap.clear();
                    MarkerOptions s = new MarkerOptions().position(latLng).title("Delivery Location").draggable(true);
                    i_data.putExtra("latitude", "" + current_location.latitude);
                    i_data.putExtra("longitude", "" + current_location.longitude);
                    confirm_location.setEnabled(true);
                    mMap.addMarker(s);

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        loc = location;

    }
}