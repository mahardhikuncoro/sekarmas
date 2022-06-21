package user.kontakdarurat;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import base.screen.BaseDialogActivity;
import base.service.kontak.Kontak;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.adapter.KontakAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author KUN <robert.kuncoro@pitik.id>
 */
public class KontakDaruratActivity extends BaseDialogActivity {

    @BindView(R.id.rv_kotak)
    RecyclerView rvKontak;

    @BindView(R.id.pb_kontak)
    ProgressBar pbKontak;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontak_darurat);
        ButterKnife.bind(this);
        initiateApiData();
        linearLayoutManager = new LinearLayoutManager(this);
        showKontak();
    }

    public void showKontak() {
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
            kontakEndpoint.getKontak("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<List<Kontak>>() {
                @Override
                public void onResponse(Call<List<Kontak>> call, Response<List<Kontak>> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        pbKontak.setVisibility(View.GONE);
                        ArrayList<Kontak> listKontak = new ArrayList<>();
                        for (Kontak kontakModel : response.body()) {
                            Kontak kontak = new Kontak();
                            kontak.setName(kontakModel.getName());
                            kontak.setPhone(kontakModel.getPhone());
                            listKontak.add(kontak);
                        }
                        KontakAdapter kontakAdapter = new KontakAdapter(getApplicationContext(), listKontak);
                        rvKontak.setLayoutManager(linearLayoutManager);
                        rvKontak.setAdapter(kontakAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Kontak>> call, Throwable t) {

                }
            });
        }
    }

    @OnClick(R.id.iv_backbutton)
    public void clickBack(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
