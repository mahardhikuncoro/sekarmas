package user.registrasi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import base.location.BaseNetworkCallback;
import base.screen.BaseDialogActivity;
import base.utils.CompressorNew;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.BuildConfig;
import id.sekarmas.mobile.application.R;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.laporan.CreateLaporan;
import user.login.LoginActivity;

/**
 * Created by Mahardhi Kuncoro on 5/22/2022
 */
public class RegistrasiActivity extends BaseDialogActivity {

    @BindView(R.id.et_nama_lengkap)
    EditText etNama;

    @BindView(R.id.et_nomor_tlp)
    EditText etNoTelp;

    @BindView(R.id.et_tgl_lahir)
    EditText etTglLahir;

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password_konfirmasi)
    EditText etPasswordKonfirmasi;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.progress_registrasi)
    ProgressBar pb_daftar;

    @BindView(R.id.spinner_gender)
    Spinner spinnerGender;

    @BindView(R.id.iv_propict)
    ImageView imgUpload;

    public String gender;

    final Calendar myCalendar = Calendar.getInstance();
    Uri photoURI;
    Uri uri;
    private String mCurrentPhotoPath;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAMERA_CODE = 300;
    MultipartBody.Part fileToUpload;

    boolean isImageGalery;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        ButterKnife.bind(this);
        pb_daftar.setVisibility(View.INVISIBLE);
        setSpinnerGender();
        initiateApiData();
        if(getIntent().getExtras() != null){
            setValueFromMap();
        }
    }

    @OnClick(R.id.btn_regist)
    public void clickDaftar(){
        if(validasi()){
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

                RequestBody rbnama = RequestBody.create(MediaType.parse("text/plain"), etNama.getText().toString());
                RequestBody rbtelpon = RequestBody.create(MediaType.parse("text/plain"), etNoTelp.getText().toString());
                RequestBody rbgender = RequestBody.create(MediaType.parse("text/plain"), gender);
                RequestBody rbtangal_lahir = RequestBody.create(MediaType.parse("text/plain"), etTglLahir.getText().toString());
                RequestBody rbemail = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString());
                RequestBody rbusername = RequestBody.create(MediaType.parse("text/plain"),etUsername.getText().toString());
                RequestBody rbpassword = RequestBody.create(MediaType.parse("text/plain"),etPassword.getText().toString());
                RequestBody rbpassword_konfirmasi = RequestBody.create(MediaType.parse("text/plain"), etPasswordKonfirmasi.getText().toString());

                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("fullname", rbnama);
                map.put("username", rbusername);
                map.put("email", rbemail);
                map.put("password", rbpassword);
                map.put("phone", rbtelpon);
                map.put("gender", rbgender);
                map.put("password_confirmation", rbpassword_konfirmasi);
                map.put("date_of_birth", rbtangal_lahir);

                registrasiEndpoint.registrasi(map, fileToUpload).enqueue(new Callback<BaseNetworkCallback>() {
                    @Override
                    public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                        try {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                dialog.dismiss();
                                if (response.body().getSuccess()) {
                                    Toast.makeText(RegistrasiActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    dialogMessage(response.body().getMessage().replace("|", "\n\n"));
                                }

                            } else {
                                dialogMessage(response.body().getMessage().replace("|", "\n\n"));
                            }
                        }catch (Exception e) {
                            dialog.dismiss();
                            dialogMessage("ERROR "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
            }
        }

    }

    @OnClick(R.id.et_tgl_lahir)
    public void clickBirthdate(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();


    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etTglLahir.setText(sdf.format(myCalendar.getTime()));
    }

    private void setSpinnerGender(){
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    gender = "Laki-Laki";
                }else if(position == 1){
                    gender = "Perempuan";
                }else{
                    gender="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private Boolean validasi(){
        if (etNama.getText().toString().isEmpty() || etNama.getText().toString().equals("")) {
            etNama.setError(getString(R.string.errorEmptyname));
            return false;
        }else if (etNoTelp.getText().toString().isEmpty() || etNoTelp.getText().toString().equals("")) {
            etNoTelp.setError(getString(R.string.errorNoDataNoHP));
            return false;
        }else if (etTglLahir.getText().toString().isEmpty() || etTglLahir.getText().toString().equals("")) {
            etTglLahir.setError(getString(R.string.errorEmptyUsername));
            return false;
        }else if (etEmail.getText().toString().isEmpty() || etEmail.getText().toString().equals("")) {
            etEmail.setError(getString(R.string.errorEmail));
            return false;
        }else if (etUsername.getText().toString().isEmpty() || etUsername.getText().toString().equals("")) {
            etUsername.setError(getString(R.string.errorUsername));
            return false;
        }else if (etPassword.getText().toString().isEmpty() || etPassword.getText().toString().equals("")) {
            etPassword.setError(getString(R.string.errorPassword));
            return false;
        }else if (etPasswordKonfirmasi.getText().toString().isEmpty() || etPasswordKonfirmasi.getText().toString().equals("")) {
            etPasswordKonfirmasi.setError(getString(R.string.errorPassword));
            return false;
        }else if (!etPasswordKonfirmasi.getText().toString().equals(etPassword.getText().toString())) {
            etPassword.setError(getString(R.string.errorPasswordNotMatch));
            return false;
        }else if(fileToUpload==null) {
            dialog(R.string.errorImageRegist);
            return false;
        }


            return true;
    }

    @OnClick(R.id.iv_propict)
    public void clickPicture(){
        Dialog choose = new AlertDialog.Builder(RegistrasiActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Choose Upload Method")
                .setNegativeButton("Cancel", null)
                .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if (position == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(RegistrasiActivity.this.getPackageManager()) != null) {
                                File photoFIle = null;
                                try {
                                    photoFIle = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (photoFIle != null) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        photoURI = FileProvider.getUriForFile(RegistrasiActivity.this,
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
                mCurrentPhotoPath = getRealPathFromURIPath(uri, RegistrasiActivity.this);
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
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;

                        Matrix matrix = new Matrix();
                        matrix.postRotate(getCameraPhotoOrientation(this, uri, file));
                        File fileCompress = new Compressor(this).compressToFile(file);
                        Bitmap imageCompress = new Compressor(this).compressToBitmap(file);
                        Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                                imageCompress.getHeight(), matrix, true);

                        imgUpload.setVisibility(View.VISIBLE);
                        imgUpload.setImageBitmap(newBitmap);
                        int height = options.outHeight;
                        int width = options.outWidth;

//                        if ((height * width) >= 1000000) {
//                            resizePhotonew(fileCompress);
//                        }
                        Log.e("SIZE "," 11 : " + (imageCompress.getWidth()* imageCompress.getWidth()));
                        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                        fileToUpload = MultipartBody.Part.createFormData("avatars", fileCompress.getName(), mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (formatType.equals("video")) {
                    imgUpload.setVisibility(View.GONE);
                    RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
                    fileToUpload = MultipartBody.Part.createFormData("avatars", file.getName(), mFile);
                }
            } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                setImageFromCameraToView();
            }
        }
    }

    public static void resizePhotonew(File fileImage) {
//        File imgFile = new File(path);

        if(fileImage.exists()){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 4;

            Bitmap bitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath(), options);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bitmap = BaseCamera.rotate(bitmap, 0);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, bos);
            byte[] bitmapData = bos.toByteArray();

            try {
                FileOutputStream fos = new FileOutputStream(fileImage);
                fos.write(bitmapData);

                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            bitmap.recycle();
            System.gc();

        }
    }

    private void setImageFromCameraToView(){
        Uri imagePath = Uri.parse(mCurrentPhotoPath);
        try {
            File fileCameraRaw = new File(getRealPathFromURIPath(imagePath, this));
            Matrix matrix = new Matrix();
            matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
            File fileCompress = new CompressorNew(this).compressToFile(fileCameraRaw);
            Bitmap bitmapCompress = new CompressorNew(this).compressToBitmap(fileCompress);
            Bitmap newBitmap = Bitmap.createBitmap(bitmapCompress, 0, 0, bitmapCompress.getWidth(),
                    bitmapCompress.getHeight(), matrix, true);
            Log.e("SIZE "," 11 : " + (bitmapCompress.getWidth()* bitmapCompress.getWidth()));

            imgUpload.setImageBitmap(newBitmap);

            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), fileCompress);
            fileToUpload = MultipartBody.Part.createFormData("avatars", fileCompress.getName(), mFile);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setValueFromMap(){
        etNama.setText(getIntent().getStringExtra("NAMA_LENGKAP"));
        etTglLahir.setText(getIntent().getStringExtra("TGL_LAHIR"));
        spinnerGender.setSelection(getIntent().getIntExtra("GENDER",0));
        etNoTelp.setText(getIntent().getStringExtra("NO_TELP"));
        mCurrentPhotoPath = getIntent().getStringExtra("REGISTRASI_FOTO");
        etUsername.setText(getIntent().getStringExtra("USERNAME"));
        etPassword.setText(getIntent().getStringExtra("PASSWORD"));
        etPasswordKonfirmasi.setText(getIntent().getStringExtra("PASSWORD_KONF"));
        etEmail.setText(getIntent().getStringExtra("EMAIL"));

        setImageFromGalery();
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


                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                matrix.postRotate(getCameraPhotoOrientation(this, imagePath, fileCameraRaw));
                File fileCompress = new Compressor(this).compressToFile(fileCameraRaw);
                Bitmap imageCompress = new Compressor(this).compressToBitmap(fileCameraRaw);
                Bitmap newBitmap = Bitmap.createBitmap(imageCompress, 0, 0, imageCompress.getWidth(),
                        imageCompress.getHeight(), matrix, true);
                imgUpload.setVisibility(View.VISIBLE);
                imgUpload.setImageBitmap(newBitmap);

//                int height = options.outHeight;
//                int width = options.outWidth;
                Log.e("SIZE ","22 : " + (imageCompress.getWidth()* imageCompress.getHeight()));
//                if ((height * width) >= 1000000) {
//                    resizePhotonew(fileCompress);
//                }

                RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCompress);
                fileToUpload = MultipartBody.Part.createFormData("avatars", fileCompress.getName(), mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (formatType.equals("video")) {
            imgUpload.setVisibility(View.GONE);
            RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), fileCameraRaw);
            fileToUpload = MultipartBody.Part.createFormData("avatars", fileCameraRaw.getName(), mFile);
        }
    }

}
