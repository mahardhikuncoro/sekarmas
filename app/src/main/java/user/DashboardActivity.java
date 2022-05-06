package user;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;

import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.network.callback.Slider;
import base.screen.BaseDialogActivity;
import base.screen.GridItem;
import base.screen.GridViewAdapter;
import base.service.information.InformationEndpoint;
import base.service.kontak.KontakEndpoint;
import base.service.visimisi.VisiMisiEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.DataMenuModel;
import base.sqlite.model.SliderSQL;
import base.sqlite.model.Userdata;
import base.utils.ExpandableHeightGridView;
import base.widget.TextSliderViewCustom;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarmas.mobile.application.R;
import ops.screen.adapter.GridViewAdapterMenu;
import ops.screen.fragment.TaskListFragment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends BaseDialogActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    @BindView(R.id.slider)
    SliderLayout sliderLayout;

    @BindView(R.id.banner_slider_indicator)
    PagerIndicator pagerIndicator;

    @BindView(R.id.gridView)
    ExpandableHeightGridView mGridView;

    private GridViewAdapterMenu mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private ArrayList<DataMenuModel> dataModels;

    private SliderSQL slidersql;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        init();
        loadSlider();
        menuGridview();
    }

    private void init() {
        userdata = new Userdata(this);
        config = new Config(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();

        networkConnection = new NetworkConnection(this);
        slidersql = new SliderSQL(this);

        mGridView.setExpanded(true);

    }

    private void loadSlider() {

        Integer slidesize = slidersql.count();
        if (slidesize > 0) {
            sliderLayout.setBackgroundColor(getResources().getColor(R.color.white));

            for (int i = 1; i <= slidesize; i++) {
                Slider temp = slidersql.select(i);

                TextSliderViewCustom textSliderView = new TextSliderViewCustom(this);
                textSliderView
                        .description(temp.getName())
                        .image(temp.getImage())
                        .error(R.drawable.defaultslide)
                        .empty(R.drawable.defaultslide)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
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
            sliderLayout.setDuration(6000);
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
        for(int i=0;i<=10;i++)
        {
            DataMenuModel dataModel = new DataMenuModel();
            dataModel.setUserid(""+i);
            dataModel.setSu_fullname(""+i);
            dataModel.setGroupid(""+i);
            dataModel.setSg_grpname(""+i);
            dataModel.setBranchid(""+i);
            dataModel.setBranchname(""+i);
            dataModel.setTypeid(""+i);
            dataModel.setMenuid(""+i);
            dataModel.setMenudesc(""+i);
            dataModel.setAssigned(""+i);
            dataModel.setTrack(""+i);
            dataModel.setIs_add(""+i);
            dataModel.setJumlahaplikasi(""+i);
            dataModel.setMenutrack(""+i);
            dataModel.setIcon(""+i);
            dataModels.add(dataModel);
        }

        for(int i=0;i<8;i++) {
            GridItem gridItem = new GridItem();
            gridItem.setId(i);
            gridItem.setDesc("MENU");
            mGridData.add(gridItem);
        }
        mGridAdapter = new GridViewAdapterMenu(this, R.layout.grid_item_layout, dataModels);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                DataMenuModel datamenu = (DataMenuModel) parent.getItemAtPosition(position);
            }
        });
    }
}
