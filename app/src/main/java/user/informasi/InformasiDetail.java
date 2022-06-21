package user.informasi;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.service.URL;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;

public class InformasiDetail extends BaseDialogActivity {


    @BindView(R.id.tv_content_cv)
    TextView tvContent;

    @BindView(R.id.tv_title_cv)
    TextView tvTitle;

    @BindView(R.id.tv_content_date)
    TextView tvDate;

    @BindView(R.id.img_post_detail)
    ImageView ivPost;

    @BindView(R.id.progress_bar)
    ProgressBar progressBarImage;


    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "created_date";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_detail);
        ButterKnife.bind(this);
        initiateApiData();

        tvTitle.setText(getIntent().getStringExtra(KEY_MESSAGE));
        tvContent.setText(getIntent().getStringExtra(KEY_DESC));
        tvDate.setText(getIntent().getStringExtra(KEY_DATE));

        String url = URL.checkUrl()+getIntent().getStringExtra(KEY_IMAGE);
        OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
        Picasso picasso = new Picasso.Builder(InformasiDetail.this).downloader(new OkHttp3Downloader(picassoClient)).build();
        picasso.setLoggingEnabled(true);
        picasso.load(url)
                .placeholder(R.drawable.defaultslide)// Place holder image from drawable folder
                .error(R.drawable.defaultslide)
                .fit().centerCrop()
                .into(ivPost, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("SUKSES", " ");
                        progressBarImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Log.e("ERROR", " ");
                        progressBarImage.setVisibility(View.GONE);
                    }
                });

    }

    @OnClick(R.id.btnback_toolbar)
    public void clickBack(){
        finish();
    }

}
