package user.pariwisata;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import base.data.pariwisatamodel.KontentWisata;
import base.data.pariwisatamodel.PariwisataJson;
import base.screen.BaseDialogActivity;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOperasionalActity extends BaseDialogActivity {


    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.et_jam_buka_senin)
    EditText etBukaSenin;

    @BindView(R.id.et_jam_tutup_senin)
    EditText etTutupSenin;

    @BindView(R.id.et_jam_buka_selasa)
    EditText etBukaSelasa;
    @BindView(R.id.et_jam_tutup_selasa)
    EditText etTutupSelasa;

    @BindView(R.id.et_jam_buka_rabu)
    EditText etBukaRabu;
    @BindView(R.id.et_jam_tutup_rabu)
    EditText etTutupRabu;

    @BindView(R.id.et_jam_buka_kamis)
    EditText etBukaKamis;
    @BindView(R.id.et_jam_tutup_kamis)
    EditText etTutupKamis;

    @BindView(R.id.et_jam_buka_jumat)
    EditText etBukaJumat;
    @BindView(R.id.et_jam_tutup_jumat)
    EditText etTutupJumat;

    @BindView(R.id.et_jam_buka_sabtu)
    EditText etBukaSabtu;
    @BindView(R.id.et_jam_tutup_sabtu)
    EditText etTutupSabtu;

    @BindView(R.id.et_jam_buka_minggu)
    EditText etBukaMinggu;
    @BindView(R.id.et_jam_tutup_minggu)
    EditText etTutupMinggu;

    @BindView(R.id.tv_title_sidebaru)
    TextView tvTitle;
    Integer idUmkm = 0;

    int PERMISSION_ALL = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_operasional_activity);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        idUmkm= getIntent().getIntExtra(ParameterKey.ID_UMKM, 0);
        tvTitle.setText("Tambah Jam Operasional");
        progressBar.setVisibility(View.GONE);

        etBukaSenin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaSenin.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupSenin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTutupSenin.setFocusable(false);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupSenin.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        etBukaSelasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etBukaSelasa.setFocusable(false);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaSelasa.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        etTutupSelasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTutupSelasa.setFocusable(false);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupSelasa.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etBukaRabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etBukaRabu.setFocusable(false);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaRabu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupRabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTutupRabu.setFocusable(false);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupRabu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etBukaKamis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaKamis.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupKamis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupKamis.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        etBukaJumat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaJumat.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupJumat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupJumat.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etBukaSabtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaSabtu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupSabtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupSabtu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etBukaMinggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etBukaMinggu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        etTutupMinggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOperasionalActity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTutupMinggu.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    @OnClick(R.id.btn_simpan)
    public void clickPost(){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } /*else if(etBukaSenin.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNamaFasilitas);
        }else if(etSatuan.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorSatuan);
        }*/
        else {
            KontentWisata request = new KontentWisata();
            request.setPariwisataId(idUmkm);
            request.setSenin(etBukaSenin.getText().toString() + " - " + etTutupSenin.getText().toString());
            request.setSelasa(etBukaSelasa.getText().toString() + " - " + etTutupSelasa.getText().toString());
            request.setRabu(etBukaRabu.getText().toString() + " - " + etTutupRabu.getText().toString());
            request.setKamis(etBukaKamis.getText().toString() + " - " + etTutupKamis.getText().toString());
            request.setJumat(etBukaJumat.getText().toString() + " - " + etTutupJumat.getText().toString());
            request.setSabtu(etBukaSabtu.getText().toString() + " - " + etTutupSabtu.getText().toString());
            request.setMinggu(etBukaMinggu.getText().toString() + " - " + etTutupMinggu.getText().toString());
            pariwisataEndpoint.addOperasional("Bearer " + userdata.select().getAccesstoken(),request).enqueue(new Callback<PariwisataJson>() {
                @Override
                public void onResponse(Call<PariwisataJson> call, Response<PariwisataJson> response) {
                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        if(response.body().getSuccess()){
                            dialogMessage(getResources().getString(R.string.suksesAddOperasional), true);
                        }
                    }else{
                        dialogMessage(getResources().getString(R.string.errorBackend));
                    }
                }

                @Override
                public void onFailure(Call<PariwisataJson> call, Throwable t) {
                    dialogMessage(getResources().getString(R.string.errorBackend));
                }
            });
        }
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
