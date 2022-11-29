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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.data.pariwisatamodel.KontentWisata;
import base.data.pariwisatamodel.PariwisataJson;
import base.location.BaseNetworkCallback;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
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
import ops.screen.adapter.PariwisataItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.laporan.CreateLaporan;

public class AddUlasanActivity extends BaseDialogActivity {

    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ic_star_1)
    ImageView star1;
    @BindView(R.id.ic_star_2)
    ImageView star2;
    @BindView(R.id.ic_star_3)
    ImageView star3;
    @BindView(R.id.ic_star_4)
    ImageView star4;
    @BindView(R.id.ic_star_5)
    ImageView star5;
    @BindView(R.id.ivPropict)
    ImageView ivPropict;
    @BindView(R.id.et_deskripsi)
    EditText etDeskripsi;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_upload)
    ImageView imgUpload;
    Integer idUmkm = 0;
    Integer rating = 0;
    String namaWisata="", url="";
    int PERMISSION_ALL = 1;
    Uri uri, photoURI;
    private String mCurrentPhotoPath;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;
    boolean isImageGalery;
    private Bitmap imageBitmap;
    List<String> permissionsRequired = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ulasan);
        ButterKnife.bind(this);
        initiateApiData();
        initView();
        getLastLocation();
    }

    private void initView(){
        permissionsRequired.add(Manifest.permission.CAMERA);
        permissionsRequired.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        idUmkm= getIntent().getIntExtra(ParameterKey.ID_UMKM, 0);
        url = getIntent().getStringExtra(ParameterKey.PICTFROM);
        namaWisata = getIntent().getStringExtra(ParameterKey.NAMA_UMKM);
        progressBar.setVisibility(View.GONE);
        tvTitle.setText(namaWisata);
        if (url != null && !url.isEmpty()) {
            OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
            Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(picassoClient)).build();
            picasso.setLoggingEnabled(true);
            picasso.load(url)
                    .placeholder(R.drawable.img_default)
                    .error(R.drawable.img_default)
                    .fit()
                    .centerCrop()
                    .into(ivPropict, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

    }

    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick(R.id.ic_star_1)
    public void clickStar1(){
        star1.setImageResource(R.drawable.ic_star_orange);
        star2.setImageResource(R.drawable.ic_star_grey);
        star3.setImageResource(R.drawable.ic_star_grey);
        star4.setImageResource(R.drawable.ic_star_grey);
        star5.setImageResource(R.drawable.ic_star_grey);
        rating = 1;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick(R.id.ic_star_2)
    public void clickStar2(){
        star1.setImageResource(R.drawable.ic_star_orange);
        star2.setImageResource(R.drawable.ic_star_orange);
        star3.setImageResource(R.drawable.ic_star_grey);
        star4.setImageResource(R.drawable.ic_star_grey);
        star5.setImageResource(R.drawable.ic_star_grey);
        rating = 2;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick(R.id.ic_star_3)
    public void clickStar3(){
        star1.setImageResource(R.drawable.ic_star_orange);
        star2.setImageResource(R.drawable.ic_star_orange);
        star3.setImageResource(R.drawable.ic_star_orange);
        star4.setImageResource(R.drawable.ic_star_grey);
        star5.setImageResource(R.drawable.ic_star_grey);
        rating = 3;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick(R.id.ic_star_4)
    public void clickStar4(){
        star1.setImageResource(R.drawable.ic_star_orange);
        star2.setImageResource(R.drawable.ic_star_orange);
        star3.setImageResource(R.drawable.ic_star_orange);
        star4.setImageResource(R.drawable.ic_star_orange);
        star5.setImageResource(R.drawable.ic_star_grey);
        rating = 4;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick(R.id.ic_star_5)
    public void clickStar5(){
        star1.setImageResource(R.drawable.ic_star_orange);
        star2.setImageResource(R.drawable.ic_star_orange);
        star3.setImageResource(R.drawable.ic_star_orange);
        star4.setImageResource(R.drawable.ic_star_orange);
        star5.setImageResource(R.drawable.ic_star_orange);
        rating = 5;
    }


    @OnClick(R.id.btn_simpan)
    public void clickPost(){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        }else if(rating == 0){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorRating);
        }else if(etDeskripsi.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorDeskripsiwisata);
        }
        else {
            KontentWisata request = new KontentWisata();
            request.setPariwisataId(idUmkm);
            request.setRating(rating);
            request.setDeskripsi(etDeskripsi.getText().toString());
            pariwisataEndpoint.addUlasan("Bearer " + userdata.select().getAccesstoken(),request).enqueue(new Callback<BaseNetworkCallback>() {
                @Override
                public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        if(response.body().getSuccess()){
                            dialogMessage(getResources().getString(R.string.suksesAddDeskripsi), true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {

                }
            });
        }
    }

    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
       finish();
    }

    @OnClick(R.id.btn_upload_image_bar)
    public void clickOpenGalery(){
        {
            Dialog choose = new AlertDialog.Builder(AddUlasanActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Choose Upload Method")
                    .setNegativeButton("Cancel", null)
                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                if (!hasPermissions(permissionsRequired)) {
                                    String[] permissionArray = new String[permissionsRequired.size()];
                                    for (int i = 0; i < permissionsRequired.size(); i++) {
                                        permissionArray[i] = permissionsRequired.get(i);
                                    }
                                    ActivityCompat.requestPermissions(AddUlasanActivity.this, permissionArray, 0);
                                }else {
                                    takePicture();
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

    public boolean hasPermissions(List<String> permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
                    rotate = 0;
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

    private File handlePhoto(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        imageBitmap = (Bitmap) extras.get("data");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "sekar_pinter_photo" + System.currentTimeMillis(), null);

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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasPermissions(permissionsRequired)) {
            String[] permissionArray = new String[permissionsRequired.size()];
            for (int i = 0; i < permissionsRequired.size(); i++) {
                permissionArray[i] = permissionsRequired.get(i);
            }
            ActivityCompat.requestPermissions(this, permissionArray, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            isImageGalery = true;
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                String mimeType = "";
                mCurrentPhotoPath = getRealPathFromURIPath(uri, AddUlasanActivity.this);
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
                        Bitmap imageCompress = new Compressor(this).compressToBitmap(file);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);
                        imgUpload.setVisibility(View.VISIBLE);
                        imgUpload.setImageBitmap(newBitmap);
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("post_image", fileCompress.getName(), mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
                    imgUpload.setVisibility(View.GONE);
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("post_image", file.getName(), mFile);
                }
            } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA_CODE) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgUpload.setImageBitmap(photo);
                    File fileCameraRaw = handlePhoto(this, data);
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCameraRaw);
                    fileToUpload = MultipartBody.Part.createFormData("image", fileCameraRaw.getName(), mFile);
                }
            }
        }
    }

}
