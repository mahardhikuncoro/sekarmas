package base.screen;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import base.location.BaseNetworkCallback;
import base.network.callback.NetworkClient;
import base.service.umkm.UmkmEndpoint;
import base.utils.enm.ParameterKey;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.BuildConfig;
import id.sekarpinter.mobile.application.R;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import user.DashboardActivity;
import user.pariwisata.AddUlasanActivity;

public class AddImageUmkmActivity extends BaseDialogActivity {

    private static final String TAG = AddImageUmkmActivity.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    Uri uri, photoURI;
    private String mCurrentPhotoPath;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;
    UmkmEndpoint umkmEndpoint;
    ImageView btnPost;
    Integer idUmkm=0;
    List<String> permissionsRequired = new ArrayList<>();
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image_activity);
        ButterKnife.bind(this);

        permissionsRequired.add(Manifest.permission.CAMERA);
        permissionsRequired.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.post_progress_bar);
        btnPost = (ImageView) findViewById(R.id.btn_post_umkm);
        mProgressBar.setVisibility(View.GONE);
        idUmkm = getIntent().getIntExtra(ParameterKey.ID_UMKM,0);

        //Initialize with empty data
//        GridItem gridItem = new GridItem();
//        gridItem.setImage("https://akcdn.detik.net.id/visual/2021/04/26/presiden-jokowi-menyampaikan-duka-cita-kri-nanggala-2.jpeg?w=650");
//        gridItem.setTitle("Test 1");
        initiateApiData();
        getLastLocation();

        mGridData = new ArrayList<>();
//        mGridData.add(gridItem);
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddImageUmkmActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.setView(R.layout.progress_bar).setCancelable(false);
                    }
                    dialog = builder.create();
                    dialog.show();
                    for (int i=0;i<mGridData.size();i++) {
                        sendImageProduk(mGridData.get(i).getId(), mGridData.get(i).getDesc(), mGridData.get(i).getFileToUpload(), i);
                    }
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.putExtra(ParameterKey.SCREEN_UMKM, true);
                    startActivity(intent);
                    dialog.dismiss();
            }
        });
        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(AddImageUmkmActivity.this, DetailsGridViewActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getDesc()).
                        putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });

        //Start download
//        new AsyncHttpTask().execute(FEED_URL);
//        mProgressBar.setVisibility(View.VISIBLE);
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

    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
    }

    @OnClick(R.id.btn_upload_image)
    public void clickOpenGalery(){
        {
//            Dialog choose = new AlertDialog.Builder(GridViewActivity.this, AlertDialog.THEME_HOLO_LIGHT)
//                    .setTitle("Choose Upload Method")
//                    .setNegativeButton("Cancel", null)
//                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int position) {
//                            if (position == 0) {
            if (!hasPermissions(permissionsRequired)) {
                String[] permissionArray = new String[permissionsRequired.size()];
                for (int i = 0; i < permissionsRequired.size(); i++) {
                    permissionArray[i] = permissionsRequired.get(i);
                }
                ActivityCompat.requestPermissions(AddImageUmkmActivity.this, permissionArray, 0);
            }else {
                takePicture();
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (cameraIntent.resolveActivity(AddImageUmkmActivity.this.getPackageManager()) != null) {
//                    File photoFIle = null;
//                    try {
//                        photoFIle = createImageFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (photoFIle != null) {
//                        if (Build.VERSION.SDK_INT >= 23) {
//                            photoURI = FileProvider.getUriForFile(AddImageUmkmActivity.this,
//                                    BuildConfig.APPLICATION_ID + ".fileProvider",
//                                    photoFIle);
//                        } else {
//                            photoURI = Uri.fromFile(photoFIle);
//                        }
//
//                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                        StrictMode.setVmPolicy(builder.build());
//
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFIle));
//                        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
//                        for (ResolveInfo resolveInfo : resInfoList) {
//                            String packageName = resolveInfo.activityInfo.packageName;
//                            grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        }
//
//                        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
//                    }
//                }
            }

//                            } else if (position == 1) {
//                                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
//                                openGalleryIntent.setType("image/*, video/*");
//                                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
//                            }
//                        }
//                    }).create();
//            choose.show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                String mimeType = "";
                String filePath = getRealPathFromURIPath(uri, AddImageUmkmActivity.this);
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
                        if (Build.VERSION.SDK_INT >= 23) {
                            photoURI = FileProvider.getUriForFile(AddImageUmkmActivity.this,
                                    BuildConfig.APPLICATION_ID + ".fileProvider",
                                    file);
                        } else {
                            photoURI = Uri.fromFile(file);
                        }
                        ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(getCameraPhotoOrientation(this, photoURI, file));
                        File fileCompress = new Compressor(this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(this).compressToBitmap(fileCompress);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("file", fileCompress.getName(), mFile);
                        GridItem gridItem = new GridItem();
                        gridItem.setImage(file.getName());
                        gridItem.setId(idUmkm);
                        gridItem.setDesc(file.getName());
                        gridItem.setBitmapImage(newBitmap);
                        gridItem.setUri(photoURI);
                        gridItem.setFileToUpload(fileToUpload);
                        mGridData.add(gridItem);
                        Log.e("SIZE IMAGE GRID"," : " + mGridData.size());
                        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
//                    layoutVideoTitle.setVisibility(View.VISIBLE);
//                    imgUpload.setVisibility(View.GONE);
//                    tvVideoTitle.setText(file.getName());
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                }
            } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                setImageFromCameraToView(data);
            }
        }
//        if(mGridData.size()>0){
//            tvPost.setText("UPLOAD");
//        }else{
//            tvPost.setText("LEWATI");
//        }

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
    private void setImageFromCameraToView(Intent data){
        File fileCameraRaw = handlePhoto(this, data);
        RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCameraRaw);
        fileToUpload = MultipartBody.Part.createFormData("file", fileCameraRaw.getName(), mFile);

        GridItem gridItem = new GridItem();
        gridItem.setImage(fileCameraRaw.getName());
        gridItem.setDesc(fileCameraRaw.getName());
        gridItem.setId(idUmkm);
        gridItem.setBitmapImage(imageBitmap);
        gridItem.setUri(uri);
        gridItem.setFileToUpload(fileToUpload);
        mGridData.add(gridItem);
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);

       /* Uri imagePath = Uri.parse(mCurrentPhotoPath);
        try {
            File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
            Matrix matrix = new Matrix();
            matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
            File fileCompress = new Compressor(this).compressToFile(fileCameraRaw);
            Bitmap bitmapCompress = new Compressor(this).compressToBitmap(fileCompress);
            Bitmap newBitmap = Bitmap.createBitmap(bitmapCompress, 0, 0, bitmapCompress.getWidth(),
                    bitmapCompress.getHeight(), matrix, true);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCompress);
            fileToUpload = MultipartBody.Part.createFormData("file", fileCompress.getName(), mFile);
            GridItem gridItem = new GridItem();
            gridItem.setImage(fileCameraRaw.getName());
            gridItem.setDesc(fileCameraRaw.getName());
            gridItem.setId(idUmkm);
            gridItem.setBitmapImage(newBitmap);
            gridItem.setUri(uri);
            gridItem.setFileToUpload(fileToUpload);
            mGridData.add(gridItem);
            mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
//            imgUpload.setImageBitmap(newBitmap);

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
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

    private void sendImageProduk(Integer idUmkm, String descUmkm,MultipartBody.Part file,  int i) {

        if (!networkConnection.isNetworkConnected()) {
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUmkm));
            RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), descUmkm);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("id", id);
            map.put("desc", desc);

            umkmEndpoint.uploadImageSidebaru("Bearer " + userdata.select().getAccesstoken(), map, file).enqueue(new Callback<BaseNetworkCallback>() {
                @Override
                public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                    if (response.isSuccessful()) {
                        Log.e("MGRID SIZE ", " I " + mGridData.size()+" + "+ " "+ i);
//                        if(i==(mGridData.size()-1)){

//                            Intent intent = new Intent(getApplicationContext(), MainActivityDashboard.class);
//                            intent.putExtra(ParameterKey.SCREEN_UMKM, true);
//                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }
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
}