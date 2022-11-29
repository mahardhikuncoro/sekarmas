package user.pariwisata;

import android.Manifest;
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
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.data.umkmmodel.UmkmCallback;
import base.screen.AddImageUmkmActivity;
import base.screen.BaseDialogActivity;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ops.screen.MapsActivity;
import ops.screen.adapter.SpinnerCostumeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePariwisata extends BaseDialogActivity {


    @BindView(R.id.btn_image_bar_loc)
    LinearLayout btnLocation;

    @BindView(R.id.img_upload)
    ImageView imgUpload;

    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.spinner_sektor)
    Spinner spinnerKategori;

    @BindView(R.id.etDataNamasidebaru)
    EditText etNamaSidebar;
    @BindView(R.id.etAlamatsidebaru)
    EditText etAlamatSidebar;
    @BindView(R.id.etEmailsidebaru)
    EditText etEmail;
    @BindView(R.id.et_handphone)
    EditText etHandphone;
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
    EditText etdeskripsi;
    @BindView(R.id.et_produk_utama)
    EditText etProdukUtama;

    Uri uri, photoURI;
    private String mCurrentPhotoPath="";
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;
    private Integer selectedIdSektor;
    private int selectedPosition;
    ArrayList<String> sektorNames;
    ArrayList<SektorModel> sektorModels;
    boolean isImageGalery;
    double lat = 0.0;
    double lng = 0.0;
    String alamat = "";
    String kabupaten = "";

    int PERMISSION_ALL = 1;
    Bitmap imageBitmap;

    String[] permissionsRequired =  {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pariwisata);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        getKategori();
        if(getIntent().getExtras() != null){
            setValueFromMap();
        }
    }

    private void setValueFromMap(){
        spinnerKategori.setSelection(getIntent().getIntExtra("LAPORAN_KATEGORI",0));
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
        setImageFromGalery();
    }

    private void getKategori() {
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            pariwisataEndpoint.getKategori("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<SektorJson>() {
                @Override
                public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        sektorNames = new ArrayList<>();
                        sektorModels = new ArrayList<>();
                        if(response.body().getData() != null && response.body().getData().size() > 0) {
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                SektorModel sektorModel = new SektorModel();
                                sektorModel.setId(response.body().getData().get(i).getId());
                                sektorModel.setName(response.body().getData().get(i).getName());
                                sektorModels.add(sektorModel);
                                sektorNames.add(response.body().getData().get(i).getName());

                            }
                        }
                        setadapterKategori();
                    }
                }

                @Override
                public void onFailure(Call<SektorJson> call, Throwable t) {

                }
            });

        }
    }

    private void setadapterKategori(){
        SpinnerCostumeAdapter spinnerCostumeAdapter = new SpinnerCostumeAdapter(CreatePariwisata.this, sektorNames);
        spinnerKategori.setAdapter(spinnerCostumeAdapter);
        spinnerKategori.setSelection(getIntent().getIntExtra("LAPORAN_KATEGORI",0));
        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spinnerKategori.setSelection(getIntent().getIntExtra("LAPORAN_KATEGORI",0));
        Log.e("SELECTED ", "INTENT  : " + getIntent().getIntExtra("LAPORAN_KATEGORI",0));
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
            etNamaSidebar.requestFocus();
        } else if(etAlamatSidebar.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            etAlamatSidebar.requestFocus();
            dialog(R.string.errorAlamatsidebaru);
        }else if(fileToUpload==null){
            progressBar.setVisibility(View.INVISIBLE);
            imgUpload.requestFocus();
            dialog(R.string.errorIMage);
        }else if(lng==0.0){
            progressBar.setVisibility(View.INVISIBLE);
            btnLocation.requestFocus();
            dialog(R.string.errorLokasi);
        }else if(lat==0.0){
            progressBar.setVisibility(View.INVISIBLE);
            btnLocation.requestFocus();
            dialog(R.string.errorLokasi);
        }else if(kabupaten.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            btnLocation.requestFocus();
            dialog(R.string.errorLokasi);
        }
         else {
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), userdata.select().getId());
            RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), etNamaSidebar.getText().toString());
            RequestBody alamat = RequestBody.create(MediaType.parse("text/plain"), etAlamatSidebar.getText().toString());
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString());
            RequestBody sektorId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedIdSektor));
            RequestBody telepon = RequestBody.create(MediaType.parse("text/plain"), etTelepon.getText().toString());
            RequestBody rbHandphone = RequestBody.create(MediaType.parse("text/plain"), etHandphone.getText().toString());
            RequestBody provinsi = RequestBody.create(MediaType.parse("text/plain"), etProvinsi.getText().toString());
            RequestBody kabupaten = RequestBody.create(MediaType.parse("text/plain"), etKabupaten.getText().toString());
            RequestBody kelurahan = RequestBody.create(MediaType.parse("text/plain"), etKelurahan.getText().toString());
            RequestBody kecamatan = RequestBody.create(MediaType.parse("text/plain"), etKecamatan.getText().toString());
            RequestBody kodepos = RequestBody.create(MediaType.parse("text/plain"), etKodePos.getText().toString());
            RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lat));
            RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lng));
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etdeskripsi.getText().toString());
            RequestBody rbProdukUtama = RequestBody.create(MediaType.parse("text/plain"), etProdukUtama.getText().toString());

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("nama", nama);
            map.put("alamat", alamat);
            map.put("kategori_id", sektorId);
            map.put("email", email);
            map.put("telepon", telepon);
            map.put("handphone", rbHandphone);
            map.put("provinsi", provinsi);
            map.put("kabupaten", kabupaten);
            map.put("kecamatan", kecamatan);
            map.put("kelurahan", kelurahan);
            map.put("kodepos", kodepos);
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("description", description);
            map.put("produk_utama", rbProdukUtama);


            pariwisataEndpoint.addPariwisata("Bearer " + userdata.select().getAccesstoken(),map,fileToUpload).enqueue(new Callback<UmkmCallback>() {
                @Override
                public void onResponse(Call<UmkmCallback> call, Response<UmkmCallback> response) {
                    if(response.isSuccessful()){
                        if(response.body().getData() != null) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(CreatePariwisata.this, AddImageUmkmActivity.class);
                            intent.putExtra(ParameterKey.ID_UMKM, response.body().getData());
                            Toast.makeText(CreatePariwisata.this, "Data Berhasil diinput, silahkan input gambar !", Toast.LENGTH_LONG)
                                    .show();
                            startActivity(intent);
                        }else{
                            dialog(R.string.errorApi);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UmkmCallback> call, Throwable t) {
                    dialog(R.string.errorApi);
                }
            });

        }
    }

    @OnClick(R.id.btn_upload_image)
    public void clickOpenGalery(){
        {
            Dialog choose = new AlertDialog.Builder(CreatePariwisata.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Pilih Media")
                    .setNegativeButton("Cancel", null)
                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                openCamera();
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

    public void openCamera() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA}, 0);
        }else {
            takePicture();
        }
    }

    public void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
    }


    @OnClick(R.id.btn_image_bar_loc)
    public void openMap(){
        Dialog choose = new AlertDialog.Builder(CreatePariwisata.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Pilih Lokasi")
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
                            Intent openMap = new Intent(CreatePariwisata.this, MapsActivity.class);
                            openMap.putExtra("LAPORAN_KATEGORI",selectedPosition);
                            openMap.putExtra("LAPORAN_FOTO",mCurrentPhotoPath);
                            openMap.putExtra("IS_IMAGE_GALERY",isImageGalery);
                            openMap.putExtra("NAMA_SIDEBARU",etNamaSidebar.getText().toString());
                            openMap.putExtra("ALAMAT_SIDEBARU",alamat);
                            openMap.putExtra("EMAIL",etEmail.getText().toString());
                            openMap.putExtra("TELEPON",etTelepon.getText().toString());
                            openMap.putExtra("HANDPHONE",etHandphone.getText().toString());
                            openMap.putExtra("PROVINSI",etProvinsi.getText().toString());
                            openMap.putExtra("KABUPATEN",etKabupaten.getText().toString());
                            openMap.putExtra("KECAMATAN",etKecamatan.getText().toString());
                            openMap.putExtra("KELURAHAN",etKelurahan.getText().toString());
                            openMap.putExtra("KODEPOS",etKodePos.getText().toString());
                            openMap.putExtra("LONGITUDE",lng);
                            openMap.putExtra("LATITUDE",lat);
                            openMap.putExtra("IS_UMKM",true);
                            startActivity(openMap);
                        }
                    }
                }).create();
        choose.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                imgUpload.setImageBitmap(imageBitmap);
                File fileCameraRaw = handlePhoto(this, data);
//                RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCameraRaw);
//                fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCameraRaw.getName(), mFile);
            }else if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                photoURI = data.getData();
                String mimeType = "";
                mCurrentPhotoPath = getRealPathFromURIPath(photoURI, CreatePariwisata.this);
                File file = new File(mCurrentPhotoPath);
                try {
                    mimeType = file.toURL().openConnection().getContentType();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] type = mimeType.split("/");
                String formatType = type[0];
                if (formatType.equals("image")) {
                    Uri imagePath = Uri.parse(mCurrentPhotoPath);
                    try {
                        File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
                        ExifInterface exifInterface = new ExifInterface(fileCameraRaw.getAbsolutePath());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(getCameraPhotoOrientation(CreatePariwisata.this, imagePath, file));
                        File fileCompress = new Compressor(CreatePariwisata.this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(CreatePariwisata.this).compressToBitmap(fileCompress);
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
            }
        }
    }

    private File handlePhoto(Context context, Intent intent) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "sekar_pinter_take_photo" + System.currentTimeMillis(), null);

        if (context.getContentResolver() != null) {
            Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }

        return new File(path);
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
//                layoutVideoTitle.setVisibility(View.GONE);
                RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                fileToUpload = MultipartBody.Part.createFormData("profile_picture", fileCompress.getName(), mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (formatType.equals("video")) {
//            layoutVideoTitle.setVisibility(View.VISIBLE);
            imgUpload.setVisibility(View.GONE);
//            tvVideoTitle.setText(fileCameraRaw.getName());
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
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[3] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[4] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[5] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[6] != PackageManager.PERMISSION_GRANTED
                ) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[5]) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[6])
                    ){
                        dialog();
                    } else {
                        dialogPermission();
                    }
                }
            }
        }
    }

    private void dialogPermission() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);
    }

    protected void dialog() {
        new MaterialDialog.Builder(this)
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .content("Please allow all permission on your app setting, thank you")
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .cancelable(false)
                .show();
    }

}
