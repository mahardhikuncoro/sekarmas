package user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import base.data.informationmodel.Information;
import base.network.callback.LoginJson;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.network.callback.NetworkConnection;
import base.network.callback.Slider;
import base.screen.BaseDialogActivity;
import base.screen.GridItem;
import base.screen.GridViewAdapter;
import base.service.URL;
import base.service.information.InformationEndpoint;
import base.service.kontak.KontakEndpoint;
import base.service.visimisi.VisiMisiEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.DataMenuModel;
import base.sqlite.model.InformasiModel;
import base.sqlite.model.SliderSQL;
import base.sqlite.model.Userdata;
import base.utils.ExpandableHeightGridView;
import base.widget.TextSliderViewCustom;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.MainActivityDashboard;
import ops.screen.adapter.GridViewAdapterMenu;
import ops.screen.fragment.TaskListFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import user.informasi.InformasiActivity;
import user.kontakdarurat.KontakDaruratActivity;
import user.laporan.LaporanActivity;
import user.pengaturan.PengaturanActivity;
import user.pengaturan.ProfileActivity;
import user.sidebaru.SidebaruActivity;
import user.visimisi.VisiMisiActivity;

public class DashboardActivity extends BaseDialogActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    @BindView(R.id.slider)
    SliderLayout sliderLayout;

    @BindView(R.id.txtViewName)
    TextView tvUsername;

    @BindView(R.id.banner_slider_indicator)
    PagerIndicator pagerIndicator;

    @BindView(R.id.gridView)
    ExpandableHeightGridView mGridView;

    @BindView(R.id.gridView_lain)
    ExpandableHeightGridView mGridViewLain;

    @BindView(R.id.imgprofile)
    ImageView imgprofile;

    private GridViewAdapterMenu mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private ArrayList<DataMenuModel> dataModels;

    private GridViewAdapterMenu mGridAdapterMenuLain;
    private ArrayList<GridItem> mGridDataMenuLain;
    private ArrayList<DataMenuModel> dataModelMenuLain;

    private ArrayList<Slider> sliderDataList;
    SliderSQL sliderdql;
    private ArrayList<String> sliderList;

    private SliderSQL slidersql;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        initiateApiData();
        initview();
        getInformation();

        menuGridview();
//        callApiSlider();

    }

    protected void getInformation(){

        sliderdql = new SliderSQL(this);
        sliderDataList = new ArrayList<Slider>();
        sliderList = new ArrayList<String>();
        sliderdql.deleteAll();

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
            informationEndpoint.getInformationList("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<List<Information>>() {
                @Override
                public void onResponse(Call<List<Information>> call, Response<List<Information>> response) {
                    try {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            for (int i = 0; i < response.body().size(); i++) {
                                InformasiModel newsmod = new InformasiModel();
                                newsmod.setNewsId(String.valueOf("" + i));
                                newsmod.setNewsTitle(response.body().get(i).getTitle());
                                newsmod.setNewsDesc(response.body().get(i).getDescription());
                                newsmod.setActive(response.body().get(i).getTitle());
                                newsmod.setImageUrl(URL.checkUrl() + response.body().get(i).getImageUrl());
                                newsmod.setCreateDate(response.body().get(i).getCreatedAt());
                                saveApiSlider(newsmod);
                            }
                            loadSlider();
                        } else {
                            dialog.dismiss();
                        }
                    }catch (Exception e){

                    }
                }

                @Override
                public void onFailure(Call<List<Information>> call, Throwable t) {

                }
            });
        }
    }

    private void saveApiSlider(InformasiModel sliderObject) {
//        for (int i=0;i<=5 ;i++) {

            Log.e("SLIDER SIZE INPUT " , " + "+ sliderObject.getImageUrl());
            Slider slider = new Slider();
            slider.setId(Long.parseLong(sliderObject.getNewsId()));
            slider.setName(sliderObject.getNewsTitle());
            slider.setImage( sliderObject.getImageUrl());
            slider.setLink(sliderObject.getImageUrl());
            slider.setPublish("Y");
            slider.setPackage_name("");

            sliderDataList.add(slider);
            sliderdql.save(slider);
//        }
    }


    private void callApiSlider() {

        sliderdql = new SliderSQL(this);
        sliderDataList = new ArrayList<Slider>();
        sliderList = new ArrayList<String>();
        sliderdql.deleteAll();
        for (int i=0;i<=5 ;i++) {

            Log.e("SLIDER SIZE INPUT " , " + "+ i);
            Slider slider = new Slider();
            slider.setId(Long.parseLong(String.valueOf(i)));
            slider.setName("slider" +i);
            slider.setImage( "https://www.trainingperbankan.com/wp-content/uploads/2018/05/logo-bank-kalbar.jpg");
            slider.setLink("");
            slider.setPublish("Y");
            slider.setPackage_name("");

            sliderDataList.add(slider);
            sliderdql.save(Long.parseLong(String.valueOf(i)), getResources().getString(R.string.companyName),"https://www.trainingperbankan.com/wp-content/uploads/2018/05/logo-bank-kalbar.jpg",
                    "", "Y", "");
        }
    }

    private void initview() {
        slidersql = new SliderSQL(this);

        mGridView.setExpanded(true);
        mGridViewLain.setExpanded(true);

        tvUsername.setText(userdata.select().getFullname());
        String img_url = userdata.select().getPhotoprofile();
        if (img_url!=null){
            Log.e("IMAGE URL"," : " + img_url);
            OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
            Picasso picasso = new Picasso.Builder(DashboardActivity.this).downloader(new OkHttp3Downloader(picassoClient)).build();
            picasso.setLoggingEnabled(true);
            picasso.load(URL.checkUrl()+img_url)
                    .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                    .error(R.drawable.ic_profile) .resize(200, 200).rotate(90)
                    .into(imgprofile, new com.squareup.picasso.Callback() {
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
    }

    @OnClick(R.id.imgprofile)
    public void clickProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void loadSlider() {

        Integer slidesize = slidersql.count();
        if (slidesize > 0) {
            sliderLayout.setBackgroundColor(getResources().getColor(R.color.white));

            for (int i = 1; i <= slidesize; i++) {
                Slider temp = slidersql.select(i);
                Log.e("SELECT "," IMAGE URL " + temp.getImage());
                TextSliderViewCustom textSliderView = new TextSliderViewCustom(this);
                textSliderView
                        .description(temp.getName())
                        .image(temp.getImage())
                        .error(R.drawable.defaultslide)
                        .empty(R.drawable.defaultslide)
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(DashboardActivity.this);

                textSliderView.bundle(new Bundle());

                textSliderView.getBundle()
                        .putString("extra", "https://www.youtube.com/watch?v=c-74OSumhNk");

                textSliderView.getBundle()
                        .putString("package", temp.getPackage_name());

                sliderLayout.addSlider(textSliderView);

            }

            sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(3000);
            sliderLayout.setCustomIndicator(pagerIndicator);
            sliderLayout.addOnPageChangeListener(DashboardActivity.this);

        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void menuGridview(){
        mGridData = new ArrayList<>();
        dataModels = new ArrayList<>();

        DataMenuModel dataModelLaporan = new DataMenuModel();
        dataModelLaporan.setUserid("0");
        dataModelLaporan.setSu_fullname("00");
        dataModelLaporan.setGroupid("00");
        dataModelLaporan.setSg_grpname("00");
        dataModelLaporan.setBranchid("00");
        dataModelLaporan.setBranchname("00");
        dataModelLaporan.setTypeid("00");
        dataModelLaporan.setMenuid("00");
        dataModelLaporan.setMenudesc("E-Pengaduan");
        dataModelLaporan.setAssigned("00");
        dataModelLaporan.setTrack("00");
        dataModelLaporan.setIs_add("00");
        dataModelLaporan.setJumlahaplikasi("00");
        dataModelLaporan.setMenutrack("00");
        dataModelLaporan.setIcon("00");
        dataModels.add(dataModelLaporan);

        DataMenuModel dataModelSidebaru = new DataMenuModel();
        dataModelSidebaru.setUserid("0");
        dataModelSidebaru.setSu_fullname("00");
        dataModelSidebaru.setGroupid("00");
        dataModelSidebaru.setSg_grpname("00");
        dataModelSidebaru.setBranchid("00");
        dataModelSidebaru.setBranchname("00");
        dataModelSidebaru.setTypeid("00");
        dataModelSidebaru.setMenuid("01");
        dataModelSidebaru.setMenudesc("E-UMKM");
        dataModelSidebaru.setAssigned("00");
        dataModelSidebaru.setTrack("00");
        dataModelSidebaru.setIs_add("00");
        dataModelSidebaru.setJumlahaplikasi("00");
        dataModelSidebaru.setMenutrack("00");
        dataModelSidebaru.setIcon("00");
        dataModels.add(dataModelSidebaru);

        DataMenuModel dataModelPariwisata = new DataMenuModel();
        dataModelPariwisata.setUserid("0");
        dataModelPariwisata.setSu_fullname("00");
        dataModelPariwisata.setGroupid("00");
        dataModelPariwisata.setSg_grpname("00");
        dataModelPariwisata.setBranchid("00");
        dataModelPariwisata.setBranchname("00");
        dataModelPariwisata.setTypeid("00");
        dataModelPariwisata.setMenuid("02");
        dataModelPariwisata.setMenudesc("E-Pariwisata");
        dataModelPariwisata.setAssigned("00");
        dataModelPariwisata.setTrack("00");
        dataModelPariwisata.setIs_add("00");
        dataModelPariwisata.setJumlahaplikasi("00");
        dataModelPariwisata.setMenutrack("00");
        dataModelPariwisata.setIcon("00");
        dataModels.add(dataModelPariwisata);

        DataMenuModel dataModelInformasi = new DataMenuModel();
        dataModelInformasi.setUserid("0");
        dataModelInformasi.setSu_fullname("00");
        dataModelInformasi.setGroupid("00");
        dataModelInformasi.setSg_grpname("00");
        dataModelInformasi.setBranchid("00");
        dataModelInformasi.setBranchname("00");
        dataModelInformasi.setTypeid("00");
        dataModelInformasi.setMenuid("03");
        dataModelInformasi.setMenudesc("Informasi");
        dataModelInformasi.setAssigned("00");
        dataModelInformasi.setTrack("00");
        dataModelInformasi.setIs_add("00");
        dataModelInformasi.setJumlahaplikasi("00");
        dataModelInformasi.setMenutrack("00");
        dataModelInformasi.setIcon("00");
        dataModels.add(dataModelInformasi);

        DataMenuModel dataModelKontakDarurat = new DataMenuModel();
        dataModelKontakDarurat.setUserid("0");
        dataModelKontakDarurat.setSu_fullname("00");
        dataModelKontakDarurat.setGroupid("00");
        dataModelKontakDarurat.setSg_grpname("00");
        dataModelKontakDarurat.setBranchid("00");
        dataModelKontakDarurat.setBranchname("00");
        dataModelKontakDarurat.setTypeid("00");
        dataModelKontakDarurat.setMenuid("04");
        dataModelKontakDarurat.setMenudesc("Kontak Darurat");
        dataModelKontakDarurat.setAssigned("00");
        dataModelKontakDarurat.setTrack("00");
        dataModelKontakDarurat.setIs_add("00");
        dataModelKontakDarurat.setJumlahaplikasi("00");
        dataModelKontakDarurat.setMenutrack("00");
        dataModelKontakDarurat.setIcon("00");
        dataModels.add(dataModelKontakDarurat);

        mGridAdapter = new GridViewAdapterMenu(this, R.layout.grid_item_layout, dataModels);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                DataMenuModel datamenu = (DataMenuModel) parent.getItemAtPosition(position);
                if(datamenu.getMenudesc().equals("E-Pengaduan")){
                    Intent intent = new Intent(DashboardActivity.this, LaporanActivity.class);
                    startActivity(intent);
                }else if(datamenu.getMenudesc().equals("E-UMKM")){
                    Intent intent = new Intent(DashboardActivity.this, SidebaruActivity.class);
                    startActivity(intent);
                }else if(datamenu.getMenudesc().equals("Informasi")){
                    Intent intent = new Intent(DashboardActivity.this, InformasiActivity.class);
                    startActivity(intent);
                }else if(datamenu.getMenudesc().equals("Visi & Misi")){
                    Intent intent = new Intent(DashboardActivity.this, VisiMisiActivity.class);
                    startActivity(intent);
                }else if(datamenu.getMenudesc().equals("Kontak Darurat")){
                    Intent intent = new Intent(DashboardActivity.this, KontakDaruratActivity.class);
                    startActivity(intent);
                }else{
                    dialogMessage("Fitur Masih dalam tahap pengembangan");
                }
            }
        });
        menuGridviewLain();
    }

    private void menuGridviewLain(){
        mGridDataMenuLain = new ArrayList<>();
        dataModelMenuLain = new ArrayList<>();

        DataMenuModel dataModelVisiMisi = new DataMenuModel();
        dataModelVisiMisi.setUserid("0");
        dataModelVisiMisi.setSu_fullname("00");
        dataModelVisiMisi.setGroupid("00");
        dataModelVisiMisi.setSg_grpname("00");
        dataModelVisiMisi.setBranchid("00");
        dataModelVisiMisi.setBranchname("00");
        dataModelVisiMisi.setTypeid("00");
        dataModelVisiMisi.setMenuid("00");
        dataModelVisiMisi.setMenudesc("Visi & Misi");
        dataModelVisiMisi.setAssigned("00");
        dataModelVisiMisi.setTrack("00");
        dataModelVisiMisi.setIs_add("00");
        dataModelVisiMisi.setJumlahaplikasi("00");
        dataModelVisiMisi.setMenutrack("00");
        dataModelVisiMisi.setIcon("00");
        dataModelMenuLain.add(dataModelVisiMisi);

        DataMenuModel dataModelPengaturan = new DataMenuModel();
        dataModelPengaturan.setUserid("0");
        dataModelPengaturan.setSu_fullname("00");
        dataModelPengaturan.setGroupid("00");
        dataModelPengaturan.setSg_grpname("00");
        dataModelPengaturan.setBranchid("00");
        dataModelPengaturan.setBranchname("00");
        dataModelPengaturan.setTypeid("00");
        dataModelPengaturan.setMenuid("01");
        dataModelPengaturan.setMenudesc("Pengaturan");
        dataModelPengaturan.setAssigned("00");
        dataModelPengaturan.setTrack("00");
        dataModelPengaturan.setIs_add("00");
        dataModelPengaturan.setJumlahaplikasi("00");
        dataModelPengaturan.setMenutrack("00");
        dataModelPengaturan.setIcon("00");
        dataModelMenuLain.add(dataModelPengaturan);


        mGridAdapterMenuLain = new GridViewAdapterMenu(this, R.layout.grid_item_layout, dataModelMenuLain);
        mGridViewLain.setAdapter(mGridAdapterMenuLain);

        //Grid view click event
        mGridViewLain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                DataMenuModel datamenu = (DataMenuModel) parent.getItemAtPosition(position);
                if(datamenu.getMenudesc().equals("Visi & Misi")){
                    Intent intent = new Intent(DashboardActivity.this, VisiMisiActivity.class);
                    startActivity(intent);
                }else if(datamenu.getMenudesc().equals("Pengaturan")){
                    Intent intent = new Intent(DashboardActivity.this, PengaturanActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
