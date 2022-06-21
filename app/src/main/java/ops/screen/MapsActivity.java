package ops.screen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.location.FusedLocationProviderClient;
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

import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import user.laporan.CreateLaporan;
import user.sidebaru.CreateSidebaru;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    Marker options;
    ImageView ivSelectPosition;

    double lat = 0.0;
    double lng = 0.0;
    String alamat = "";
    String kabupaten="";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

//            init();
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

        getLocationPermission();
        ivSelectPosition = (ImageView) findViewById(R.id.iv_select_location);
    }

    @OnClick(R.id.iv_select_location)
    public void getLocation(){
        Log.e("CLICK HERE "," YAA ");
        Intent openMap;
        if(getIntent().getBooleanExtra("IS_UMKM",false)) {
            openMap = new Intent(MapsActivity.this, CreateSidebaru.class);
            openMap.putExtra("LAPORAN_KATEGORI",getIntent().getExtras().getInt("LAPORAN_KATEGORI"));
            openMap.putExtra("LAPORAN_FOTO",getIntent().getExtras().getString("LAPORAN_FOTO"));
            openMap.putExtra("IS_IMAGE_GALERY",getIntent().getExtras().getString("IS_IMAGE_GALERY"));
            openMap.putExtra("NAMA_SIDEBARU",getIntent().getExtras().getString("NAMA_SIDEBARU"));
            openMap.putExtra("ALAMAT_SIDEBARU",alamat);
            openMap.putExtra("EMAIL",getIntent().getExtras().getString("EMAIL"));
            openMap.putExtra("HANDPHONE",getIntent().getExtras().getString("HANDPHONE"));
            openMap.putExtra("TELEPON",getIntent().getExtras().getString("TELEPON"));
            openMap.putExtra("PROVINSI",getIntent().getExtras().getString("PROVINSI"));
            openMap.putExtra("KABUPATEN",getIntent().getExtras().getString("KABUPATEN"));
            openMap.putExtra("KECAMATAN",getIntent().getExtras().getString("KECAMATAN"));
            openMap.putExtra("KELURAHAN",getIntent().getExtras().getString("KELURAHAN"));
            openMap.putExtra("KODEPOS",getIntent().getExtras().getString("KODEPOS"));
            openMap.putExtra("LONGITUDE", lng);
            openMap.putExtra("LATITUDE", lat);
            openMap.putExtra("IS_UMKM",true);
        }else {
            openMap = new Intent(MapsActivity.this, CreateLaporan.class);
            openMap.putExtra("LAPORAN_TITLE", getIntent().getExtras().getString("LAPORAN_TITLE"));
            openMap.putExtra("LAPORAN_DESC", getIntent().getExtras().getString("LAPORAN_DESC"));
            openMap.putExtra("LAPORAN_KATEGORI", getIntent().getIntExtra("LAPORAN_KATEGORI", 0));
            openMap.putExtra("LAPORAN_FOTO", getIntent().getExtras().getString("LAPORAN_FOTO"));
            openMap.putExtra("IS_IMAGE_GALERY", getIntent().getExtras().getBoolean("IS_IMAGE_GALERY"));
            openMap.putExtra("LONGITUDE", lng);
            openMap.putExtra("LATITUDE", lat);
            openMap.putExtra("KABUPATEN", kabupaten);
        }
        startActivity(openMap);
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
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

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
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
                LatLng latvalue = cameraPosition.target;
                lat = latvalue.latitude;
                lng = latvalue.longitude;
                alamat = title;
                if(options==null)
                {
                    alamat = getCompleteAddressString(lat,lng);
                    kabupaten = getCityNameString(lat,lng);
                    options = mMap.addMarker(new MarkerOptions().position(cameraPosition.target).title("Marker"));
                    options.showInfoWindow();
                    mSearchText.setText(alamat);
                    Log.e("HAHAHAHAHAhA ", "moveCamera: moving the camera to: lat: " + lat + ", lng: " + lng );
                } else {
                    alamat = getCompleteAddressString(lat,lng);
                    kabupaten = getCityNameString(lat,lng);
                    options.setPosition(cameraPosition.target);
                    mSearchText.setText(alamat);
                    Log.e("HAHAHAHAHAhA ", "moveCamera: moving the camera to: lat: " + lat + ", lng: " + lng );

                }
            }
        });


        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
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

} /*{

    private GoogleMap mMap;
    public FusedLocationProviderClient fusedLocationClient;
    int PERMISSION_ID = 44;
    protected Double longitude = 0.00;
    protected Double latitude = 0.00;
    protected String address ="Unknown street";
    protected String cityName ="Unknown city";
    private Marker marker;
    private NetworkConnection networkConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        networkConnection = new NetworkConnection(this);
        getLastLocation();
    }

    *//**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @SuppressLint("MissingPermission")
    protected void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    protected LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            setLatitude(latitude);
            setLongitude(longitude);
            setAddress(getCompleteAddressString(latitude,longitude));
            setCityName(getCityNameString(latitude,longitude));
        }
    };



    protected boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }
        return  true;
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    setLatitude(location.getLatitude());
                                    setLongitude(location.getLongitude());
                                    Log.e("LONGITUDE",": " + getLongitude());
                                    Log.e("LATITUDE",": " + getLatitude());
                                    // Add a marker in Sydney and move the camera
                                    LatLng sydney = new LatLng(getLatitude(), getLongitude());

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));
                                    marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Lokasi Saya").snippet(""));
//                                    animateMarker(marker,sydney,false);

                                    if(networkConnection.isNetworkConnected()) {
                                        setAddress(getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                                        setCityName(getCityNameString(location.getLatitude(),location.getLongitude()));
                                    } else
                                        dialog(R.string.errorNoInternetConnection);
                                }
                            }
                        }
                );
            } else {
                dialogGps(R.string.errorGps);
            }
        } else {
            requestPermissions();
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    protected void dialogGps(int rString) {
        new MaterialDialog.Builder(this)
                .content(rString)
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .cancelable(false)
                .show();
    }

    protected void dialog(int rString) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(rString).icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .positiveText(R.string.buttonClose)
                .positiveColor(getResources().getColor(R.color.black))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(true)
                .build();

//        LinearLayout text = (LinearLayout) dialog.getCustomView();
//        text.("Hi!");
        dialog.show();
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



    public Double getLongitude() {
        return longitude;
    }

    protected void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    protected void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    protected void setAddress(String address) {
        this.address = address;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(marker != null){
            marker.remove();

        }

        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(latLng));
        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}*/