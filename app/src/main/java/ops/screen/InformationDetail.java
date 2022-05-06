package ops.screen;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import base.data.commentmodel.CommentJson;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.screen.BaseDialogActivity;
import base.service.URL;
import base.service.comment.CommentEndpoint;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.adapter.CommentAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class InformationDetail extends BaseDialogActivity {


    @BindView(R.id.tv_content_cv)
    TextView tvContent;

    @BindView(R.id.tv_title_cv)
    TextView tvTitle;

    @BindView(R.id.img_post_detail)
    ImageView ivPost;

    @BindView(R.id.progress_bar)
    ProgressBar progressBarImage;


    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMAGE = "image";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_detail);
        ButterKnife.bind(this);
        initiateApiData();

        tvTitle.setText(getIntent().getStringExtra(KEY_MESSAGE));
        tvContent.setText(getIntent().getStringExtra(KEY_DESC));

        String url = URL.checkUrl()+getIntent().getStringExtra(KEY_IMAGE);
        Log.e("HAHAHAHHA "," clicked news : " +  url);
        OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
        Picasso picasso = new Picasso.Builder(InformationDetail.this).downloader(new OkHttp3Downloader(picassoClient)).build();
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
