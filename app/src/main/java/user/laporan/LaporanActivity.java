package user.laporan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
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
import base.data.reportmodel.ReportJson;
import base.data.reportmodel.ReportModel;
import base.screen.BaseDialogActivity;
import base.sqlite.model.TaskListDetailModel;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import ops.screen.TaskListActivity;
import ops.screen.adapter.LaporanAdapter;
import ops.screen.fragment.TaskListInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.report.ReportActivity;

public class LaporanActivity extends BaseDialogActivity {

    @BindView(R.id.taskListRecycle)
    RecyclerView recyclerView;

    @BindView(R.id.linearRecycle)
    LinearLayout _linearRecycle;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout _swiperefresh;

    LaporanAdapter laporanAdapter;
    ArrayList<DataLaporan> pengaduanList;
    private ArrayList<ReportModel> reportList;
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
            getBlackList();
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
                        pengaduanList = new ArrayList<>();
                        pengaduanList.addAll(response.body().getData());
                        if(reportList.size()>0 && pengaduanList.size() > 0) {
                            for (DataLaporan dataLaporan : response.body().getData()) {
                                for (ReportModel reportModel : reportList) {
                                    if (String.valueOf(dataLaporan.getId()).equals(reportModel.getObjectId())) {
                                        if (reportModel.getObjectType().equals("pengaduan") && (reportModel.getIsHidden() == 1 || reportModel.getIsReported() == 1)) {
                                            pengaduanList.remove(dataLaporan);
                                        }
                                    }
                                }
                            }
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
        laporanAdapter = new LaporanAdapter(this, pengaduanList, new TaskListInterface() {
            @Override
            public void onListSelected(TaskListDetailModel list) {

            }

            @Override
            public void onOptionClick(DataLaporan model) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(LaporanActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_ugc, null);
                final LinearLayout lnReport = (LinearLayout) dialogView.findViewById(R.id.lnReport);
                final LinearLayout lnHide = (LinearLayout) dialogView.findViewById(R.id.lnHide);
                final LinearLayout lnBlockandReport = (LinearLayout) dialogView.findViewById(R.id.lnBlockandReport);
                lnReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LaporanActivity.this, ReportActivity.class);
                        intent.putExtra("kontent", (Parcelable) model);
                        intent.putExtra("type", "pengaduan");
                        intent.putExtra("isreport", true);
                        startActivity(intent);
                        dialogBuilder.dismiss();
                    }
                });
                lnHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LaporanActivity.this, ReportActivity.class);
                        intent.putExtra("kontent", (Parcelable) model);
                        intent.putExtra("type", "pengaduan");
                        intent.putExtra("ishide", true);
                        startActivity(intent);
                        dialogBuilder.dismiss();
                    }
                });
                lnBlockandReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LaporanActivity.this, ReportActivity.class);
                        intent.putExtra("kontent", (Parcelable) model);
                        intent.putExtra("type", "pengaduan");
                        intent.putExtra("isblock", true);
                        startActivity(intent);
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });
        laporanAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(laporanAdapter);

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
                        callListLaporan();
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
        i.putExtra(ParameterKey.NAMA_UMKM,desc);
        i.putExtra(ParameterKey.IS_ADD, isAdd );
        startActivity(i);
    }

    @OnClick(R.id.iv_backbutton)
    public void clickBack(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(dialog!= null){
            dialog.dismiss();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlackList();
    }
}
