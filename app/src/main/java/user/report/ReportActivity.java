package user.report;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import base.data.laporan.DataLaporan;
import base.data.reportmodel.ReportModel;
import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.location.BaseNetworkCallback;
import base.screen.BaseDialogActivity;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.adapter.KategoriReportAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author KUN <robert.kuncoro@pitik.id>
 */
public class ReportActivity extends BaseDialogActivity {
    @BindView(R.id.rv_pengaturan)
    RecyclerView recyclerView;
    KategoriReportAdapter kategoriReportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initiateApiData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        getReportkategori();
    }

    public void getReportkategori() {
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
            Call<SektorJson> resetPasswordJsonCall = reportEndpoint.getListKategori("Bearer " + userdata.select().getAccesstoken());
            resetPasswordJsonCall.enqueue(new Callback<SektorJson>() {
                @Override
                public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        kategoriReportAdapter = new KategoriReportAdapter(ReportActivity.this, response.body().getData(), new KategoriReportAdapter.ReportInterface() {
                            @Override
                            public void onListSelected(SektorModel model) {
                                reportKontent(model);
                            }
                        });
                        recyclerView.setAdapter(kategoriReportAdapter);
                    } else {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<SektorJson> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void reportKontent(SektorModel model){
        String kontentType = getIntent().getStringExtra("type");
        final ReportModel request = new ReportModel();
        if(kontentType.equals("pengaduan")) {
            DataLaporan dataLaporan;
            dataLaporan = (DataLaporan) getIntent().getParcelableExtra("kontent");
            request.setObjectId(dataLaporan.getId());
            request.setObjectType(kontentType);
            request.setHidden(getIntent().getBooleanExtra("ishide", false));
            request.setReported(getIntent().getBooleanExtra("isreport", false));
            request.setKategoriId(model.getId());
            request.setDeskripsi("");
        }else if(kontentType.equals("pariwisata")){
            request.setObjectId(String.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0)));
            request.setObjectType(kontentType);
            request.setHidden(getIntent().getBooleanExtra("ishide", false));
            request.setReported(getIntent().getBooleanExtra("isreport", false));
            request.setKategoriId(model.getId());
            request.setDeskripsi("");
        }

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

            reportEndpoint.report("Bearer " + userdata.select().getAccesstoken(), request).enqueue(new Callback<BaseNetworkCallback>() {
                @Override
                public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                    dialog.dismiss();
                    if(response.body().getSuccess()){
                        dialogMessage("Terima kasih, Laporan anda akan kami proses !", true);
                    }else{
                        dialogMessage(getResources().getString(R.string.errorBackend),false);
                    }
                }

                @Override
                public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {
                    dialog.dismiss();
                    dialogMessage(getResources().getString(R.string.errorBackend),false);
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
