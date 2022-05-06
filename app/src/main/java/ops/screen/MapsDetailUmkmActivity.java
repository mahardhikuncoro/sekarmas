package ops.screen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import base.network.callback.NetworkConnection;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import ops.screen.fragment.UmkmDetailActivity;

public class MapsDetailUmkmActivity extends FragmentActivity implements OnMapReadyCallback{

    Marker options;
    ImageView ivSelectPosition;

    double lat = 0.0;
    double lng = 0.0;
    String alamat = "";
    String kabupaten="";
    String namaUmkm="";

    @BindView(R.id.iv_select_location)
    ImageView ivSelectedPOsition;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        lng = getIntent().getDoubleExtra("LONGITUDE",0F);
        lat = getIntent().getDoubleExtra("LATITUDE",0F);
        namaUmkm = getIntent().getStringExtra("NAMA_UMKM");

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        if(!getIntent().getBooleanExtra("IS_UPDATE",false))
            ivSelectedPOsition.setVisibility(View.GONE);
        getLocationPermission();
        ivSelectPosition = (ImageView) findViewById(R.id.iv_select_location);
    }

    @OnClick(R.id.iv_select_location)
    public void getLocation(){
        Log.e("CLICK HERE "," YAA ");
        Intent openMap;
        if(getIntent().getBooleanExtra("IS_UPDATE",false)) {
            openMap = new Intent(MapsDetailUmkmActivity.this, UpdateUmkm.class);
            openMap.putExtra("ID_UMKM",getIntent().getStringExtra("ID_UMKM"));
            openMap.putExtra("SEKTOR_POSITION",getIntent().getExtras().getInt("SEKTOR_POSITION"));
            openMap.putExtra("LAPORAN_FOTO",getIntent().getExtras().getString("LAPORAN_FOTO"));
            openMap.putExtra("IS_IMAGE_GALERY",getIntent().getExtras().getString("IS_IMAGE_GALERY"));
            openMap.putExtra("NAMA_SIDEBARU",getIntent().getExtras().getString("NAMA_SIDEBARU"));
            openMap.putExtra("ALAMAT_SIDEBARU",alamat);
            openMap.putExtra("EMAIL",getIntent().getExtras().getString("EMAIL"));
            openMap.putExtra("TELEPON",getIntent().getExtras().getString("TELEPON"));
            openMap.putExtra("HANDPHONE",getIntent().getExtras().getString("HANDPHONE"));
            openMap.putExtra("PROVINSI",getIntent().getExtras().getString("PROVINSI"));
            openMap.putExtra("KABUPATEN",getIntent().getExtras().getString("KABUPATEN"));
            openMap.putExtra("KECAMATAN",getIntent().getExtras().getString("KECAMATAN"));
            openMap.putExtra("KELURAHAN",getIntent().getExtras().getString("KELURAHAN"));
            openMap.putExtra("KODEPOS",getIntent().getExtras().getString("KODEPOS"));
            openMap.putExtra("DESKRIPSI",getIntent().getExtras().getString("DESKRIPSI"));
            openMap.putExtra("IS_UPDATE",getIntent().getBooleanExtra("IS_UPDATE",false));
            openMap.putExtra("LONGITUDE", lng);
            openMap.putExtra("LATITUDE", lat);
        }else {
            openMap = new Intent(MapsDetailUmkmActivity.this, UmkmDetailActivity.class);
            openMap.putExtra("ID_UMKM",getIntent().getStringExtra("ID_UMKM"));
            openMap.putExtra("SEKTOR_POSITION",getIntent().getExtras().getInt("SEKTOR_POSITION"));
            openMap.putExtra("LAPORAN_FOTO",getIntent().getExtras().getString("LAPORAN_FOTO"));
            openMap.putExtra("IS_IMAGE_GALERY",getIntent().getExtras().getString("IS_IMAGE_GALERY"));
            openMap.putExtra("NAMA_SIDEBARU",getIntent().getExtras().getString("NAMA_SIDEBARU"));
            openMap.putExtra("ALAMAT_SIDEBARU",getIntent().getExtras().getString("ALAMAT_SIDEBARU"));
            openMap.putExtra("EMAIL",getIntent().getExtras().getString("EMAIL"));
            openMap.putExtra("HANDPHONE",getIntent().getExtras().getString("HANDPHONE"));
            openMap.putExtra("TELEPON",getIntent().getExtras().getString("TELEPON"));
            openMap.putExtra("PROVINSI",getIntent().getExtras().getString("PROVINSI"));
            openMap.putExtra("KABUPATEN",getIntent().getExtras().getString("KABUPATEN"));
            openMap.putExtra("KECAMATAN",getIntent().getExtras().getString("KECAMATAN"));
            openMap.putExtra("KELURAHAN",getIntent().getExtras().getString("KELURAHAN"));
            openMap.putExtra("KODEPOS",getIntent().getExtras().getString("KODEPOS"));
            openMap.putExtra("DESKRIPSI",getIntent().getExtras().getString("DESKRIPSI"));
            openMap.putExtra("LONGITUDE", lng);
            openMap.putExtra("LATITUDE", lat);
        }
        startActivity(openMap);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(lat, lng),
                                    DEFAULT_ZOOM,
                                    namaUmkm);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsDetailUmkmActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        Log.e(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                alamat = title;
                if(getIntent().getBooleanExtra("IS_UPDATE",false)){
                    LatLng latvalue = cameraPosition.target;
                    lat = latvalue.latitude;
                    lng = latvalue.longitude;
                    if(options==null)
                    {
                        alamat = getCompleteAddressString(lat,lng);
                        kabupaten = getCityNameString(lat,lng);
                        options = mMap.addMarker(new MarkerOptions().position(cameraPosition.target).title(title));
                        options.showInfoWindow();
                        mSearchText.setText(alamat);
                        Log.e("DARI UPDATE ", "moveCamera: moving the camera to: lat: " + lat + ", lng: " + lng +" alamat : "+ alamat);
                    } else {
                        alamat = getCompleteAddressString(lat,lng);
                        kabupaten = getCityNameString(lat,lng);
                        options.setPosition(cameraPosition.target);
                        mSearchText.setText(alamat);
                        Log.e("DARI UPDATE ", "moveCamera: moving the camera to: lat: " + lat + ", lng: " + lng  +" alamat : "+ alamat);

                    }
                }else{
                    if(options==null)
                    {
                        alamat = getCompleteAddressString(lat,lng);
                        kabupaten = getCityNameString(lat,lng);
                        mSearchText.setText(alamat);
                        options = mMap.addMarker(new MarkerOptions().position(cameraPosition.target).title(title));
                        options.showInfoWindow();
                        Log.e("DARI DETAIL ", "moveCamera: moving the camera to: lat: " + lat + ", lng: " + lng );
                    }

                }


            }
        });


        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsDetailUmkmActivity.this);

//        LatLng sydney = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney"));
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                StringBuilder strReturnedCity = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    strReturnedCity.append(returnedAddress.getLocality()).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                cityName = strReturnedCity.toString();
                Log.e("Address :", strReturnedAddress.toString());
                Log.e("CITY :", strReturnedCity.toString());
            } else {
                strAdd = "Unknown address";
                Log.e("Address : ", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            strAdd = "Unknown address";
            Log.e("HAA ", "Canont get Address!" + e);
        }
        return strAdd;
    }

    protected String getCityNameString(double LATITUDE, double LONGITUDE) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);

            Log.e("CITY ", "Canont get Address!" + cityName);
            Log.e("STATE ", "Canont get Address!" + stateName);
            Log.e("COUNTRY ", "Canont get Address!" + countryName);
        } catch (Exception e) {
            e.printStackTrace();
            cityName = "Unknown City";
            Log.e("HAA ", "Canont get Address!" + e);
        }
        return cityName;
    }

}