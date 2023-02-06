package user.login;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.sqlite.model.SliderSQL;
import base.utils.Security;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import user.DashboardActivity;
import user.registrasi.RegistrasiActivity;

public class LoginActivity extends LoginAcitivityApiData {

    @BindView(R.id.input_layout_user) TextInputLayout layoutInputUser;
    @BindView(R.id.input_layout_password) TextInputLayout layoutInputPassword;
    @BindView(R.id.inputUser) EditText inputUser;
    @BindView(R.id.inputPassword) EditText inputPassword;
    @BindView(R.id.txtLupasPassword) TextView txtLupaPass;
//    @BindView(R.id.txtRegister) TextView txtReg;
    @BindView(R.id.buttonLogin) Button btnLogin;
    @BindView(R.id.bc_login) RelativeLayout bc_login;
    @BindView(R.id.compnameBelow) TextView compnameBelow;
    private Boolean doubleBackToExitPressedOnce = false;

    @BindString(R.string.buildName) String buildName;
    private static final String FRESH_INSTALL = "FRESH_INSTALL";
    private SharedPreferences sharedPref;
    private Boolean isFreshInstall = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_signin);
        ButterKnife.bind(this);
        transparentStatusbar();
        initiateApiData();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        isFreshInstall =  sharedPref.getBoolean(FRESH_INSTALL, true);
        if(isFreshInstall){
            popUpTnC();
        }

        if(userdata.count()>0){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        } else{
            try {
                prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sliderdql = new SliderSQL(this);
//        getLastLocation();
    }

    private void prepare() throws IOException {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
            layoutInputPassword.setVisibility(View.VISIBLE);
            inputPassword.setVisibility(View.VISIBLE);
            txtLupaPass.setVisibility(View.VISIBLE);
            btnLogin.setText("Login");
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Intent intentBack = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentBack);
        } else {
            if (doubleBackToExitPressedOnce) {
                Intent intentok = new Intent(Intent.ACTION_MAIN);
                intentok.addCategory(Intent.CATEGORY_HOME);
                intentok.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentok);
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.doubleBackPressConfirmation, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @OnClick(R.id.buttonLogin)
    public void login(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (inputUser.getText().toString().equalsIgnoreCase(""))
            layoutInputUser.setError(getResources().getString(R.string.errorEmptyUsername));
        else if (inputPassword.getText().toString().equalsIgnoreCase(""))
            layoutInputPassword.setError(getResources().getString(R.string.errorEmptyPassword));
        else
            callLoginUser(inputUser.getText().toString(), inputPassword.getText().toString(), this);

    }

    @OnClick(R.id.txtLupasPassword)
    public void forgetPassword(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("nohp", "");
        intent.putExtra("screen", "fp");
        startActivity(intent);
        String noUser = inputUser.getText().toString();
    }

    @OnClick(R.id.tv_register)
    public void registerNewUser(){
        Intent intent = new Intent(LoginActivity.this, RegistrasiActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_reset)
    public void resetPassword(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sekarpinter.com/password/reset"));
        startActivity(browserIntent);
    }

    private void popUpTnC(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_privacy_policy, null);
        dialogBuilder.setCancelable(false);
        final CheckBox cbSetujui = (CheckBox) dialogView.findViewById(R.id.cbSetujui);
        final Button btnRegist = (Button) dialogView.findViewById(R.id.btnRegist);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

        cbSetujui.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnRegist.setClickable(true);
                    btnRegist.setBackground(getDrawable(R.drawable.button_orange_selector));
                    btnRegist.setTextColor(getResources().getColor(R.color.white));
                }else{
                    btnRegist.setClickable(false);
                    btnRegist.setBackground(getDrawable(R.drawable.rounded_gray_dark));
                    btnRegist.setTextColor(getResources().getColor(R.color.md_grey_600));
                }
            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(FRESH_INSTALL, false);
                editor.apply();
                dialogBuilder.dismiss();
            }
        });
    }


    private void validate(){
        if (inputUser.getText().toString().length() > 0 && inputPassword.getText().toString().length() > 0){
            try{
                DateFormat dateFormat = new SimpleDateFormat("ddMMHHmm");
                Date date = new Date();
                String dateEncrypt = dateFormat.format(date)
                        .replace("0","A")
                        .replace("1","B")
                        .replace("2","C")
                        .replace("3","D")
                        .replace("4","E")
                        .replace("5","F")
                        .replace("6","G")
                        .replace("7","H")
                        .replace("8","I")
                        .replace("9","J");

                String dateEncrypt2 = dateFormat.format(date)
                        .replace("0","a")
                        .replace("1","b")
                        .replace("2","c")
                        .replace("3","d")
                        .replace("4","e")
                        .replace("5","f")
                        .replace("6","g")
                        .replace("7","h")
                        .replace("8","i")
                        .replace("9","j");

                String ini = Security.encryptStrAndToBase64(dateEncrypt, dateEncrypt2,  inputPassword.getText().toString());
                Log.e("error", "callSignIn");
                //callSignIn(inputUser.getText().toString(), ini, buildName,dateEncrypt+"-"+dateEncrypt2);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (inputUser.getText().toString().isEmpty() || inputUser.getText().toString().equals("")){
                layoutInputUser.setError(getString(R.string.errorEmptyUsername));
            }else if (inputPassword.getText().toString().isEmpty() || inputPassword.getText().toString().equals("")){
                layoutInputPassword.setError(getString(R.string.errorEmptyPassword));
            }else if (inputUser.getText().toString().isEmpty() || inputUser.getText().toString().equals("") &&
                    inputPassword.getText().toString().isEmpty() || inputPassword.getText().toString().equals("")){
                layoutInputUser.setError(getString(R.string.errorEmptyUsername));
                layoutInputPassword.setError(getString(R.string.errorEmptyPassword));
            }
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void loadImage(String photoprofile,String name) {
        String img_url = photoprofile;
//        if (!img_url.equalsIgnoreCase("")) {
//            Picasso.with(getApplicationContext()).load(img_url).placeholder(R.drawable.ic_person_white_24dp)// Place holder image from drawable folder
//                    .error(R.drawable.ic_person_white_24dp)
//                    .resize(200, 200)
//                    .centerCrop()
////                    .rotate(90)
//                    .into(imagePropic);
//            txtviewusername.setText(name.toUpperCase());
//        }else{
//            Picasso.with(getApplicationContext())
//                    .load(R.mipmap.ic_profile_login).placeholder(R.mipmap.ic_profile_login)// Place holder image from drawable folder
//                    .error(R.mipmap.ic_profile_login)
//                    .resize(200, 200)
//                    .centerCrop()
////                    .rotate(90)
//                    .into(imagePropic);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
