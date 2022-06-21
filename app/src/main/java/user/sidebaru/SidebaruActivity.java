package user.sidebaru;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;

import base.data.umkmmodel.UmkmModel;
import base.data.umkmmodel.umkmlist.UmkmListJson;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.screen.BaseDialogActivity;
import base.service.umkm.UmkmEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.DataMenuModel;
import base.sqlite.model.FormData;
import base.sqlite.model.Userdata;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ops.screen.fragment.UmkmAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SidebaruActivity extends BaseDialogActivity {


    @BindView(R.id.recycleMenuEntry)
    RecyclerView recyclerView;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout _swiperefresh;


    private Config config;
    private ArrayList<UmkmModel> umkmList;
    private UmkmEndpoint umkmEndpoint;
    private NetworkConnection networkConnection;
    private Userdata userdata;
    private ArrayList<String> menulist;
    private ArrayList<DataMenuModel> dataModels;
    private String assignedType ="allassigned", tc = "5.0";
    private Dialog dialog;
    private Boolean isNew ;
    private FormData formData;
    protected Bundle bundle;
    protected TelephonyManager telephonyManager;
    private UmkmAdapter umkmAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebaru);
        ButterKnife.bind(this);
        initiateApiData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        prepare();
        retreiveDataUmkmList();
    }

    protected void initiateApiData(){
        config = new Config(this);
        networkConnection = new NetworkConnection(this);
        userdata = new Userdata(this);
        formData = new FormData(this);
        bundle = new Bundle();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
        httpclient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("parameter", "value").build();
                return chain.proceed(request);
            }
        });

        umkmEndpoint = retrofit.create(UmkmEndpoint.class);

        telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    }

    private void prepare(){
        _swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isNew = null;
                Log.e("MASUK ","MASUK REFRESH ");
                retreiveDataUmkmList();
            }
        });
    }

    protected void dialog(int rString) {
        new MaterialDialog.Builder(this)
                .title(rString).icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .positiveText(R.string.buttonClose)
                .positiveColor(getResources().getColor(R.color.black))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(true)
                .show();
    }
    protected void dialogMessage(String rString) {
        new MaterialDialog.Builder(this)
                .title(rString).icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(true)
                .show();
    }
    public void retreiveDataUmkmList(){

        _swiperefresh.setRefreshing(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()) {
            _swiperefresh.setRefreshing(false);
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {

            umkmEndpoint.getListUmkm("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<UmkmListJson>() {
                @Override
                public void onResponse(Call<UmkmListJson> call, Response<UmkmListJson> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        _swiperefresh.setRefreshing(false);
                        if(response.body().getData() != null) {
                            umkmList = new ArrayList<>();
                            for(UmkmModel umkmModel : response.body().getData()){
                                UmkmModel model = new UmkmModel();
                                model.setId(umkmModel.getId());
                                model.setNama(umkmModel.getNama());
                                model.setAlamat(umkmModel.getAlamat());
                                model.setSektorId(umkmModel.getSektorId());
                                model.setStatus(umkmModel.getStatus());
                                model.setProfilePicture(umkmModel.getProfilePicture());
                                umkmList.add(model);
                            }
                            setAdapter();
                        }
                    } else{
                        dialog.dismiss();
                        dialogMessage(getResources().getString(R.string.errorBackend));
                    }
                }

                @Override
                public void onFailure(Call<UmkmListJson> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }

    }

    private  void setAdapter(){
        dialog.dismiss();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        umkmAdapter = new UmkmAdapter(umkmList, this);
        umkmAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(umkmAdapter);

    }
    @OnClick(R.id.create_umkm_button)
    public void createUmkmClick(){
        Intent intent = new Intent(this, CreateSidebaru.class);
        startActivity(intent);
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
