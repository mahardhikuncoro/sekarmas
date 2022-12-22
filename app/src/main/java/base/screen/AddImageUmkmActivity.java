package base.screen;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import base.location.BaseNetworkCallback;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.sidebaru.DetailSidebaruActivity;

public class AddImageUmkmActivity extends BaseDialogActivity {


    @BindView(R.id.btnRetake)
    Button btnRetake;
    @BindView(R.id.btnSave) Button btnSave;
    @BindView(R.id.imageview) ImageView imageView;
    @BindView(R.id.et_deskripsi)
    EditText etDeskripsi;
    @BindView(R.id.post_progress_bar) ProgressBar progressBar;
    private Bitmap imageBitmap;

    public static final int REQUEST_CAMERA_CODE = 300;
    public static final int REQUEST_GALLERY_CODE = 200;
    private String mCurrentPhotoPath;
    MultipartBody.Part fileToUpload;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image_activity);
        ButterKnife.bind(this);
        initiateApiData();
        progressBar.setVisibility(View.GONE);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(getIntent().getStringExtra(ParameterKey.PICTFROM) != null && getIntent().getStringExtra(ParameterKey.PICTFROM).equals("GALLERY")){
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*, video/*");
            startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            btnRetake.setVisibility(View.GONE);
        }else {
            takePicture();
            getLastLocation();
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

    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                File fileCameraRaw = handlePhoto(this, data);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCameraRaw);
                fileToUpload = MultipartBody.Part.createFormData("file", fileCameraRaw.getName(), mFile);

            }else if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                photoURI = data.getData();
                String mimeType = "";
                mCurrentPhotoPath = getRealPathFromURIPath(photoURI, AddImageUmkmActivity.this);
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
                        matrix.postRotate(getCameraPhotoOrientation(AddImageUmkmActivity.this, imagePath, file));
                        File fileCompress = new Compressor(AddImageUmkmActivity.this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(AddImageUmkmActivity.this).compressToBitmap(fileCompress);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(newBitmap);
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("file", fileCompress.getName(), mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
                    imageView.setVisibility(View.GONE);
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
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

    @OnClick(R.id.btnSave)
    public void clickSave(){
        sendImageUmkm(getIntent().getIntExtra(ParameterKey.ID_UMKM, 0), etDeskripsi.getText().toString(), fileToUpload);
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

    private void sendImageUmkm(Integer idUmkm, String descUmkm, MultipartBody.Part file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
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
                    {
                        dialog.dismiss();
                        try{
                            if (response.isSuccessful()) {
                                if(response.body().getSuccess()) {
                                    dialogSuccessUpload(getResources().getString(R.string.suksesAddImageProduk), true);
                                }else{
                                    try {
                                        JSONObject jObjError = new JSONObject(response.body().toString());
                                        dialogMessage(jObjError.getString("messages"));
                                    } catch (Exception e) {
                                    }
                                }
                            }else{
                                try {
                                    JSONObject jObjError = new JSONObject(response.body().toString());
                                    dialogMessage(jObjError.getString("messages"));
                                } catch (Exception e) {
                                    dialogMessage(getResources().getString(R.string.errorBackend));
                                }
                            }
                        }catch (Exception e){
                            dialogMessage(getResources().getString(R.string.errorBackend));
                        }
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
    }

    protected void dialogSuccessUpload(String rString, boolean finish) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(rString)
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .positiveText(R.string.buttonTutup)
                .positiveColor(getResources().getColor(R.color.black))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        if(finish){
                            if(ParameterKey.activityTemp != null) {
                                ParameterKey.activityTemp.finish();
                            }
                            Intent intent = new Intent(AddImageUmkmActivity.this, DetailSidebaruActivity.class);
                            intent.putExtra(ParameterKey.ID_UMKM, getIntent().getIntExtra(ParameterKey.ID_UMKM,0));
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .cancelable(false)
                .build();

        dialog.show();
    }
}