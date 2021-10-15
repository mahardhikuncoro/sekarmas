package ops.screen;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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

import base.location.BaseNetworkCallback;
import base.network.callback.NetworkClient;
import base.service.category.CategoryEndpoint;
import base.service.category.model.CategoryJson;
import base.service.laporan.LaporanEndpoint;
import base.sqlite.model.Userdata;
import base.utils.enm.ParameterKey;
import id.zelory.compressor.Compressor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import base.screen.BaseDialogActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.BuildConfig;
import id.sekarmas.mobile.application.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ops.screen.adapter.SpinnerCostumeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateLaporan extends BaseDialogActivity {

    @BindView(R.id.btn_upload_image_bar)
    LinearLayout btnUploadImage;

    @BindView(R.id.img_upload)
    ImageView imgUpload;

    @BindView(R.id.layout_video_title)
    LinearLayout layoutVideoTitle;

    @BindView(R.id.tv_video_title)
    TextView tvVideoTitle;

    @BindView(R.id.tv_name_post_write)
    TextView tvName;

    @BindView(R.id.et_post_title)
    EditText tvTitle;

    @BindView(R.id.et_post_write)
    EditText tvPost;

    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tv_shared_status)
    Spinner spinnerCategory;

    Uri uri, photoURI;
    private String mCurrentPhotoPath;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;
    CategoryEndpoint categoryEndpoint;
    LaporanEndpoint laporanEndpoint;
    private Userdata userdata;
    private String selectedIdCategory;
    ArrayList<String> categoryNames;
    ArrayList<CategoryJson> categories;

    int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_laporan);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        Log.e("CITY NAME "," "+ getCityName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();
        userdata = new Userdata(CreateLaporan.this);
        tvName.setText(userdata.select().getFullname());

        categoryEndpoint = retrofit.create(CategoryEndpoint.class);
        laporanEndpoint = retrofit.create(LaporanEndpoint.class);
        layoutVideoTitle.setVisibility(View.GONE);
        getCategory();
    }

    private void getCategory() {
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            categoryEndpoint.getListCategory("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<List<CategoryJson>>() {
                @Override
                public void onResponse(Call<List<CategoryJson>> call, Response<List<CategoryJson>> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    categoryNames = new ArrayList<>();
                    categories = new ArrayList<>();
                    for (int i = 0; i < response.body().size(); i++) {
                        CategoryJson categoryModel = new CategoryJson();
                        categoryModel.setId(response.body().get(i).getId());
                        categoryModel.setName(response.body().get(i).getName());
                        categories.add(categoryModel);
                        categoryNames.add(response.body().get(i).getName());
                    }
                    setadapterCategory();
                }

                @Override
                public void onFailure(Call<List<CategoryJson>> call, Throwable t) {

                }
            });

        }
    }

    private void setadapterCategory(){
        SpinnerCostumeAdapter spinnerCostumeAdapter = new SpinnerCostumeAdapter(CreateLaporan.this, categoryNames);
        spinnerCategory.setAdapter(spinnerCostumeAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIdCategory = categories.get(position).getId();
                Log.e("SELECTED ", "ID : " + selectedIdCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btn_post)
    public void clickPost(){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else if(tvTitle.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorJudul);
        } else if(tvPost.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorDeskripsi);
        }
        else if(fileToUpload==null){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorIMage);
        } else {
//            MultipartBody.Part body = MultipartBody.Part.createFormData("post_image",file , fileToUpload);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), userdata.select().getId());
            RequestBody title = RequestBody.create(MediaType.parse("text/plain"), tvTitle.getText().toString());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), tvPost.getText().toString());
            RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"), selectedIdCategory);
            RequestBody kabupaten_kota = RequestBody.create(MediaType.parse("text/plain"), getCityName());
            RequestBody is_public = RequestBody.create(MediaType.parse("text/plain"), "1");
            RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getLatitude()));
            RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getLongitude()));

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("user_id", userId);
            map.put("title", title);
            map.put("description", description);
            map.put("category_id", categoryId);
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("kabupaten_kota", kabupaten_kota);
            map.put("is_public", is_public);

            laporanEndpoint.uploadPost("Bearer " + userdata.select().getAccesstoken(),map,fileToUpload).enqueue(new Callback<BaseNetworkCallback>() {
                @Override
                public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                    if(response.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("Sukses Post","YEYEYEYEY ");
                        Intent intent = new Intent(getApplicationContext(), MainActivityDashboard.class);
                        intent.putExtra(ParameterKey.SCREEN_TASK, true);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {

                }
            });

        }
    }

    @OnClick(R.id.btn_upload_image_bar)
    public void clickOpenGalery(){
        {
            Dialog choose = new AlertDialog.Builder(CreateLaporan.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Choose Upload Method")
                    .setNegativeButton("Cancel", null)
                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(CreateLaporan.this.getPackageManager()) != null) {
                                    File photoFIle = null;
                                    try {
                                        photoFIle = createImageFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (photoFIle != null) {
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            photoURI = FileProvider.getUriForFile(CreateLaporan.this,
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
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                String mimeType = "";
                String filePath = getRealPathFromURIPath(uri, CreateLaporan.this);
                File file = new File(filePath);
                try {
                    mimeType = file.toURL().openConnection().getContentType();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] type = mimeType.split("/");
                String formatType = type[0];
                if (formatType.equals("image")) {
                    try {
                        ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(getCameraPhotoOrientation(this, uri, file));
                        File fileCompress = new Compressor(this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(this).compressToBitmap(file);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);
                        imgUpload.setVisibility(View.VISIBLE);
                        imgUpload.setImageBitmap(newBitmap);
                        layoutVideoTitle.setVisibility(View.GONE);
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("post_image", fileCompress.getName(), mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
                    layoutVideoTitle.setVisibility(View.VISIBLE);
                    imgUpload.setVisibility(View.GONE);
                    tvVideoTitle.setText(file.getName());
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("post_image", file.getName(), mFile);
                }
            } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                Uri imagePath = Uri.parse(mCurrentPhotoPath);
                try {
                    File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
                    ExifInterface exifInterface = new ExifInterface(fileCameraRaw.getAbsolutePath());
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
                    File fileCompress = new Compressor(this).compressToFile(fileCameraRaw);
                    Bitmap bitmapCompress = new Compressor(this).compressToBitmap(fileCompress);
                    Bitmap newBitmap = Bitmap.createBitmap(bitmapCompress, 0, 0, bitmapCompress.getWidth(),
                            bitmapCompress.getHeight(), matrix, true);
                    imgUpload.setImageBitmap(newBitmap);
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCompress);
                    fileToUpload = MultipartBody.Part.createFormData("post_image", fileCompress.getName(), mFile);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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

    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
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
