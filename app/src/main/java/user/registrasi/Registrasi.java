package user.registrasi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import base.location.BaseNetworkCallback;
import base.screen.BaseDialogActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.login.LoginActivity;

/**
 * Created by Mahardhi Kuncoro on 5/22/2022
 */
public class Registrasi extends BaseDialogActivity {

    @BindView(R.id.et_nama_lengkap)
    TextInputEditText etNama;

    @BindView(R.id.et_nomor_tlp)
    TextInputEditText etNoTelp;

    @BindView(R.id.et_tgl_lahir)
    TextInputEditText etTglLahir;

    @BindView(R.id.et_email)
    TextInputEditText etEmail;

    @BindView(R.id.et_username)
    TextInputEditText etUsername;

    @BindView(R.id.et_password_konfirmasi)
    TextInputEditText etPasswordKonfirmasi;

    @BindView(R.id.et_password)
    TextInputEditText etPassword;

    @BindView(R.id.progress_registrasi)
    ProgressBar pb_daftar;

    @BindView(R.id.spinner_gender)
    Spinner spinnerGender;

    private String gender;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        ButterKnife.bind(this);
        pb_daftar.setVisibility(View.INVISIBLE);
        setSpinnerGender();
        initiateApiData();
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
                registrasiEndpoint.registrasi(etNama.getText().toString(),etUsername.getText().toString(),
                        etEmail.getText().toString(),etPassword.getText().toString(),
                        etNoTelp.getText().toString(), gender, etPasswordKonfirmasi.getText().toString(),
                        etTglLahir.getText().toString()).enqueue(new Callback<BaseNetworkCallback>() {
                    @Override
                    public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                        dialog.dismiss();
                        if(response.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(Registrasi.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registrasi.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            dialogMessage(response.body().getMessage());
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
//        spinnerGender.setAdapter(new ArrayAdapter<String>(this,R.array.gender_array, android.R.layout.simple_spinner_item));
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
        }

        return true;
    }
}
