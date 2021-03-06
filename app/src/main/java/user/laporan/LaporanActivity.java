package user.laporan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import base.data.laporan.DataLaporan;
import base.data.laporan.LaporanJson;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.screen.BaseDialogActivity;
import base.service.laporan.LaporanEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.FormData;
import base.sqlite.model.Userdata;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.TaskListActivity;
import ops.screen.adapter.LaporanAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaporanActivity extends BaseDialogActivity {

    @BindView(R.id.taskListRecycle)
    RecyclerView recyclerView;

    @BindView(R.id.linearRecycle)
    LinearLayout _linearRecycle;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout _swiperefresh;

    LaporanAdapter laporanAdapter;

    ArrayList<DataLaporan> laporaArrayList;

    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        ButterKnife.bind(this);
        initiateApiData();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)) {
            dialogPermission();
        }else {
            initView();
            prepare();
            callListLaporan();
        }
    }


    private void prepare(){
        _swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               callListLaporan();
            }
        });
    }


    private void callListLaporan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        _swiperefresh.setRefreshing(true);
        if (!networkConnection.isNetworkConnected()) {
            _swiperefresh.setRefreshing(false);
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            laporanEndpoint.getListLaporan("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<LaporanJson>() {
                @Override
                public void onResponse(Call<LaporanJson> call, Response<LaporanJson> response) {
                    dialog.dismiss();
                    _swiperefresh.setRefreshing(false);
                    _linearRecycle.setVisibility(View.VISIBLE);
                    if (response.isSuccessful()){
                        laporaArrayList = new ArrayList<>();
                        for(DataLaporan data : response.body().getData()) {
                            laporaArrayList.add(data);
                        }
                        setAdapter();
                    }
                }

                @Override
                public void onFailure(Call<LaporanJson> call, Throwable t) {
                    dialog.dismiss();
                }
            });

        }
    }

    private void setAdapter() {
        laporanAdapter = new LaporanAdapter(this, laporaArrayList);
        laporanAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(laporanAdapter);

    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LaporanActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

    }

    protected void dialogPermission() {
        new MaterialDialog.Builder(this)
                .content("Please allow all permission on your app setting, thank you")
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .cancelable(false)
                .show();
    }

    protected void dialogMessage(String rString) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_application:
//                dialog(R.string.addPinNumber);
                return true;
//                break;
            // action with ID action_settings was selected
            case R.id.action_logout:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                return true;
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.create_post_button)
    public void createPost(){
      Intent intent = new Intent(this, CreateLaporan.class);
      startActivity(intent);
    }

    public void gototask(String tc,String assignedType,String desc, String isAdd){

        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("FLAG_SUBMIT","1");
        i.putExtra(ParameterKey.ASSIGNED_TYPE,assignedType);
        i.putExtra(ParameterKey.ASSIGNED_TC,tc);
        i.putExtra(ParameterKey.MENU_DESC,desc);
        i.putExtra(ParameterKey.IS_ADD, isAdd );
        startActivity(i);
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
