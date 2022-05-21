package base.screen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import base.data.DokumenOfflineData;
import base.data.loginmodel.LogoutJson;
import base.network.callback.EndPoint;
import base.network.callback.LoginJson;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.service.kontak.KontakEndpoint;
import base.service.login.EndpointLogin;
import base.service.information.InformationEndpoint;
import base.service.umkm.UmkmEndpoint;
import base.service.visimisi.VisiMisiEndpoint;
import base.sqlite.model.FormData;
import base.sqlite.model.SQLiteConfig;
import base.sqlite.model.Config;
import base.sqlite.model.Userdata;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import user.changepassword.ChangePasswordActivity;
import user.login.LoginActivity;
import id.sekarmas.mobile.application.R;


public class BaseDialogActivity extends AppCompatActivity {

    protected Config config;
    protected NetworkConnection networkConnection;
    protected EndPoint endPoint;
    protected EndpointLogin newEndPoint;
    protected UmkmEndpoint umkmEndpoint;
    protected InformationEndpoint informationEndpoint;
    protected VisiMisiEndpoint visiMisiEndpoint;
    protected KontakEndpoint kontakEndpoint;
    protected TelephonyManager telephonyManager;
    protected String UserImei = "";
    protected Userdata userdata;
    protected FormData formData;
    protected DokumenOfflineData dokumenData;
    protected Bundle bundle;
    protected Dialog dialog;

    public FusedLocationProviderClient fusedLocationClient;
    int PERMISSION_ID = 44;
    protected Double longitude = 0.00;
    protected Double latitude = 0.00;
    protected String address ="Unknown street";
    protected String cityName ="Unknown city";


    protected void initiateApiData(){
        config = new Config(this);
        networkConnection = new NetworkConnection(this);
        userdata = new Userdata(this);
        formData = new FormData(this);
        dokumenData = new DokumenOfflineData(this);
        bundle = new Bundle();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
        httpclient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("parameter", "value").build();
                return chain.proceed(request);
            }
        });

        endPoint = retrofit.create(EndPoint.class);

        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        newEndPoint = retrofit.create(EndpointLogin.class);
        umkmEndpoint = retrofit.create(UmkmEndpoint.class);
        informationEndpoint =retrofit.create(InformationEndpoint.class);
        visiMisiEndpoint = retrofit.create(VisiMisiEndpoint.class);
        kontakEndpoint = retrofit.create(KontakEndpoint.class);

    }

    protected void transparentStatusbar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.greytransparent));
        }
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

    protected void dialogMessage(String rString) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(rString)
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .positiveText(R.string.buttonClose)
                .positiveColor(getResources().getColor(R.color.black))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(false)
                .build();

        dialog.show();
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    protected void dialogPlaystore(int rString) {
        final SQLiteConfig config = new SQLiteConfig(this);
        new MaterialDialog.Builder(this)
                .content(rString)
                .positiveText(R.string.buttonClose)
                .negativeText(R.string.buttonUpdate)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                    public void onNegative(MaterialDialog dialog) {
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(config.getGoogleMarket() + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(config.getGooglePlaystore() + appPackageName)));
                        }
                    }
                })
                .cancelable(true)
                .show();
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

    protected void dialogPermissionCamera() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.CAMERA
        }, 1);
    }
    protected static String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    protected void setToolbarMenu(Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    // action with ID action_refresh was selected
                    case R.id.action_application:
                        final View addView = getLayoutInflater().inflate(R.layout.about_bexi, null);
                        new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.app_name).setView(addView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                }).setNegativeButton("", null).show();

                        break;
                    case R.id.action_change_pass:
                        Intent intentchangepass = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(intentchangepass);
                        finish();
                        break;
                    case R.id.action_logout:
                        dialogLogout(R.string.asklogout);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    protected void setDialog(boolean show){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        Dialog dialog = builder.create();
        if (show){
            dialog.show();
        }
        else if(!show) {
            dialog.dismiss();
        }
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
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
                                    Log.e("LONGITUDE","LONGITUDE :" + getLongitude());
                                    Log.e("LONGITUDE","LATITUDE : " + getLatitude());
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

    protected void  callLogout(){
        networkConnection = new NetworkConnection(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()){
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            newEndPoint.logoutUser("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<LogoutJson>() {
                @Override
                public void onResponse(Call<LogoutJson> call, Response<LogoutJson> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        removeUserData(response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<LogoutJson> call, Throwable t) {

                }
            });
        }
    }

    protected void removeUserData(String message){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        formData.deleteAll("FORM_SURVEY");
        formData.deleteAll("FORM_SURVEY_DATA");
        formData.deleteAll("FORM_SURVEY_REFERENCE");
        formData.deleteAll();
        userdata.deleteAll();
        finish();
        Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show();
    }

    protected void dialogLogout(int rString) {
        new MaterialDialog.Builder(this)
                .content(rString)
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .title(R.string.companyName)
                .positiveText(R.string.buttonKeluar)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        callLogout();

                    }
                })
                .cancelable(true)
                .show();
    }

    protected String getdeviceId(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    private void unregistrasiFirebase(){
        final LoginJson.LoginRequest request = new LoginJson().new LoginRequest();
        request.setLoginType("unregisterDevice");
        request.setUserid(userdata.select().getUsername());
        request.setDeviceId(getdeviceId());
        Call<LoginJson.loginAutenticationCallback> call = endPoint.getAutentication(request);
        call.enqueue(new Callback<LoginJson.loginAutenticationCallback>() {
            @Override
            public void onResponse(Call<LoginJson.loginAutenticationCallback> call, Response<LoginJson.loginAutenticationCallback> response) {
                if(response.isSuccessful()){
                    removeUserData(response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<LoginJson.loginAutenticationCallback> call, Throwable t) {
            }
        });
    }


}