package user.sidebaru;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.data.umkmmodel.UmkmModel;
import base.screen.DetailsGridViewActivity;
import base.screen.GridItem;
import base.screen.AddImageUmkmActivity;
import base.screen.GridViewDetailAdapter;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.MapsDetailUmkmActivity;
import ops.screen.fragment.PosisiAdapter;
import ops.screen.fragment.RiwayatAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.DashboardActivity;

public class DetailSidebaruActivity extends BaseDialogActivity {

    @BindView(R.id.btnback_toolbar) ImageView btnback_toolbar;
    @BindView(R.id.tv_nama_sidebaru)
    TextView etNamaSidebar;
    @BindView(R.id.etAlamatsidebaru)
    TextView etAlamatSidebar;
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
        initiateApiData();
        getLastLocation();
        setToolbar();
        //Initialize with empty data


        retreiveDataDetailHistory(String.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0)));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(DetailSidebaruActivity.this, DetailsGridViewActivity.class);
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
                            Picasso picasso = new Picasso.Builder(DetailSidebaruActivity.this).downloader(new OkHttp3Downloader(picassoClient)).build();
                            picasso.setLoggingEnabled(true);
                            picasso.load(String.valueOf(response.body().getProfilePicture()))
                                    .placeholder(R.drawable.defaultslide)// Place holder image from drawable folder
                                    .error(R.drawable.defaultslide).resize(140, 140).rotate(0)
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

                            mGridAdapter = new GridViewDetailAdapter(DetailSidebaruActivity.this, R.layout.grid_item_layout, mGridData);
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

    @OnClick(R.id.fb_delete)
    public void deleteClick(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(DetailSidebaruActivity.this).create();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.hapus_sidebaru, null);
        final Button _fotoUlangButton = (Button) dialogView.findViewById(R.id.fotoUlangButton);
        final Button _kembaliButton = (Button) dialogView.findViewById(R.id.kembaliButton);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        _fotoUlangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umkmEndpoint.deleteUmkm("Bearer " + userdata.select().getAccesstoken(), String.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0))).enqueue(new Callback<SektorJson>() {
                    @Override
                    public void onResponse(Call<SektorJson> call, Response<SektorJson> response) {
                        if(response.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
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
        Intent intent = new Intent(DetailSidebaruActivity.this, UpdateSidebaru.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM,0));
        startActivity(intent);
    }

    @OnClick(R.id.fb_tambah_gambar)
    public void tambahGambarClick(){
//        Dialog choose = new android.app.AlertDialog.Builder(DetailSidebaruActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT)
//                .setTitle("Choose Upload Method")
//                .setNegativeButton("Cancel", null)
//                .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int position) {
//                        if (position == 0) {
//                            Intent intentprofile = new Intent(DetailSidebaruActivity.this, CameraActivity.class);
//                            intentprofile.putExtra(ParameterKey.PICTFROM,"CAMERA");
//                            intentprofile.putExtra(ParameterKey.ID_UMKM,Integer.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0)));
//                            startActivity(intentprofile);
//                        } else if (position == 1) {
//                            Intent intentprofile = new Intent(DetailSidebaruActivity.this, CameraActivity.class);
//                            intentprofile.putExtra(ParameterKey.ID_UMKM,Integer.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0)));
//                            intentprofile.putExtra(ParameterKey.PICTFROM,"GALLERY");
//                            startActivity(intentprofile);
//                        }
//                    }
//                }).create();
//        choose.show();

        Intent intent = new Intent(DetailSidebaruActivity.this, AddImageUmkmActivity.class);
        intent.putExtra(ParameterKey.ID_UMKM,Integer.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM,0)));
        startActivity(intent);
    }

    @OnClick(R.id.btn_image_bar_loc)
    public void lihatLokasi(){
        Intent i = new Intent(DetailSidebaruActivity.this, MapsDetailUmkmActivity.class);
        i.putExtra("LONGITUDE",longitude);
        i.putExtra("LATITUDE",latitude);
        i.putExtra("NAMA_UMKM",etNamaSidebar.getText().toString());
        i.putExtra(ParameterKey.SCREEN_UMKM, true);
        startActivity(i);
    }

    private void setToolbar(){

        String id = (getIntent().getExtras().getString(ParameterKey.REGNO) == null ? "" : getIntent().getExtras().getString(ParameterKey.REGNO));
        String fullname = (getIntent().getExtras().getString("NAMA_NASABAH") == null ? "": getIntent().getExtras().getString("NAMA_NASABAH"));
    }
    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

