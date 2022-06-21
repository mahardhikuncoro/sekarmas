package user.laporan;

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
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.adapter.CommentAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LaporanDetail extends BaseDialogActivity {

    @BindView(R.id.txt_post_name)
    TextView txtPostName;
    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.txt_position)
    TextView txtPosition;
    @BindView(R.id.txt_post_time)
    TextView txtPostTime;

    @BindView(R.id.lc_comment_detail)
    RecyclerView rvComment;
    @BindView(R.id.tv_content_cv)
    TextView tvContent;
    @BindView(R.id.et_comment_detail)
    EditText etComment;
    @BindView(R.id.img_post_detail)
    ImageView ivPost;
    @BindView(R.id.post_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBarImage;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;

    LinearLayout linearLayout;

    String idLaporan;
    CommentEndpoint commentEndpoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laporan_detail);
        ButterKnife.bind(this);

        initiateApiData();
        idLaporan = getIntent().getStringExtra("id_laporan");
//        commentEndpoint = CommentUtils.getCommentUrl();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();
        commentEndpoint = retrofit.create(CommentEndpoint.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LaporanDetail.this, LinearLayoutManager.VERTICAL,false);
        rvComment.setLayoutManager(linearLayoutManager);
        getDetailPost();
    }
    private  void getDetailPost(){
        progressBar.setVisibility(View.VISIBLE);
        commentEndpoint.getDetailComment("Bearer " + userdata.select().getAccesstoken(), idLaporan).enqueue(new Callback<CommentJson>() {
            @Override
            public void onResponse(Call<CommentJson> call, Response<CommentJson> response) {
                if(response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    tvContent.setText(response.body().getDescription());
                    String url = URL.checkUrl()+response.body().getImageUrl();
                    Transformation transformation = new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int targetWidth = ivPost.getWidth();

                            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                            int targetHeight = (int) (targetWidth * aspectRatio);
                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                            if (result != source) {
                                // Same bitmap is returned if sizes are the same
                                source.recycle();
                            }
                            return result;
                        }

                        @Override
                        public String key() {
                            return "transformation" + " desiredWidth";
                        }
                    };
                    OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                    Picasso picasso = new Picasso.Builder(LaporanDetail.this).downloader(new OkHttp3Downloader(picassoClient)).build();
                    picasso.setLoggingEnabled(true);
                    picasso.load(url)
                            .placeholder(R.drawable.defaultslide)// Place holder image from drawable folder
                            .error(R.drawable.defaultslide).transform(transformation)
                            .into(ivPost, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBarImage.setVisibility(View.GONE);
                                    Log.e("SUKSES", " ");
                                }

                                @Override
                                public void onError() {
                                    progressBarImage.setVisibility(View.GONE);
                                    Log.e("ERROR", " ");

                                }
                            });

                    String img_url = userdata.select().getPhotoprofile();
                    if(img_url!= null) {
                        picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                        Picasso picassoPropict = new Picasso.Builder(LaporanDetail.this).downloader(new OkHttp3Downloader(picassoClient)).build();
                        picassoPropict.setLoggingEnabled(true);
                        picassoPropict.load(base.service.URL.checkUrl()+img_url)
                                .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                                .error(R.drawable.ic_profile).resize(200, 200)
                                .into( imgProfile, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }

                    tvCommentCount.setText((response.body().getComments().size() + " Comment"));
                    txtPostName.setText(response.body().getUser().getFullname());
                    txtPosition.setText(response.body().getKabupatenKota());
                    txtPostTime.setText(response.body().getCreatedAt());
                    if(response.body().getStatus()==1)
                        ivStatus.setImageResource(R.drawable.ic_reject);
                    else if(response.body().getStatus()==2)
                        ivStatus.setImageResource(R.drawable.ic_pending);
                    else
                        ivStatus.setImageResource(R.drawable.ic_done_status);
                    CommentAdapter commentAdapter = new CommentAdapter(LaporanDetail.this, response.body().getComments());
                    rvComment.setAdapter(commentAdapter);
                }
            }

            @Override
            public void onFailure(Call<CommentJson> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btn_send_comment_detail)
    public void sendComment(){
        if(etComment.length()>0) {
            hideKeyboard();
            commentEndpoint.postComment("Bearer " + userdata.select().getAccesstoken(), idLaporan, etComment.getText().toString()).enqueue(new Callback<CommentJson>() {
                @Override
                public void onResponse(Call<CommentJson> call, Response<CommentJson> response) {
                    if (response.isSuccessful()){
                        etComment.setText("");
                        getDetailPost();
                    }
                }
                @Override
                public void onFailure(Call<CommentJson> call, Throwable t) {

                }
            });
        }
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
