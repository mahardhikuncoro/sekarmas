package user.informasi;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import base.data.informationmodel.Information;
import base.screen.BaseDialogActivity;
import base.sqlite.model.InformasiModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.adapter.InformasiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformasiActivity extends BaseDialogActivity {

    private ArrayList<InformasiModel> news;
    @BindView(R.id.taskListRecycle)
    RecyclerView rvInformasi;

    @BindView(R.id.linearRecycle)
    LinearLayout _linearRecycle;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout _swiperefresh;

    private InformasiAdapter informasiAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi);
        ButterKnife.bind(this);
        initiateApiData();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InformasiActivity.this, LinearLayoutManager.VERTICAL,false);
        rvInformasi.setLayoutManager(linearLayoutManager);
        rvInformasi.setHasFixedSize(true);

        prepare();
        getInformation();

    }
    private void prepare(){
        _swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInformation();
            }
        });
    }


    protected void getInformation(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        _swiperefresh.setRefreshing(true);
        if (!networkConnection.isNetworkConnected()) {
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            informationEndpoint.getInformationList("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<List<Information>>() {
                @Override
                public void onResponse(Call<List<Information>> call, Response<List<Information>> response) {
                    if(response.isSuccessful()){
                        dialog.dismiss();
                        _swiperefresh.setRefreshing(false);
                        news = new ArrayList<>();
                        for(int i = 0; i<response.body().size() ;i++){
                            InformasiModel newsmod = new InformasiModel();
                            newsmod.setNewsId(response.body().get(i).getId());
                            newsmod.setNewsTitle(response.body().get(i).getTitle());
                            newsmod.setNewsDesc(response.body().get(i).getDescription());
                            newsmod.setActive(response.body().get(i).getTitle());
                            newsmod.setImageUrl(response.body().get(i).getImageUrl());
                            newsmod.setCreateDate(response.body().get(i).getCreatedAt());
                            news.add(newsmod);
                        }
                        setAdapter(news);
                    }else {
                        _swiperefresh.setRefreshing(false);
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<List<Information>> call, Throwable t) {

                }
            });
        }
    }

    private void setAdapter(ArrayList<InformasiModel> news) {
        informasiAdapter = new InformasiAdapter(this, news);
        informasiAdapter.notifyDataSetChanged();
        rvInformasi.setAdapter(informasiAdapter);
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
