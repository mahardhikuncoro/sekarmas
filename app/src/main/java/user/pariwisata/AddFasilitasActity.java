package user.pariwisata;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class AddFasilitasActity extends BaseDialogActivity {


    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.et_fasilitas)
    EditText etFasilitas;
    @BindView(R.id.et_harga)
    EditText etHarga;
    @BindView(R.id.et_satuan)
    EditText etSatuan;
    Integer idUmkm = 0;

    int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_fasilitas_activity);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        idUmkm= getIntent().getIntExtra(ParameterKey.ID_UMKM, 0);
        progressBar.setVisibility(View.GONE);

    }

    @OnClick(R.id.btn_simpan)
    public void clickPost(){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else if(etFasilitas.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNamaFasilitas);
        } else if(etHarga.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorHarga);
        }else if(etSatuan.getText().toString().equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorSatuan);
        }
        else {
            KontentWisata request = new KontentWisata();
            request.setPariwisataId(idUmkm);
            request.setNama(etFasilitas.getText().toString());
            request.setHarga(etHarga.getText().toString());
            request.setSatuan(etSatuan.getText().toString());
            pariwisataEndpoint.addFasilitasWisata("Bearer " + userdata.select().getAccesstoken(),request).enqueue(new Callback<PariwisataJson>() {
                @Override
                public void onResponse(Call<PariwisataJson> call, Response<PariwisataJson> response) {
                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        if(response.body().getSuccess()){
                            dialogMessage(getResources().getString(R.string.suksesAddFasilitas), true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PariwisataJson> call, Throwable t) {

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
