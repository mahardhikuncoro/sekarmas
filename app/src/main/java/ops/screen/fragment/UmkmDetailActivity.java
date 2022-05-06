package ops.screen.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.data.umkmmodel.UmkmModel;
import base.screen.DetailsGridViewActivity;
import base.screen.GridItem;
import base.screen.GridViewActivity;
import base.screen.GridViewAdapter;
import base.screen.GridViewDetailAdapter;
import base.service.URL;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.CameraActivity;
import ops.screen.CreateUmkm;
import ops.screen.MainActivityDashboard;
import ops.screen.MapsDetailUmkmActivity;
import ops.screen.UpdateUmkm;
import ops.screen.offline.FormOfflineDocument;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.changepassword.ChangePasswordActivity;

public class UmkmDetailActivity extends BaseDialogActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btnback_toolbar) ImageView btnback_toolbar;
    @BindView(R.id.etDataNamasidebaru)
    EditText etNamaSidebar;
    @BindView(R.id.etAlamatsidebaru)
    EditText etAlamatSidebar;
    @BindView(R.id.et_sektor)
    EditText etSektor;
    @BindView(R.id.etEmailsidebaru)
    EditText etEmail;
    @BindView(R.id.etTelepon)
    EditText etTelepon;
    @BindView(R.id.et_handphone)
    EditText etHandphone;
    @BindView(R.id.etProvinsi)
    EditText etProvinsi;
    @BindView(R.id.etkabupaten)
    EditText etKabupaten;
    @BindView(R.id.etkecamatan)
    EditText etKecamatan;
    @BindView(R.id.etkelurahan)
    EditText etKelurahan;
    @BindView(R.id.etkodepos)
    EditText etKodePos;
    @BindView(R.id.etdeskripsi)
    EditText etDeskripsi;
    @BindView(R.id.et_produk_utama)
    EditText etProdukUtama;
    @BindView(R.id.tv_title_gridview)
    TextView tvTitleGridview;
    @BindView(R.id.iv_profile_sidebaru)
    ImageView ivProfile;


    @BindView(R.id.post_progress_bar) ProgressBar progressBar;
    @BindView(R.id.img_upload) ImageView ivProfileUmkm;
    @BindView(R.id.fb_delete) FloatingActionButton fbDelete;
    @BindView(R.id.fb_edit) FloatingActionButton fbEdit;
    @BindView(R.id.multiple_actions) FloatingActionsMenu  menuMultipleActions ;
    @BindView(R.id.gridView_detail) GridView mGridView;

    private Dialog dialog;
    protected Bundle bundle;
    private RiwayatAdapter _riwayatAdapter;
    private PosisiAdapter _posisiAdapter;
    private Integer idSektor=0;

    private GridViewDetailAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    protected Double longitude ;
    protected Double latitude ;

    private Integer selectedIdSektor;
    private String sectorName;
    private int selectedPosition;
    ArrayList<String> sektorNames;
    ArrayList<SektorModel> sektorModels;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umkm_detail_activity);
        ButterKnife.bind(this);
        transparentStatusbar();
        initiateApiData();
        getLastLocation();
        toolbar.inflateMenu(R.menu.menu_actionbar);
        setToolbar();
        //Initialize with empty data


        if(getIntent().getStringExtra(ParameterKey.ID_UMKM) != null){
            retreiveDataDetailHistory(getIntent().getStringExtra(ParameterKey.ID_UMKM));
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(UmkmDetailActivity.this, DetailsGridViewActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getDesc()).
                        putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });
    }




    public void retreiveDataDetailHistory(String idUmkm){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {

            umkmEndpoint.getDetailUmkm("Bearer " + userdata.select().getAccesstoken(), idUmkm).enqueue(new Callback<UmkmModel>() {
                @Override
                public void onResponse(Call<UmkmModel> call, Response<UmkmModel> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(response.isSuccessful()) {
                        etNamaSidebar.setText(response.body().getNama()==null?"":response.body().getNama());
                        etAlamatSidebar.setText(response.body().getAlamat()==null?"":response.body().getAlamat());
                        etHandphone.setText(String.valueOf(response.body().getHandphone()==null?"":response.body().getHandphone()));
                        etTelepon.setText(String.valueOf(response.body().getTelepon()==null?"":response.body().getTelepon()));
                        etEmail.setText(String.valueOf(response.body().getEmail()==null?"":response.body().getEmail()));
                        etProvinsi.setText(String.valueOf(response.body().getProvinsi()==null?"":response.body().getProvinsi()));
                        etKabupaten.setText(String.valueOf(response.body().getKabupaten()==null?"":response.body().getKabupaten()));
                        etKecamatan.setText(String.valueOf(response.body().getKecamatan()==null?"":response.body().getKecamatan()));
                        etKelurahan.setText(String.valueOf(response.body().getKelurahan()==null?"":response.body().getKelurahan()));
                        etKodePos.setText(String.valueOf(response.body().getKodepos()==null?"":response.body().getKodepos()));
                        etDeskripsi.setText(String.valueOf(response.body().getDescription()==null?"":response.body().getDescription()));
                        idSektor =response.body().getSektorId()==null ? 0 : response.body().getSektorId();
                        longitude= Double.valueOf(response.body().getLongitude());
                        latitude= Double.valueOf(response.body().getLatitude());

                        if (response.body().getProfilePicture() != null || !response.body().getProfilePicture().equals("")) {
                            OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                            Picasso picasso = new Picasso.Builder(UmkmDetailActivity.this).downloader(new OkHttp3Downloader(picassoClient)).build();
                            picasso.setLoggingEnabled(true);
                            picasso.load(String.valueOf(response.body().getProfilePicture()))
                                    .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                                    .error(R.drawable.ic_profile).resize(140, 140).rotate(0)
                                    .into(ivProfile, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("SUKSES", " ");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("ERROR", " ");

                                        }
                                    });
                        }

                        Log.e("USER ID"," : " + userdata.select().getId());
                        if(userdata.select().getId().equals(response.body().getCreatedBy())){
                            fbDelete.setVisibility(View.VISIBLE);
                            fbEdit.setVisibility(View.VISIBLE);
                        }

                        mGridData = new ArrayList<>();
                        if(response.body().getProduk()!=null) {
                            for (int i = 0; i < response.body().getProduk().size(); i++) {
                                GridItem gridItem = new GridItem();
                                gridItem.setImage(response.body().getProduk().get(i).getUrl());
                                gridItem.setId(response.body().getProduk().get(i).getId());
                                gridItem.setDesc(response.body().getProduk().get(i).getName());
                                gridItem.setBitmapImage(null);
                                gridItem.setUri(null);
                                gridItem.setFileToUpload(null);
                                mGridData.add(gridItem);
                            }

                            mGridAdapter = new GridViewDetailAdapter(UmkmDetailActivity.this, R.layout.grid_item_layout, mGridData);
                            mGridView.setAdapter(mGridAdapter);
                        }else{
                            tvTitleGridview.setVisibility(View.GONE);
                        }
                        getSektor();
                    }
                }

                @Override
                public void onFailure(Call<UmkmModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    private void getSektor() {
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            umkmEndpoint.getSektorUmkm("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<SektorJson>() {
                @Override
                public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        sektorNames = new ArrayList<>();
                        sektorModels = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            SektorModel sektorModel = new SektorModel();
                            sektorModel.setId(response.body().getData().get(i).getId());
                            sektorModel.setName(response.body().getData().get(i).getName());
                            sektorModels.add(sektorModel);
                            sektorNames.add(response.body().getData().get(i).getName());
                            if(idSektor==response.body().getData().get(i).getId()){
                                sectorName=response.body().getData().get(i).getName();
                                etSektor.setText(sectorName);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<SektorJson> call, Throwable t) {

                }
            });

        }
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d("TAG", e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(UmkmDetailActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            //Hide progressbar
//            mProgressBar.setVisibility(View.GONE);
        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

//    *//**
//     * Parsing the feed results and get the list
//     *
//     * @param result
//     *//*
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                item = new GridItem();
                item.getDesc();
                JSONArray attachments = post.getJSONArray("attachments");
                if (null != attachments && attachments.length() > 0) {
                    JSONObject attachment = attachments.getJSONObject(0);
                    if (attachment != null)
                        item.setImage(attachment.getString("url"));
                }
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fb_delete)
    public void deleteClick(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(UmkmDetailActivity.this).create();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.hapus_sidebaru, null);
        final Button _fotoUlangButton = (Button) dialogView.findViewById(R.id.fotoUlangButton);
        final Button _kembaliButton = (Button) dialogView.findViewById(R.id.kembaliButton);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        _fotoUlangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umkmEndpoint.deleteUmkm("Bearer " + userdata.select().getAccesstoken(), getIntent().getStringExtra(ParameterKey.ID_UMKM)).enqueue(new Callback<SektorJson>() {
                    @Override
                    public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                        if(response.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), MainActivityDashboard.class);
                            intent.putExtra(ParameterKey.SCREEN_UMKM, true);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<SektorJson> call, Throwable t) {

                    }
                });
            }
        });
        _kembaliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.cancel();
            }
        });

    }

    @OnClick(R.id.fb_edit)
    public void editeClick(){
        Intent intent = new Intent(UmkmDetailActivity.this, UpdateUmkm.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getStringExtra(ParameterKey.ID_UMKM));
        startActivity(intent);
    }

    @OnClick(R.id.fb_tambah_gambar)
    public void tambahGambarClick(){
        Intent intent = new Intent(UmkmDetailActivity.this, GridViewActivity.class);
        intent.putExtra(ParameterKey.ID_UMKM,Integer.valueOf(getIntent().getStringExtra(ParameterKey.ID_UMKM)));
        startActivity(intent);
    }


    @OnClick(R.id.btntutupDetail)
    public void tutupDetail(){
        Intent i = new Intent(UmkmDetailActivity.this, MainActivityDashboard.class);
        i.putExtra(ParameterKey.SCREEN_UMKM, true);
        startActivity(i);
    }

    @OnClick(R.id.btn_image_bar_loc)
    public void lihatLokasi(){
        Intent i = new Intent(UmkmDetailActivity.this, MapsDetailUmkmActivity.class);
        i.putExtra("LONGITUDE",longitude);
        i.putExtra("LATITUDE",latitude);
        i.putExtra("NAMA_UMKM",etNamaSidebar.getText().toString());
        i.putExtra(ParameterKey.SCREEN_UMKM, true);
        startActivity(i);
    }

    private void setToolbar(){

        String id = (getIntent().getExtras().getString(ParameterKey.REGNO) == null ? "" : getIntent().getExtras().getString(ParameterKey.REGNO));
        String fullname = (getIntent().getExtras().getString("NAMA_NASABAH") == null ? "": getIntent().getExtras().getString("NAMA_NASABAH"));
//        txtIdUser.setText(id);
//        txtFullname.setText(fullname);
//        txtFullname.setAllCaps(true);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    // action with ID action_refresh was selected
                    case R.id.action_application:
                        final View addView = getLayoutInflater().inflate(R.layout.about_bexi, null);
                        new AlertDialog.Builder(UmkmDetailActivity.this).setTitle(R.string.companyName).setView(addView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                }).setNegativeButton("", null).show();

                        break;
                    case R.id.action_change_pass:
                        Intent intentchangepass = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(intentchangepass);
                        finish();
                        break;
                    case R.id.action_logout:
                        dialogLogout(R.string.asklogout);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
        Intent intent = new Intent(getApplicationContext(), MainActivityDashboard.class);
        intent.putExtra(ParameterKey.SCREEN_UMKM, true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivityDashboard.class);
        intent.putExtra(ParameterKey.SCREEN_UMKM, true);
        startActivity(intent);
    }
}

