package user.sidebaru;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.data.umkmmodel.UmkmCallback;
import base.data.umkmmodel.UmkmModel;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.service.umkm.UmkmEndpoint;
import base.sqlite.model.Userdata;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.BuildConfig;
import id.sekarpinter.mobile.application.R;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import ops.screen.MainActivityDashboard;
import ops.screen.MapsDetailUmkmActivity;
import ops.screen.adapter.SpinnerCostumeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import user.DashboardActivity;

public class UpdateSidebaru extends BaseDialogActivity {


    @BindView(R.id.btn_image_bar_loc)
    LinearLayout btnLocation;

    @BindView(R.id.tv_title_sidebaru)
    TextView tvTitleForm;

    @BindView(R.id.img_upload)
    ImageView imgUpload;

    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.spinner_sektor)
    Spinner spinnerSektor;

    @BindView(R.id.etDataNamasidebaru)
    EditText etNamaSidebar;
    @BindView(R.id.etAlamatsidebaru)
    EditText etAlamatSidebar;
    @BindView(R.id.et_handphone)
    EditText etHandphone;
    @BindView(R.id.etEmailsidebaru)
    EditText etEmail;
    @BindView(R.id.etTelepon)
    EditText etTelepon;
    @BindView(R.id.etProvinsi)
    EditText etProvinsi;
    @BindView(R.id.etkabupaten)
    EditText etKabupaten;
    @BindView(R.id.etkecamatan)
    EditText etKecamatan;
    @BindView(R.id.etkelurahan)
    EditText etKelurahan;
    @BindView(R.id.etkodepos)
    EditText etKodePos;
    @BindView(R.id.etdeskripsi)
    EditText etDeskripsi;
    @BindView(R.id.ln_profile_gambar)
    LinearLayout lnProfile;

    Uri uri, photoURI;
    private String mCurrentPhotoPath="";
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;
    UmkmEndpoint umkmEndpoint;
    private Userdata userdata;
    private Integer selectedIdSektor;
    private int selectedPosition;
    ArrayList<String> sektorNames;
    ArrayList<SektorModel> sektorModels;
    boolean isImageGalery;
    double lat = 0.0;
    double lng = 0.0;
    String alamat = "";
    String kabupaten = "";
    Integer idSektor=0;
    Integer sektorPosition=0;
    Integer idUmkm=0;

    int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_sidebaru);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();
        userdata = new Userdata(UpdateSidebaru.this);
        idUmkm= getIntent().getIntExtra(ParameterKey.ID_UMKM,0);

        tvTitleForm.setText("Update Umkm");
        lnProfile.setVisibility(View.GONE);
        umkmEndpoint = retrofit.create(UmkmEndpoint.class);


        if(getIntent().getBooleanExtra("IS_UPDATE",false)){
            setValueFromMap();
        }else{
            retreiveDetailUmkm(String.valueOf(idUmkm));
        }
    }

    public void retreiveDetailUmkm(String idUmkm) {
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {

            umkmEndpoint.getDetailUmkm("Bearer " + userdata.select().getAccesstoken(), idUmkm).enqueue(new Callback<UmkmModel>() {
                @Override
                public void onResponse(Call<UmkmModel> call, Response<UmkmModel> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        etNamaSidebar.setText(response.body().getNama()==null?"":response.body().getNama());
                        etAlamatSidebar.setText(response.body().getAlamat()==null?"":response.body().getAlamat());
                        etHandphone.setText(String.valueOf(response.body().getHandphone()==null?"":response.body().getHandphone()));
                        etTelepon.setText(String.valueOf(response.body().getTelepon()==null?"":response.body().getTelepon()));
                        etEmail.setText(String.valueOf(response.body().getEmail()==null?"":response.body().getEmail()));
                        etProvinsi.setText(String.valueOf(response.body().getProvinsi()==null?"":response.body().getProvinsi()));
                        etKabupaten.setText(String.valueOf(response.body().getKabupaten()==null?"":response.body().getKabupaten()));
                        etKecamatan.setText(String.valueOf(response.body().getKecamatan()==null?"":response.body().getKecamatan()));
                        etKelurahan.setText(String.valueOf(response.body().getKelurahan()==null?"":response.body().getKelurahan()));
                        etKodePos.setText(String.valueOf(response.body().getKodepos()==null?"":response.body().getKodepos()));
                        etDeskripsi.setText(String.valueOf(response.body().getDescription()==null?"":response.body().getDescription()));
                        idSektor =response.body().getSektorId()==null ? 0 : response.body().getSektorId();
                        lng = Double.valueOf(response.body().getLongitude());
                        lat = Double.valueOf(response.body().getLatitude());
                        kabupaten = response.body().getKabupaten();

                        if (response.body().getProfilePicture() != null || !response.body().getProfilePicture().equals("")) {
                            OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                            Picasso picasso = new Picasso.Builder(UpdateSidebaru.this).downloader(new OkHttp3Downloader(picassoClient)).build();
                            picasso.setLoggingEnabled(true);
                            picasso.load(String.valueOf(response.body().getProfilePicture()))
                                    .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                                    .error(R.drawable.ic_profile).resize(120, 120).rotate(0)
                                    .into(imgUpload, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("SUKSES", " ");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("ERROR", " ");

                                        }
                                    });
                        }

                        Log.e("USER ID", " : " + userdata.select().getId());
                        /*if (userdata.select().getId().equals(response.body().getCreatedBy())) {
                            fbDelete.setVisibility(View.VISIBLE);
                            fbEdit.setVisibility(View.VISIBLE);
                        }

                        mGridData = new ArrayList<>();
                        if (response.body().getProduk() != null) {
                            for (int i = 0; i < response.body().getProduk().size(); i++) {
                                GridItem gridItem = new GridItem();
                                gridItem.setImage(response.body().getProduk().get(i).getUrl());
                                gridItem.setId(response.body().getProduk().get(i).getId());
                                gridItem.setDesc(response.body().getProduk().get(i).getName());
                                gridItem.setBitmapImage(null);
                                gridItem.setUri(null);
                                gridItem.setFileToUpload(null);
                                mGridData.add(gridItem);
                            }

                            mGridAdapter = new GridViewDetailAdapter(UmkmDetailActivity.this, R.layout.grid_item_layout, mGridData);
                            mGridView.setAdapter(mGridAdapter);
                        } else {
                            tvTitleGridview.setVisibility(View.GONE);
                        }*/

                        getSektor();
                    }
                }

                @Override
                public void onFailure(Call<UmkmModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setValueFromMap(){
        idUmkm = getIntent().getIntExtra("ID_UMKM",0);
        sektorPosition = getIntent().getIntExtra("SEKTOR_POSITION",0);
        mCurrentPhotoPath = getIntent().getStringExtra("LAPORAN_FOTO");
        lng = getIntent().getDoubleExtra("LONGITUDE",0.0);
        lat = getIntent().getDoubleExtra("LATITUDE",0.0);
        kabupaten = getIntent().getStringExtra("KABUPATEN");
        etNamaSidebar.setText(getIntent().getStringExtra("NAMA_SIDEBARU"));
        etAlamatSidebar.setText(getIntent().getStringExtra("ALAMAT_SIDEBARU"));
        etEmail.setText(getIntent().getStringExtra("EMAIL"));
        etTelepon.setText(getIntent().getStringExtra("TELEPON"));
        etHandphone.setText(getIntent().getStringExtra("HANDPHONE"));
        etProvinsi.setText(getIntent().getStringExtra("PROVINSI"));
        etKabupaten.setText(getIntent().getStringExtra("KABUPATEN"));
        etKecamatan.setText(getIntent().getStringExtra("KECAMATAN"));
        etKelurahan.setText(getIntent().getStringExtra("KELURAHAN"));
        etKodePos.setText(getIntent().getStringExtra("KODEPOS"));
        etDeskripsi.setText(getIntent().getStringExtra("DESKRIPSI"));

        getSektor();
    }

    private void getSektor() {
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            umkmEndpoint.getSektorUmkm("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<SektorJson>() {
                @Override
                public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        sektorNames = new ArrayList<>();
                        sektorModels = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            SektorModel sektorModel = new SektorModel();
                            sektorModel.setId(response.body().getData().get(i).getId());
                            sektorModel.setName(response.body().getData().get(i).getName());
                            sektorModels.add(sektorModel);
                            sektorNames.add(response.body().getData().get(i).getName());
                            if(idSektor==response.body().getData().get(i).getId()){
                                sektorPosition=i;
                            }
                        }
                        setadapterSektor();
                    }
                }

                @Override
                public void onFailure(Call<SektorJson> call, Throwable t) {

                }
            });

        }
    }

    private void setadapterSektor(){
        SpinnerCostumeAdapter spinnerCostumeAdapter = new SpinnerCostumeAdapter(UpdateSidebaru.this, sektorNames);
        spinnerSektor.setAdapter(spinnerCostumeAdapter);
        spinnerSektor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIdSektor = sektorModels.get(position).getId();
                selectedPosition = position;
                Log.e("SELECTED ", "ID : " + selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSektor.setSelection(sektorPosition);

    }

    @OnClick(R.id.btn_post)
    public void clickPost(){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else if(etNamaSidebar.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNamasidebaru);
        } else if(etAlamatSidebar.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorAlamatsidebaru);
        }/*else if(fileToUpload==null){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorIMage);
        }*/else if(lng==0.0){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorLokasi);
        }else if(lat==0.0){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorLokasi);
        }else if(kabupaten.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorLokasi);
        }
         else {
//            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), userdata.select().getId());
            RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), etNamaSidebar.getText().toString());
            RequestBody alamat = RequestBody.create(MediaType.parse("text/plain"), etAlamatSidebar.getText().toString());
            RequestBody handphone = RequestBody.create(MediaType.parse("text/plain"), etHandphone.getText().toString());
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString());
            RequestBody sektorId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedIdSektor));
            RequestBody telepon = RequestBody.create(MediaType.parse("text/plain"), etTelepon.getText().toString());
            RequestBody provinsi = RequestBody.create(MediaType.parse("text/plain"), etProvinsi.getText().toString());
            RequestBody kabupaten = RequestBody.create(MediaType.parse("text/plain"), etKabupaten.getText().toString());
            RequestBody kelurahan = RequestBody.create(MediaType.parse("text/plain"), etKelurahan.getText().toString());
            RequestBody kecamatan = RequestBody.create(MediaType.parse("text/plain"), etKecamatan.getText().toString());
            RequestBody kodepos = RequestBody.create(MediaType.parse("text/plain"), etKodePos.getText().toString());
            RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lat));
            RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lng));
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etDeskripsi.getText().toString());

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("nama", nama);
            map.put("alamat", alamat);
            map.put("sektor_id", sektorId);
            map.put("email", email);
            map.put("handphone", handphone);
            map.put("telepon", telepon);
            map.put("provinsi", provinsi);
            map.put("kabupaten", kabupaten);
            map.put("kecamatan", kecamatan);
            map.put("kelurahan", kelurahan);
            map.put("kodepos", kodepos);
            map.put("description", description);
            map.put("latitude", latitude);
            map.put("longitude", longitude);

            umkmEndpoint.updateSidebaru("Bearer " + userdata.select().getAccesstoken(),map,String.valueOf(idUmkm)).enqueue(new Callback<UmkmCallback>() {
                @Override
                public void onResponse(Call<UmkmCallback> call, Response<UmkmCallback> response) {
                    if(response.isSuccessful()){
                        if(response.body().getSuccess()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(UpdateSidebaru.this, "Data berhasil diperbaharui !", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.putExtra(ParameterKey.SCREEN_UMKM, true);
                            startActivity(intent);
                        }else{
                            Log.e(" ELSE "," "+ response.body());
                            dialog(R.string.errorApi);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UmkmCallback> call, Throwable t) {
                    Log.e(" FAILURE "," "+ t.toString());
                    dialog(R.string.errorApi );
                }
            });

        }
    }

    @OnClick(R.id.btn_upload_image)
    public void clickOpenGalery(){
        {
            Dialog choose = new AlertDialog.Builder(UpdateSidebaru.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Choose Upload Method")
                    .setNegativeButton("Cancel", null)
                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(UpdateSidebaru.this.getPackageManager()) != null) {
                                    File photoFIle = null;
                                    try {
                                        photoFIle = createImageFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (photoFIle != null) {
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            photoURI = FileProvider.getUriForFile(UpdateSidebaru.this,
                                                    BuildConfig.APPLICATION_ID + ".fileProvider",
                                                    photoFIle);
                                        } else {
                                            photoURI = Uri.fromFile(photoFIle);
                                        }

                                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                        StrictMode.setVmPolicy(builder.build());

                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFIle));
                                        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
                                    }
                                }
                            } else if (position == 1) {
                                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                                openGalleryIntent.setType("image/*, video/*");
                                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
                            }
                        }
                    }).create();
            choose.show();
        }
    }

    @OnClick(R.id.btn_image_bar_loc)
    public void openMap(){
        Dialog choose = new AlertDialog.Builder(UpdateSidebaru.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Choose Upload Method")
                .setNegativeButton("Cancel", null)
                .setItems(new String[]{"Lokasi Sekarang", "Pilih dari peta"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            lng = getLongitude();
                            lat = getLatitude();
                            kabupaten = getCityName();
                            alamat = getCompleteAddressString(lat,lng);
                            etAlamatSidebar.setText(alamat);
                        } else if (which == 1) {
                            Intent openMap = new Intent(UpdateSidebaru.this, MapsDetailUmkmActivity.class);
                            openMap.putExtra("SEKTOR_POSITION",selectedPosition);
                            openMap.putExtra("ID_UMKM",idUmkm);
                            openMap.putExtra("LAPORAN_FOTO",mCurrentPhotoPath);
                            openMap.putExtra("IS_IMAGE_GALERY",isImageGalery);
                            openMap.putExtra("NAMA_SIDEBARU",etNamaSidebar.getText().toString());
                            openMap.putExtra("ALAMAT_SIDEBARU",etAlamatSidebar.getText().toString());
                            openMap.putExtra("EMAIL",etEmail.getText().toString());
                            openMap.putExtra("TELEPON",etTelepon.getText().toString());
                            openMap.putExtra("HANDPHONE",etHandphone.getText().toString());
                            openMap.putExtra("PROVINSI",etProvinsi.getText().toString());
                            openMap.putExtra("KABUPATEN",etKabupaten.getText().toString());
                            openMap.putExtra("KECAMATAN",etKecamatan.getText().toString());
                            openMap.putExtra("KELURAHAN",etKelurahan.getText().toString());
                            openMap.putExtra("KODEPOS",etKodePos.getText().toString());
                            openMap.putExtra("DESKRIPSI",etDeskripsi.getText().toString());
                            openMap.putExtra("LONGITUDE",lng);
                            openMap.putExtra("LATITUDE",lat);
                            openMap.putExtra("IS_UPDATE",true);
                            startActivity(openMap);
                        }
                    }
                }).create();
        choose.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            isImageGalery = true;
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                String mimeType = "";
                mCurrentPhotoPath = getRealPathFromURIPath(uri, UpdateSidebaru.this);
                File file = new File(mCurrentPhotoPath);
                try {
                    mimeType = file.toURL().openConnection().getContentType();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] type = mimeType.split("/");
                String formatType = type[0];
                if (formatType.equals("image")) {
                    try {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(getCameraPhotoOrientation(this, uri, file));
                        File fileCompress = new Compressor(this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(this).compressToBitmap(fileCompress);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);
                        imgUpload.setVisibility(View.VISIBLE);
                        imgUpload.setImageBitmap(newBitmap);
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCompress.getName(), mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
                    imgUpload.setVisibility(View.GONE);
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("profile_picture", file.getName(), mFile);
                }
            } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                setImageFromCameraToView();
            }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        @SuppressLint("Recycle")
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    private void setImageFromCameraToView(){
        Uri imagePath = Uri.parse(mCurrentPhotoPath);
        try {
            File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
            Matrix matrix = new Matrix();
            matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
            File fileCompress = new Compressor(this).compressToFile(fileCameraRaw);
            Bitmap bitmapCompress = new Compressor(this).compressToBitmap(fileCompress);
            Bitmap newBitmap = Bitmap.createBitmap(bitmapCompress, 0, 0, bitmapCompress.getWidth(),
                    bitmapCompress.getHeight(), matrix, true);
            imgUpload.setImageBitmap(newBitmap);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCompress);
            fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCompress.getName(), mFile);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageFromGalery(){

        String mimeType = "";
        Uri imagePath = Uri.parse(mCurrentPhotoPath);
        File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
        try {
            fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
            mimeType = fileCameraRaw.toURL().openConnection().getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] type = mimeType.split("/");
        String formatType = type[0];
        if (formatType.equals("image")) {
            try {
                fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
                Matrix matrix = new Matrix();
                matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
                File fileCompress = new Compressor(this).compressToFile(fileCameraRaw);
                Bitmap imageCompress = new Compressor(this).compressToBitmap(fileCameraRaw);
                Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                        imageCompress.getHeight(), matrix, true);
                imgUpload.setVisibility(View.VISIBLE);
                imgUpload.setImageBitmap(newBitmap);
                RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCompress.getName(), mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (formatType.equals("video")) {
            imgUpload.setVisibility(View.GONE);
            RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCameraRaw);
            fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCameraRaw.getName(), mFile);
        }
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, File imageFile) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
       finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
