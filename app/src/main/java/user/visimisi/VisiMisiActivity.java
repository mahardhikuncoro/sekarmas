package user.visimisi;

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
import base.data.visimisimodel.Misi;
import base.data.visimisimodel.Visi;
import base.data.visimisimodel.VisiMisi;
import base.screen.BaseDialogActivity;
import base.sqlite.model.InformasiModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import ops.screen.TaskListAdapter;
import ops.screen.adapter.InformasiAdapter;
import ops.screen.adapter.TaskListMisiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisiMisiActivity extends BaseDialogActivity {

    private ArrayList<Visi> taskListList;
    private ArrayList<Misi> taskListListMisi;
    private TaskListAdapter taskListAdapter;
    private TaskListMisiAdapter taskListAdapterMisi;

    @BindView(R.id.taskListRecycleAll)
    RecyclerView taskListRecycleAll;

    @BindView(R.id.taskListRecycleMisi)
    RecyclerView taskListRecycleMisi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visi_misi);
        ButterKnife.bind(this);
        initiateApiData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        taskListRecycleAll.setLayoutManager(linearLayoutManager);
        taskListRecycleAll.setHasFixedSize(true);
        taskListRecycleAll.smoothScrollToPosition(10);

        LinearLayoutManager linearLayoutManagermisi = new LinearLayoutManager(this);
        linearLayoutManagermisi.setOrientation(LinearLayoutManager.VERTICAL);

        taskListRecycleMisi.setLayoutManager(linearLayoutManagermisi);
        taskListRecycleMisi.setHasFixedSize(true);
        taskListRecycleMisi.smoothScrollToPosition(10);

        getVisiDanMisi();

    }

    private void getVisiDanMisi(){
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
            visiMisiEndpoint.getVisiMisi("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<VisiMisi>() {
                @Override
                public void onResponse(Call<VisiMisi> call, Response<VisiMisi> response) {
                    if(response.isSuccessful()){
                        dialog.dismiss();
                        taskListList = new ArrayList<Visi>();
                        taskListListMisi = new ArrayList<Misi>();
                        for(int i = 0; i<response.body().getData().size();i++) {
                            VisiMisi visiMisi = new VisiMisi();
                            visiMisi.setData(response.body().getData());
                            for (int j=0;j<response.body().getData().get(i).getVisi().size();j++){
                                Visi visi = new Visi();
                                visi.setContent(visiMisi.getData().get(i).getVisi().get(j).getContent());
                                taskListList.add(visi);
                            }
                            for (int j=0;j<response.body().getData().get(i).getMisi().size();j++){
                                Misi misi = new Misi();
                                misi.setContent(visiMisi.getData().get(i).getMisi().get(j).getContent());
                                taskListListMisi.add(misi);
                            }
                        }
                        setAdapter();
                    }else {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<VisiMisi> call, Throwable t) {

                }
            });

        }
    }

    private void setAdapter() {
        taskListAdapter = new TaskListAdapter(this, taskListList);
        taskListAdapter.notifyDataSetChanged();
        taskListRecycleAll.setAdapter(taskListAdapter);

        taskListAdapterMisi = new TaskListMisiAdapter(this, taskListListMisi);
        taskListAdapterMisi.notifyDataSetChanged();
        taskListRecycleMisi.setAdapter(taskListAdapterMisi);
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
