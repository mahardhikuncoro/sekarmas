package user.pariwisata;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import base.data.pariwisatamodel.PariwisataJson;
import base.data.pariwisatamodel.PariwisataModel;
import base.data.reportmodel.ReportJson;
import base.data.reportmodel.ReportModel;
import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.screen.BaseDialogActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.adapter.KategoriAdapter;
import ops.screen.adapter.KategoriReportAdapter;
import ops.screen.adapter.PariwisataAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.report.ReportActivity;

public class PariwisataActivity extends BaseDialogActivity implements KategoriAdapter.OnKategoriListener{

    @BindView(R.id.taskListRecycle)
    RecyclerView rvWisata;

    @BindView(R.id.rv_kategori)
    RecyclerView rvKategori;

    @BindView(R.id.linearRecycle)
    LinearLayout _linearRecycle;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout _swiperefresh;

    private ArrayList<PariwisataModel> wisataList;
    private PariwisataAdapter pariwisataAdapter;
    private KategoriAdapter kategoriAdapter;
    private Integer selectedId = 0;
    private ArrayList<SektorModel> kategoriList;
    private ArrayList<ReportModel> reportList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pariwisata);
        ButterKnife.bind(this);
        initiateApiData();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PariwisataActivity.this, LinearLayoutManager.VERTICAL,false);
        rvWisata.setLayoutManager(linearLayoutManager);
        rvWisata.setHasFixedSize(true);

        LinearLayoutManager linearLayoutKategori = new LinearLayoutManager(PariwisataActivity.this, LinearLayoutManager.HORIZONTAL,false);
        rvKategori.setLayoutManager(linearLayoutKategori);
        rvKategori.setHasFixedSize(true);

        prepare();
        getBlackList();
    }

    private void getKategori() {
//        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
//            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            pariwisataEndpoint.getKategori("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<SektorJson>() {
                @Override
                public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                    if (response.isSuccessful()) {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        sektorNames = new ArrayList<>();
                        kategoriList = new ArrayList<>();
                        kategoriList.addAll(response.body().getData());
                        setAdapterKategori();
                    }
                }

                @Override
                public void onFailure(Call<SektorJson> call, Throwable t) {

                }
            });

        }
    }

    private void setAdapterKategori() {
        selectedId = kategoriList.get(0).getId();
        kategoriAdapter = new KategoriAdapter(PariwisataActivity.this, kategoriList, this,selectedId);
        rvKategori.setAdapter(kategoriAdapter);
        getPariwisata();
    }

    private void prepare(){
        _swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPariwisata();
            }
        });
    }


    protected void getPariwisata(){
        if(dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setView(R.layout.progress_bar).setCancelable(false);
            }
            dialog = builder.create();
            dialog.show();
        }
        _swiperefresh.setRefreshing(true);
        if (!networkConnection.isNetworkConnected()) {
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            pariwisataEndpoint.getPariwisataByKategory("Bearer " + userdata.select().getAccesstoken(), selectedId).enqueue(new Callback<PariwisataJson>() {
                @Override
                public void onResponse(Call<PariwisataJson> call, Response<PariwisataJson> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        _swiperefresh.setRefreshing(false);
                        if(response.body().getData() != null) {
                            wisataList = new ArrayList<>();
                            wisataList.addAll(response.body().getData());
                            for(PariwisataModel pariwisataModel : response.body().getData()) {
                                for(ReportModel reportModel : reportList){
                                    if(String.valueOf(pariwisataModel.getId()).equals(reportModel.getObjectId())) {
                                        if(reportModel.getObjectType().equals("pariwisata") && (reportModel.getIsHidden()== 1 || reportModel.getIsReported() == 1)) {
                                            wisataList.remove(pariwisataModel);
                                        }
                                    }
                                }
                            }
                            setAdapter(selectedId);
                        }
                    } else{
                        dialog.dismiss();
                        dialogMessage(getResources().getString(R.string.errorBackend));
                    }
                }
                @Override
                public void onFailure(Call<PariwisataJson> call, Throwable t) {
                    _swiperefresh.setRefreshing(false);
                    dialogMessage(getResources().getString(R.string.errorBackend));
                }
            });
        }
    }

    protected void getBlackList(){
        if (!networkConnection.isNetworkConnected()) {
            dialog(R.string.errorNoInternetConnection);
        } else {
            Call<ReportJson> resetPasswordJsonCall = reportEndpoint.getBlackList("Bearer " + userdata.select().getAccesstoken(), userdata.select().getId());
            resetPasswordJsonCall.enqueue(new Callback<ReportJson>() {
                @Override
                public void onResponse(Call<ReportJson> call, Response<ReportJson> response) {
                    if (response.isSuccessful()) {
                        reportList = new ArrayList<>();
                        reportList.addAll(response.body().getData());
                        getKategori();
                    } else {
                        dialogMessage(getResources().getString(R.string.errorBackend), false);
                    }
                }

                @Override
                public void onFailure(Call<ReportJson> call, Throwable t) {
                }
            });
        }
    }
    
    private  void setAdapter(int selectedId){
        dialog.dismiss();
        pariwisataAdapter = new PariwisataAdapter(wisataList, this, selectedId);
        pariwisataAdapter.notifyDataSetChanged();
        rvWisata.setAdapter(pariwisataAdapter);

    }


    @OnClick(R.id.iv_backbutton)
    public void clickBack(){
        finish();
    }

    @OnClick(R.id.create_post_button)
    public void clickCreatePariwisata(){
        Intent intentCreate = new Intent(this, CreatePariwisata.class);
        startActivity(intentCreate);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        selectedId = kategoriList.get(position).getId();
        kategoriAdapter = new KategoriAdapter(PariwisataActivity.this, kategoriList, this, kategoriList.get(position).getId());
        rvKategori.setAdapter(kategoriAdapter);
        rvKategori.scrollToPosition(position);
//        getPariwisataByKategori(kategoriList.get(position).getId());
//        setAdapter(kategoriList.get(position).getId());
        getPariwisata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog!= null){
            dialog.dismiss();
        }
       getBlackList();
    }
}
