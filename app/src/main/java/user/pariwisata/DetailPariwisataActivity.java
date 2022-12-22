package user.pariwisata;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import base.data.pariwisatamodel.KontentWisata;
import base.data.pariwisatamodel.PariwisataModel;
import base.data.pariwisatamodel.UlasanJson;
import base.data.sektormodel.SektorJson;
import base.data.sektormodel.SektorModel;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.screen.DetailsGridViewActivity;
import base.screen.GridItem;
import base.screen.GridViewDetailAdapter;
import base.utils.enm.ParameterKey;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.AddImagePariwisataActivity;
import ops.screen.MapsDetailUmkmActivity;
import ops.screen.adapter.KategoriAdapter;
import ops.screen.adapter.ProdukAdapter;
import ops.screen.adapter.UlasanAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.DashboardActivity;
import user.laporan.LaporanActivity;
import user.report.ReportActivity;

public class DetailPariwisataActivity extends BaseDialogActivity implements KategoriAdapter.OnKategoriListener{

    @BindView(R.id.btnback_toolbar)
    ImageView btnback_toolbar;
    @BindView(R.id.tv_nama_sidebaru)
    TextView etNamaSidebar;
    @BindView(R.id.etAlamatsidebaru)
    TextView etAlamatSidebar;
    @BindView(R.id.tvSenin)
    TextView tvSenin;
    @BindView(R.id.tvSelasa)
    TextView tvSelasa;
    @BindView(R.id.tvRabu)
    TextView tvRabu;
    @BindView(R.id.tvKamis)
    TextView tvKamis;
    @BindView(R.id.tvJumat)
    TextView tvJumat;
    @BindView(R.id.tvSabtu)
    TextView tvSabtu;
    @BindView(R.id.tvMinggu)
    TextView tvMinggu;
    @BindView(R.id.tvAvgRating)
    TextView tvAvgRating;
    @BindView(R.id.tvJumlahUlasan)
    TextView tvJumlahUlasan;
    @BindView(R.id.iv_profile_sidebaru)
    ImageView ivProfile;


    @BindView(R.id.post_progress_bar) ProgressBar progressBar;
    @BindView(R.id.fb_delete) FloatingActionButton fbDelete;
    @BindView(R.id.rv_kategori) RecyclerView rvKategori;
    @BindView(R.id.rvProduk) RecyclerView rvProduk;
    @BindView(R.id.rvUlasan) RecyclerView rvUlasan;
    @BindView(R.id.multiple_actions) FloatingActionsMenu  menuMultipleActions ;
    @BindView(R.id.gridView_detail) GridView mGridView;
    @BindView(R.id.ln_deskripsi_wisata) LinearLayout lnDeskripsiWisata;
    @BindView(R.id.ln_fasilitas) LinearLayout lnFasilitasWisata;
    @BindView(R.id.ln_fasilitas_gratis) LinearLayout lnFasilitasGratis;
    @BindView(R.id.ln_jam_operasional) LinearLayout lnJamOperasional;
    @BindView(R.id.lnProduk) LinearLayout lnProduk;
    @BindView(R.id.lnUlasan) LinearLayout lnUlasan;
    @BindView(R.id.linearout_gv) RelativeLayout linearoutGv;
    @BindView(R.id.iv_menu) ImageView ivMenu;

    protected Bundle bundle;
    private Integer idSektor=0;
    private String namaWisata ="", urlProfil;

    private GridViewDetailAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    protected Double longitude ;
    protected Double latitude ;
    List<String> permissionsRequired = new ArrayList<>();
    private KategoriAdapter kategoriAdapter;
    private UlasanAdapter ulasanAdapter;
    private Integer selectedId = 1;
    private List<SektorModel> kategoriList;
    private List<UlasanJson> ulasanList;
    private List<KontentWisata> deskripsiList;
    private List<KontentWisata> fasilitasList;
    private List<KontentWisata> fasilitasGratisList;
    private List<KontentWisata> produkList;
    private KontentWisata jam;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pariwisata_detail_activity);
        ButterKnife.bind(this);
        initiateApiData();
        getLastLocation();
        setToolbar();
        permissionsRequired.add(Manifest.permission.CAMERA);
        permissionsRequired.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsRequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ParameterKey.activityTemp = this;

        LinearLayoutManager linearLayoutKategori = new LinearLayoutManager(DetailPariwisataActivity.this, LinearLayoutManager.HORIZONTAL,false);
        rvKategori.setLayoutManager(linearLayoutKategori);
        rvKategori.setHasFixedSize(true);
        setKategoriList();

        LinearLayoutManager linearLayoutProduk = new LinearLayoutManager(DetailPariwisataActivity.this, LinearLayoutManager.VERTICAL,false);
        rvProduk.setLayoutManager(linearLayoutProduk);
        rvProduk.setHasFixedSize(true);


        LinearLayoutManager linearLayoutUlasan = new LinearLayoutManager(DetailPariwisataActivity.this, LinearLayoutManager.VERTICAL,false);
        rvUlasan.setLayoutManager(linearLayoutUlasan);
        rvUlasan.setHasFixedSize(true);

        //Initialize with empty data
        retreiveDetailPariwisata(getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
    }

    public boolean hasPermissions(List<String> permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setKategoriList(){
        kategoriList = new ArrayList<>();

        SektorModel sektor1 = new SektorModel();
        sektor1.setId(1);
        sektor1.setName("Ringkasan");

        SektorModel sektor2 = new SektorModel();
        sektor2.setId(2);
        sektor2.setName("Fasilitas");

        SektorModel sektor3 = new SektorModel();
        sektor3.setId(3);
        sektor3.setName("Operasional");

        SektorModel sektor4 = new SektorModel();
        sektor4.setId(4);
        sektor4.setName("Galeri");

        SektorModel sektor5 = new SektorModel();
        sektor5.setId(5);
        sektor5.setName("Ulasan");

        kategoriList.add(sektor1);
        kategoriList.add(sektor2);
        kategoriList.add(sektor3);
        kategoriList.add(sektor4);
        kategoriList.add(sektor5);

        setAdapterKategori();

    }

    private void setAdapterKategori() {
        selectedId = kategoriList.get(0).getId();
        kategoriAdapter = new KategoriAdapter(DetailPariwisataActivity.this, kategoriList, this, selectedId);
        rvKategori.setAdapter(kategoriAdapter);
    }

    private void setAdapterUlasan() {
        ulasanAdapter = new UlasanAdapter(ulasanList, DetailPariwisataActivity.this);
        rvUlasan.setAdapter(ulasanAdapter);
    }
    public void getDataUlasan(Integer idUmkm){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            pariwisataEndpoint.getUlasan("Bearer " + userdata.select().getAccesstoken(), idUmkm, "null").enqueue(new Callback<List<UlasanJson>>() {
                @Override
                public void onResponse(Call<List<UlasanJson>> call, Response<List<UlasanJson>> response) {
                    if(response.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        ulasanList = new ArrayList<>();
                        ulasanList.addAll(response.body());
                        double sumRating = 0;
                        for (UlasanJson ulasanJson : response.body()) {
                            sumRating = sumRating + ulasanJson.getRating();
                        }
                        try {
                            tvAvgRating.setText(BigDecimal.valueOf(sumRating / response.body().size()).setScale(1, RoundingMode.HALF_DOWN) + "");
                            tvJumlahUlasan.setText(response.body().size() + " Ulasan");

                        }catch(Exception e){}

                        setAdapterUlasan();
                    }
                }

                @Override
                public void onFailure(Call<List<UlasanJson>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    public void retreiveDetailPariwisata(Integer idUmkm){
        progressBar.setVisibility(View.VISIBLE);
        if (!networkConnection.isNetworkConnected()) {
            progressBar.setVisibility(View.INVISIBLE);
            dialog(R.string.errorNoInternetConnection);
        } else {
            pariwisataEndpoint.getDetailPariwisata("Bearer " + userdata.select().getAccesstoken(), idUmkm).enqueue(new Callback<PariwisataModel>() {
                @Override
                public void onResponse(Call<PariwisataModel> call, Response<PariwisataModel> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(response.isSuccessful()) {
                        etNamaSidebar.setText(response.body().getNama()==null?"":response.body().getNama());
                        namaWisata = response.body().getNama()==null?"":response.body().getNama();
                        etAlamatSidebar.setText(response.body().getAlamat()==null?"":response.body().getAlamat());
                        idSektor =response.body().getKategoriId()==null ? 0 : response.body().getKategoriId();
                        longitude= Double.valueOf(response.body().getLongitude());
                        latitude= Double.valueOf(response.body().getLatitude());

                        if (response.body().getProfilePicture() != null || !response.body().getProfilePicture().equals("")) {
                            urlProfil = response.body().getProfilePicture();
                            OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                            Picasso picasso = new Picasso.Builder(DetailPariwisataActivity.this).downloader(new OkHttp3Downloader(picassoClient)).build();
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
                                        public void onError() {Log.e("ERROR", " ");}
                                    });
                        }

                        deskripsiList = new ArrayList<>();
                        deskripsiList.addAll(response.body().getDeskripsiModel());
                        setDeskripsiWisata();

                        fasilitasList = new ArrayList<>();
                        fasilitasList.addAll(response.body().getFasilitas());
                        setFasilitas();

                        fasilitasGratisList = new ArrayList<>();
                        fasilitasGratisList.addAll(response.body().getFasilitasGratis());
                        setFasilitasGratis();

                        if(response.body().getJam() != null && response.body().getJam().size() > 0) {
                            jam = new KontentWisata();
                            jam.setSenin(response.body().getJam().get(0).getSenin());
                            jam.setSelasa(response.body().getJam().get(0).getSelasa());
                            jam.setRabu(response.body().getJam().get(0).getRabu());
                            jam.setKamis(response.body().getJam().get(0).getKamis());
                            jam.setJumat(response.body().getJam().get(0).getJumat());
                            jam.setSabtu(response.body().getJam().get(0).getSabtu());
                            jam.setMinggu(response.body().getJam().get(0).getMinggu());
                            setJamOperasional();
                        }

                        produkList = new ArrayList<>();
                        produkList.addAll(response.body().getProduk());
                        setProdukList();

                        mGridData = new ArrayList<>();
                        if(response.body().getGaleri()!=null) {
                            int i=0;
                            for (KontentWisata galeri : response.body().getGaleri()) {
                                GridItem gridItem = new GridItem();
                                gridItem.setImage(galeri.getUrl());
                                gridItem.setId(i);
                                gridItem.setDesc(galeri.getDeskripsi());
                                gridItem.setBitmapImage(null);
                                gridItem.setUri(null);
                                gridItem.setFileToUpload(null);
                                mGridData.add(gridItem);
                                i++;
                            }

                            mGridAdapter = new GridViewDetailAdapter(DetailPariwisataActivity.this, R.layout.grid_item_layout, mGridData);
                            mGridView.setAdapter(mGridAdapter);

                            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    //Get item at position
                                    GridItem item = (GridItem) parent.getItemAtPosition(position);

                                    Intent intent = new Intent(DetailPariwisataActivity.this, DetailsGridViewActivity.class);
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
                    }
                }

                @Override
                public void onFailure(Call<PariwisataModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("MASUK "," SINI " + t.toString());
                }
            });
        }
    }

    private void setDeskripsiWisata(){
        for(KontentWisata deskripsi : deskripsiList){

            TextView tvJudul = new TextView(this);
            tvJudul.setText(deskripsi.getJudul());
            tvJudul.setTextSize(16);
            tvJudul.setTypeface(Typeface.DEFAULT_BOLD);
            tvJudul.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

            TextView tvDeskripsi = new TextView(this);
            tvDeskripsi.setText(deskripsi.getDeskripsi());
            tvDeskripsi.setTextSize(14);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            llParams.setMargins(0, 8, 0, 16);
            tvDeskripsi.setLayoutParams(llParams);

            lnDeskripsiWisata.addView(tvJudul);
            lnDeskripsiWisata.addView(tvDeskripsi);
        }
//        lnDeskripsiWisata.setVisibility(deskripsiList.size() <= 0 ? View.GONE : View.VISIBLE);
    }

    private void setFasilitas(){
        for(KontentWisata deskripsi : fasilitasList){

            LinearLayout llFasilitas = new LinearLayout(this);
            llFasilitas.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            llFasilitas.setLayoutParams(llParams);

            TextView tvJudul = new TextView(this);
            tvJudul.setText(deskripsi.getNama());
            tvJudul.setTextSize(14);
            tvJudul.setBackground(getResources().getDrawable(R.drawable.shape_rounded_grey));
            LinearLayout.LayoutParams llJudul = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            tvJudul.setPadding(24, 24, 24, 24);
            tvJudul.setLayoutParams(llJudul);

            TextView tvDeskripsi = new TextView(this);
            tvDeskripsi.setText("Rp. " + deskripsi.getHarga()+"/"+deskripsi.getSatuan());
            tvDeskripsi.setTextSize(14);
            tvDeskripsi.setBackground(getResources().getDrawable(R.drawable.shape_rounded_grey));
            LinearLayout.LayoutParams llDeskripsi = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            tvDeskripsi.setPadding(24, 24, 24, 24);
            tvDeskripsi.setLayoutParams(llDeskripsi);

            llFasilitas.addView(tvJudul);
            llFasilitas.addView(tvDeskripsi);
            lnFasilitasWisata.addView(llFasilitas);
        }
//        lnFasilitasWisata.setVisibility(fasilitasList.size() <= 0 ? View.GONE : View.VISIBLE);

    }
    private void setFasilitasGratis(){
        for(KontentWisata deskripsi : fasilitasGratisList){

            LinearLayout llFasilitas = new LinearLayout(this);
            llFasilitas.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            llFasilitas.setLayoutParams(llParams);

            TextView tvDeskripsi = new TextView(this);
            tvDeskripsi.setText(deskripsi.getNama());
            tvDeskripsi.setTextSize(14);
            tvDeskripsi.setBackground(getResources().getDrawable(R.drawable.shape_rounded_grey));
            tvDeskripsi.setPadding(24, 24, 24, 24);
            LinearLayout.LayoutParams llDeskripsi = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            tvDeskripsi.setLayoutParams(llDeskripsi);

            llFasilitas.addView(tvDeskripsi);
            lnFasilitasGratis.addView(llFasilitas);
        }
//        lnFasilitasGratis.setVisibility(fasilitasGratisList.size() <= 0 ? View.GONE : View.VISIBLE);
    }

    private void setJamOperasional(){
        tvSenin.setText("Senin  :   " + jam.getSenin());
        tvSelasa.setText("Selasa    :   " + jam.getSelasa());
        tvRabu.setText("Rabu    :  " + jam.getRabu());
        tvKamis.setText("Kamis  :   " + jam.getKamis());
        tvJumat.setText("Jumat  :    " + jam.getJumat());
        tvSabtu.setText("Sabtu  :   " + jam.getSabtu());
        tvMinggu.setText("Minggu   :   " + jam.getMinggu());
    }

    private void setProdukList(){
        ProdukAdapter produkAdapter = new ProdukAdapter(produkList, this );
        rvProduk.setAdapter(produkAdapter);
    }

    @OnClick(R.id.fb_desc)
    public void addDesc(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddDeskripsiActivity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        startActivity(intent);
    }

    @OnClick(R.id.iv_menu)
    public void clickMenu(){
        {
            final AlertDialog dialogBuilder = new AlertDialog.Builder(DetailPariwisataActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.popup_ugc, null);
            final LinearLayout lnReport = (LinearLayout) dialogView.findViewById(R.id.lnReport);
            final LinearLayout lnHide = (LinearLayout) dialogView.findViewById(R.id.lnHide);
            final LinearLayout lnBlockandReport = (LinearLayout) dialogView.findViewById(R.id.lnBlockandReport);
            lnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailPariwisataActivity.this, ReportActivity.class);
                    intent.putExtra(ParameterKey.ID_UMKM, getIntent().getIntExtra(ParameterKey.ID_UMKM,0));
                    intent.putExtra("type", "pariwisata");
                    intent.putExtra("isreport", true);
                    startActivity(intent);
                    dialogBuilder.dismiss();
                }
            });
            lnHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailPariwisataActivity.this, ReportActivity.class);
                    intent.putExtra(ParameterKey.ID_UMKM, getIntent().getIntExtra(ParameterKey.ID_UMKM,0));
                    intent.putExtra("type", "pariwisata");
                    intent.putExtra("ishide", true);
                    startActivity(intent);
                    dialogBuilder.dismiss();
                }
            });
            lnBlockandReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailPariwisataActivity.this, ReportActivity.class);
                    intent.putExtra(ParameterKey.ID_UMKM, getIntent().getIntExtra(ParameterKey.ID_UMKM,0));
                    intent.putExtra("type", "pariwisata");
                    intent.putExtra("isreport", true);
                    intent.putExtra("ishide", true);
                    startActivity(intent);
                    dialogBuilder.dismiss();
                }
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        }
    }
    @OnClick(R.id.btnAddUlasan)
    public void addUlasan(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddUlasanActivity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        intent.putExtra(ParameterKey.NAMA_UMKM, namaWisata);
        intent.putExtra(ParameterKey.PICTFROM,urlProfil);
        startActivity(intent);
    }

    @OnClick(R.id.fb_delete)
    public void deleteClick(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(DetailPariwisataActivity.this).create();
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

    @OnClick(R.id.fb_add_fasilitas)
    public void clickAddFasilitas(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddFasilitasActity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        startActivity(intent);
    }

    @OnClick(R.id.fb_add_fasilitas_gratis)
    public void clickAddFasilitasGratis(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddFasilitasGratisActity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        startActivity(intent);
    }

    @OnClick(R.id.fb_add_produk)
    public void clickAddProduk(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddProdukActity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        startActivity(intent);
    }
    @OnClick(R.id.fb_add_operasional)
    public void clickAddOperasional(){
        Intent intent = new Intent(DetailPariwisataActivity.this, AddOperasionalActity.class);
        intent.putExtra(ParameterKey.ID_UMKM,getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
        startActivity(intent);
    }

    @OnClick(R.id.fb_tambah_gambar)
    public void tambahGambarClick(){
        if (!hasPermissions(permissionsRequired)) {
            String[] permissionArray = new String[permissionsRequired.size()];
            for (int i = 0; i < permissionsRequired.size(); i++) {
                permissionArray[i] = permissionsRequired.get(i);
            }
            ActivityCompat.requestPermissions(this, permissionArray, 0);
        }else {
            Dialog choose = new android.app.AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Choose Upload Method")
                    .setNegativeButton("Cancel", null)
                    .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                Intent intentprofile = new Intent(DetailPariwisataActivity.this, AddImagePariwisataActivity.class);
                                intentprofile.putExtra(ParameterKey.PICTFROM, "CAMERA");
                                intentprofile.putExtra(ParameterKey.ID_UMKM, Integer.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM, 0)));
                                startActivity(intentprofile);
                                finish();
                            } else if (position == 1) {
                                Intent intentprofile = new Intent(DetailPariwisataActivity.this, AddImagePariwisataActivity.class);
                                intentprofile.putExtra(ParameterKey.ID_UMKM, Integer.valueOf(getIntent().getIntExtra(ParameterKey.ID_UMKM, 0)));
                                intentprofile.putExtra(ParameterKey.PICTFROM, "GALLERY");
                                startActivity(intentprofile);
                                finish();
                            }
                        }
                    }).create();
            choose.show();
        }
    }

    @OnClick(R.id.btn_image_bar_loc)
    public void lihatLokasi(){
        Intent i = new Intent(DetailPariwisataActivity.this, MapsDetailUmkmActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasPermissions(permissionsRequired)) {
            String[] permissionArray = new String[permissionsRequired.size()];
            for (int i = 0; i < permissionsRequired.size(); i++) {
                permissionArray[i] = permissionsRequired.get(i);
            }
            ActivityCompat.requestPermissions(this, permissionArray, 0);
        }
    }

    @Override
    public void onItemSelected(int position) {
        selectedId = kategoriList.get(position).getId();
        kategoriAdapter = new KategoriAdapter(DetailPariwisataActivity.this, kategoriList, this, kategoriList.get(position).getId());
        rvKategori.setAdapter(kategoriAdapter);
        rvKategori.scrollToPosition(position);
        if(selectedId == 1){
            lnDeskripsiWisata.setVisibility(View.VISIBLE);
            lnFasilitasGratis.setVisibility(View.GONE);
            lnFasilitasWisata.setVisibility(View.GONE);
            lnJamOperasional.setVisibility(View.GONE);
            lnProduk.setVisibility(View.GONE);
            linearoutGv.setVisibility(View.GONE);
            lnUlasan.setVisibility(View.GONE);
        }else if(selectedId == 2){
            lnDeskripsiWisata.setVisibility(View.GONE);
            lnFasilitasGratis.setVisibility(View.VISIBLE);
            lnFasilitasWisata.setVisibility(View.VISIBLE);
            lnJamOperasional.setVisibility(View.GONE);
            lnProduk.setVisibility(View.GONE);
            linearoutGv.setVisibility(View.GONE);
            lnUlasan.setVisibility(View.GONE);
        }else if(selectedId == 3){
            lnDeskripsiWisata.setVisibility(View.GONE);
            lnFasilitasGratis.setVisibility(View.GONE);
            lnFasilitasWisata.setVisibility(View.GONE);
            lnJamOperasional.setVisibility(View.VISIBLE);
            lnProduk.setVisibility(View.VISIBLE);
            linearoutGv.setVisibility(View.GONE);
            lnUlasan.setVisibility(View.GONE);
        }else if(selectedId == 4){
            lnDeskripsiWisata.setVisibility(View.GONE);
            lnFasilitasGratis.setVisibility(View.GONE);
            lnFasilitasWisata.setVisibility(View.GONE);
            lnJamOperasional.setVisibility(View.GONE);
            lnProduk.setVisibility(View.GONE);
            linearoutGv.setVisibility(View.VISIBLE);
            lnUlasan.setVisibility(View.GONE);
        }else if(selectedId == 5){
            getDataUlasan(getIntent().getIntExtra(ParameterKey.ID_UMKM, 0));
            lnDeskripsiWisata.setVisibility(View.GONE);
            lnFasilitasGratis.setVisibility(View.GONE);
            lnFasilitasWisata.setVisibility(View.GONE);
            lnJamOperasional.setVisibility(View.GONE);
            lnProduk.setVisibility(View.GONE);
            linearoutGv.setVisibility(View.GONE);
            lnUlasan.setVisibility(View.VISIBLE);
        }
    }
}

