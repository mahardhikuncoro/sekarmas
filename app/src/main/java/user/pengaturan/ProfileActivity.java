package user.pengaturan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.network.callback.ResponseStatus;
import base.screen.BaseDialogActivity;
import base.service.URL;
import base.sqlite.model.Userdata;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.CameraActivity;

public class ProfileActivity extends BaseDialogActivity{

    @BindView(R.id.etDataNama)
    EditText etDataNama;

    @BindView(R.id.tv_birth_date)
    EditText tvBirthDate;

    @BindView(R.id.tv_phone)
    EditText tvPhone;

    @BindView(R.id.tv_email)
    EditText tvEmail;

    @BindView(R.id.tv_gender)
    EditText tvGender;

    @BindView(R.id.imgDataNama)
    ImageView imgDataNama;

    @BindView(R.id.textGantiPass)
    TextView textGantiPass;
    Switch switchNotification;

    private String dataNama;
    private String birthDate;
    private String gender,email, phoneNumber;
    private Picasso picasso;


    SharedPreferences sharedpreferences;
    public static final String usernotif = "usernotif";
    private String UserImei="";

    private boolean fromCamera = false;
    static final int SELECT_PICTURE = 102;
    static final int RESELECT_PICTURE = 103;
    private String photoPath;
    private String categoryGroup;
    private String category;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initiateApiData();
        getProfile();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadAsset();
        setProfile();
    }


    private void loadAsset() {
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.downloader(new OkHttp3Downloader(
                NetworkClient.getUnsafeOkHttpClient()
        ));
        picasso = picassoBuilder.build();
        loadIcon();
    }

    private void loadIcon() {
        String img_url = userdata.select().getPhotoprofile();
        OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(picassoClient)).build();
        picasso.setLoggingEnabled(true);
        picasso.load(URL.checkUrl()+img_url)
                .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                .error(R.drawable.ic_profile) .resize(200, 200).rotate(90)
                .into(imgDataNama, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("SUKSES", " ");
                    }

                    @Override
                    public void onError() {
                        Log.e("ERROR", " ");

                    }
                });
       /* if (!img_url.equalsIgnoreCase(""))
            Picasso.with(getActivity()).load(img_url).placeholder(R.drawable.ic_person_white_24dp)// Place holder image from drawable folder
                    .error(R.drawable.ic_person_white_24dp)
                    .resize(200, 200)
//                    .rotate(90)
                    .centerCrop()
                    .into(imgDataNama);*/
//        picasso.load(R.drawable.nama).fit().into(imgDataNama);
    }

    private void setProfile(){

        etDataNama.setText(dataNama);
        tvBirthDate.setText(birthDate);
        tvEmail.setText(email);
        tvGender.setText(gender);
        tvPhone.setText(phoneNumber);

        etDataNama.setEnabled(false);
        tvBirthDate.setEnabled(false);
        tvEmail.setEnabled(false);
        tvGender.setEnabled(false);
        tvPhone.setEnabled(false);
        textGantiPass.setVisibility(View.GONE);

    }
    private void getProfile(){
        userdata = new Userdata(this);
        dataNama =  userdata.select().getFullname();
        birthDate = userdata.select().getBirthDate();
        gender = userdata.select().getGender();
        email = userdata.select().getEmail();
        phoneNumber = userdata.select().getPhoneNumber();
    }


    @OnClick(R.id.textGantiPass)
    public void gantiPassword(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.animation_enter, R.animator.animation_out, R.animator.animation_back_left, R.animator.animation_back_right);
    }


    protected void dialog(int rString) {
        new MaterialDialog.Builder(this)
                .content(rString)
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        String valuePref =  sharedpreferences.getString(usernotif, "");
                        if(valuePref.equalsIgnoreCase(ResponseStatus.REMINDER_FALSE.name()))
                            switchNotification.setChecked(false);
                        else
                            switchNotification.setChecked(true);
                    }
                })
                .cancelable(true)
                .show();
    }


    @OnClick(R.id.imgDataNama)
    public void uploadimage(){
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }else {
            Intent intentprofile = new Intent(this.getApplicationContext(), CameraActivity.class);
            intentprofile.putExtra("REGNO","11");
            intentprofile.putExtra("TC","5.0");
            intentprofile.putExtra("UPLOAD_TYPE","profile");
//            startActivity(intentprofile);
        }
//        intentChooser(null,"HALLO");
    }


    public void intentChooser(final Integer photoId, final String category) {
        final String[] items			= new String[] {"Dari Kamera", "Dari Galeri"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    if (photoId == null) {
                        takePicture(category);
                    } else {
                        takePicture(category);
                    }
                }
                if (item == 1) {
                    if (photoId == null) {
                        openGallery(null, category);
                    } else {
                        openGallery(photoId, category);
                    }
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void takePicture(final String category) {
        fromCamera = true;
        this.category = category;

    }


    private void openGallery(Integer photoId, String category) {
//        this.category = category;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        if (photoId == null) {
            startActivityForResult(Intent.createChooser(intent, "Pilih Aksi"), SELECT_PICTURE);
        } else {
//            this.photoId = photoId;
            startActivityForResult(Intent.createChooser(intent, "Pilih Aksi"), RESELECT_PICTURE);
        }
    }

    @OnClick(R.id.btn_logout)
    public void clickLogout(){
        dialogLogout(R.string.asklogout);
    }

//    protected void dialogLogout(int rString) {
//        new MaterialDialog.Builder(this)
//                .content(rString)
//                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
//                .title(R.string.companyName)
//                .positiveText(R.string.buttonKeluar)
//                .callback(new MaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onPositive(MaterialDialog dialog) {
//                        dialog.dismiss();
//                        callLogout();
//                    }
//                })
//                .cancelable(true)
//                .show();
//    }
//    protected void callLogout(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setView(R.layout.progress_bar).setCancelable(false);
//        }
//        dialog = builder.create();
//        dialog.show();
//        if (!networkConnection.isNetworkConnected()){
//            dialog.dismiss();
//            dialog(R.string.errorNoInternetConnection);
//        } else {
//            newEndPoint.logoutUser("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<LogoutJson>() {
//                @Override
//                public void onResponse(Call<LogoutJson> call, Response<LogoutJson> response) {
//                    if (response.isSuccessful()) {
//                        dialog.dismiss();
//                        removeUserData(response.body().getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<LogoutJson> call, Throwable t) {
//
//                }
//            });
//        }
//    }

}
