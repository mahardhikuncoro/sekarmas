package ops.screen.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.sembozdemir.viewpagerarrowindicator.library.ViewPagerArrowIndicator;

import java.util.ArrayList;
import java.util.List;

import base.data.informationmodel.Information;
import base.data.visimisimodel.Misi;
import base.data.visimisimodel.Visi;
import base.data.visimisimodel.VisiMisi;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.network.callback.Slider;
import base.service.information.InformationEndpoint;
import base.service.kontak.Kontak;
import base.service.kontak.KontakEndpoint;
import base.service.visimisi.VisiMisiEndpoint;
import base.sqlite.model.DataMenuModel;
import base.sqlite.model.FormData;
import base.sqlite.model.NewsModel;
import base.sqlite.model.Config;
import base.sqlite.model.SliderSQL;
import base.sqlite.model.TaskListDetailModel;
import base.sqlite.model.Userdata;
//import base.widget.TextSliderViewCustom;
import base.widget.TextSliderViewCustom;
import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import ops.screen.adapter.KontakAdapter;
import ops.screen.adapter.TaskListMisiAdapter;
import ops.screen.TaskListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import user.login.LoginActivity;

public class TaskListFragment extends Fragment implements TaskListInterface, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {


    @BindView(R.id.taskListRecycleAll)
    RecyclerView taskListRecycleAll;

    @BindView(R.id.taskListRecycleMisi)
    RecyclerView taskListRecycleMisi;

//    @BindView(R.id.swiperefresh)
//    SwipeRefreshLayout _swiperefresh;

    @BindView(R.id.linearTitleSwipe)
    LinearLayout _linearTitleSwipe;

    @BindView(R.id.layoutSlider)
    LinearLayout _layoutSlider;

//    SliderLayout sliderLayout = (SliderLayout) rootView.findViewById(R.id.banner_slider);
//    PagerIndicator pagerIndicator = (PagerIndicator) rootView.findViewById(R.id.banner_slider_indicator);
//    DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());

    @BindView(R.id.fb_call)
    FloatingActionButton fbKontak;

    @BindView(R.id.slider)
    SliderLayout sliderLayout;

    @BindView(R.id.banner_slider_indicator)
    PagerIndicator pagerIndicator;

    @BindView(R.id.viewPagerNews)
    ViewPager viewPager ;

    @BindView(R.id.viewPagerArrowIndicator)
    ViewPagerArrowIndicator viewPagerArrowIndicator;


    private Config config;
    private ArrayList<Visi> taskListList;
    private ArrayList<Misi> taskListListMisi;
    private TaskListAdapter taskListAdapter;
    private TaskListMisiAdapter taskListAdapterMisi;
    private KontakEndpoint kontakEndpoint;
    private InformationEndpoint informationEndpoint;
    private VisiMisiEndpoint visiMisiEndpoint;
    private NetworkConnection networkConnection;
    public Dialog dialogmaterial;
    private Userdata userdata;
    private ArrayList<String> menulist;
    private ArrayList<NewsModel> news;
    private ArrayList<DataMenuModel> dataModels;
    private String assignedType ="allassigned", tc = "5.0";
    private Dialog dialog;
    private Boolean isNew ;
    private FormData formData;
    private SliderSQL slidersql;
    private List<LinearLayout> linearLayouts = new ArrayList<>();
    private Integer newsPosition=0;

    private FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.tasklist_fragment, container, false);
        ButterKnife.bind(this, view);
        initialisation();
        onAttach(getActivity());
        getInformation();
//        getMenuAccess();
//        showMenu();
//        searchList();
        loadSlider();
        fbKontak.setAlpha(0.5f);;
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View addView = getActivity().getLayoutInflater().inflate(R.layout.about_bexi, null);
                new AlertDialog.Builder(getActivity().getApplicationContext()).setTitle(R.string.app_name).setView(addView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).setNegativeButton("", null).show();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext =(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadSlider() {

        Integer slidesize = slidersql.count();
//        Integer slidesize = 0;
        if (slidesize > 0) {
            sliderLayout.setBackgroundColor(getResources().getColor(R.color.white));

            for (int i = 1; i <= slidesize; i++) {
                Slider temp = slidersql.select(i);

                TextSliderViewCustom textSliderView = new TextSliderViewCustom(getActivity());
                textSliderView
                        .description(temp.getName())
                        .image(temp.getImage())
                        .error(R.drawable.defaultslide)
                        .empty(R.drawable.defaultslide)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(TaskListFragment.this);

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
            sliderLayout.setDuration(6000);
            sliderLayout.setCustomIndicator(pagerIndicator);
            sliderLayout.addOnPageChangeListener(TaskListFragment.this);

        }
    }

    private void initialisation() {

        userdata = new Userdata(getActivity().getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        taskListRecycleAll.setLayoutManager(linearLayoutManager);
        taskListRecycleAll.setHasFixedSize(true);
        taskListRecycleAll.smoothScrollToPosition(10);

        LinearLayoutManager linearLayoutManagermisi = new LinearLayoutManager(getActivity());
        linearLayoutManagermisi.setOrientation(LinearLayoutManager.VERTICAL);

        taskListRecycleMisi.setLayoutManager(linearLayoutManagermisi);
        taskListRecycleMisi.setHasFixedSize(true);
        taskListRecycleMisi.smoothScrollToPosition(10);

        config = new Config(getActivity().getApplicationContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();
        menulist = new ArrayList<>();
        kontakEndpoint = retrofit.create(KontakEndpoint.class);

        networkConnection = new NetworkConnection(getActivity());
        slidersql = new SliderSQL(getActivity().getApplicationContext());

//        informationEndpoint = InformationUtils.getInformation();
//        visiMisiEndpoint = VisiMisiUtils.getVisiMisi();

        informationEndpoint =retrofit.create(InformationEndpoint.class);;
        visiMisiEndpoint = retrofit.create(VisiMisiEndpoint.class);
        ((View)viewPager.getParent()).requestLayout();
    }



    protected void dialog(int rString) {
        new MaterialDialog.Builder(getActivity())
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
        new MaterialDialog.Builder(getActivity())
                .content(rString)
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
    private void showMenu() {
//        fillList(assignedType);
        showNews(news);
    }


    @OnClick(R.id.fb_call)
    public void showKontak(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.kontak_popup, null);
        final RecyclerView rvKontak = (RecyclerView)dialogView.findViewById(R.id.rv_kotak);
        final ProgressBar pbKontak = (ProgressBar)dialogView.findViewById(R.id.pb_kontak);
        final ImageView ivCLose = (ImageView) dialogView.findViewById(R.id.iv_close);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        kontakEndpoint.getKontak("Bearer " + userdata.select().getAccesstoken()).enqueue(new Callback<List<Kontak>>() {
            @Override
            public void onResponse(Call<List<Kontak>> call, Response<List<Kontak>> response) {
                if(response.isSuccessful()){
                    pbKontak.setVisibility(View.GONE);
                    ArrayList<Kontak> listKontak = new ArrayList<>();
                    for (Kontak kontakModel : response.body()) {
                        Kontak kontak = new Kontak();
                        kontak.setName(kontakModel.getName());
                        kontak.setPhone(kontakModel.getPhone());
                        listKontak.add(kontak);
                    }
                    KontakAdapter kontakAdapter = new KontakAdapter(getActivity().getApplicationContext(),listKontak);
                    rvKontak.setLayoutManager(linearLayoutManager);
                    rvKontak.setAdapter(kontakAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Kontak>> call, Throwable t) {

            }
        });

        ivCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }


 /*   private void searchList(){
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if(taskListAdapter == null){
            Log.e("TASKLIST ADAPTER " ,"ISNULL");
        }

        _etSearch.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        _etSearch.setMaxWidth(Integer.MAX_VALUE);
        _etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskListAdapter = new TaskListAdapter(getActivity().getApplicationContext(), taskListList, assignedType, new TaskListInterface() {
                    @Override
                    public void onListSelected(TaskListDetailModel list) {

                    }
                });
                taskListAdapter.getFilter().filter(query);
                Log.e("TASKLIST ADAPTER " ,"AFTER FILTER " + taskListAdapter.getItemCount());
                taskListAdapter.notifyDataSetChanged();
                taskListRecycleAll.setAdapter(taskListAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                taskListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }*/

    @Override
    public void onListSelected(TaskListDetailModel list) {

    }
    private void removeUserData(String message){
        Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        formData.deleteAll("FORM_SURVEY");
        formData.deleteAll("FORM_SURVEY_DATA");
        formData.deleteAll("FORM_SURVEY_REFERENCE");
        formData.deleteAll();
        userdata.deleteAll();
        getActivity().finish();
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

     /*   String link = slider.getBundle().getString("extra");
        String package_name = slider.getBundle().getString("package");

        if (package_name.length() > 1) {
            if (isPackageExisted(package_name)) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(package_name);
                startActivity(launchIntent);//null pointer check in case package name was not found
            } else {
                popUpSlider();
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slider.getBundle().get("extra") + ""));
//                startActivity(browserIntent);
            }
        } else if (!link.equalsIgnoreCase("") && link.length() > 10) {
            popUpSlider();
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slider.getBundle().get("extra") + ""));
//            startActivity(browserIntent);
        }*/

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

    public boolean isPackageExisted(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getActivity().getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    private void showNews(ArrayList<NewsModel> news) {

        FragmentManager fragManager = myContext.getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(fragManager, news, getActivity().getApplicationContext()));
        viewPagerArrowIndicator.bind(viewPager);

        viewPagerArrowIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("HAHAHAHHA "," click : " + newsPosition);

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                Log.e("HAHAHAHHA "," onPageScrolled : " + i);
                newsPosition = i;
            }

            @Override
            public void onPageSelected(int i) {
                Log.e("HAHAHAHHA "," onPageSelected : " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.e("HAHAHAHHA "," onPageScrollStateChanged : " );
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e("HAHAHAHHA "," onPageSelected : " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        getVisiDanMisi();
    }

    private void popUpSlider(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity().getApplicationContext()).create();
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.popup_slider, null);

        final WebView mWebView = (WebView)getView().findViewById(R.id.activity_main_webview);
        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("https://www.youtube.com/watch?v=c-74OSumhNk");
    }

//        @OnClick(R.id.lnr_news)
//        public void clickNews(){
////            showInformation("","","");
//            Log.e("HAHAHAHHA "," clicked news : " + newsPosition);
//        }

    private void showInformation(String url, final String doc_code, final String doc_id){
        String urldetail = url.replace("_thumbnails", "");
        final AlertDialog dialogBuilder = new AlertDialog.Builder(myContext).create();
        LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.detail_news, null);
//        final ScrollView _scrool = (ScrollView) dialogView.findViewById(R.id.scroolImage);
        final Button _kembaliButton = (Button) dialogView.findViewById(R.id.kembaliButton);
        final LinearLayout rlNews = (LinearLayout) dialogView.findViewById(R.id.rl_card_news);
        final ImageView close = (ImageView) dialogView.findViewById(R.id.close);
//        final TextView textAll =(TextView)dialogView.findViewById(R.id.txt_news);

        List<TextView> textInputEditTextCopies = new ArrayList<>();
        for (NewsModel newsModel : news) {
            TextView textInputEditText = new TextInputEditText(myContext);
            textInputEditText.setEnabled(false);
            textInputEditText.setTag("news_"+newsModel.getNewsId());
            textInputEditText.setText(newsModel.getNewsDesc());
            rlNews.addView(textInputEditText);
            textInputEditTextCopies.add(textInputEditText);
        }
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.cancel();
            }
        });
    }

    protected void getInformation(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    if(response.isSuccessful()){
                        dialog.dismiss();
                        news = new ArrayList<>();
                        for(int i = 0; i<response.body().size() ;i++){
                            NewsModel newsmod = new NewsModel();
                            newsmod.setNewsId(response.body().get(i).getId());
                            newsmod.setNewsTitle(response.body().get(i).getTitle());
                            newsmod.setNewsDesc(response.body().get(i).getDescription());
                            newsmod.setActive(response.body().get(i).getTitle());
                            newsmod.setImageUrl(response.body().get(i).getImageUrl());
                            news.add(newsmod);
                        }
                        showNews(news);
                    }else {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<List<Information>> call, Throwable t) {

                }
            });
        }
    }

    private void getVisiDanMisi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        taskListAdapter = new TaskListAdapter(getActivity().getApplicationContext(), taskListList);
        taskListAdapter.notifyDataSetChanged();
        taskListRecycleAll.setAdapter(taskListAdapter);

        taskListAdapterMisi = new TaskListMisiAdapter(getActivity().getApplicationContext(), taskListListMisi);
        taskListAdapter.notifyDataSetChanged();
        taskListRecycleMisi.setAdapter(taskListAdapter);
    }
}



