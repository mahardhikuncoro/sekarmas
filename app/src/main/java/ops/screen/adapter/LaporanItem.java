package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;


public class LaporanItem extends LaporanAdapter.ViewHolder {

    @BindView(R.id.txt_post_name)
    TextView txtPostName;

    @BindView(R.id.txt_post_title)
    TextView txtPostTitle;

    @BindView(R.id.txt_position)
    TextView txtPosition;

    @BindView(R.id.txt_category_name)
    TextView txtCategory;

    @BindView(R.id.txt_post_desc)
    TextView txtPostDesc;

    @BindView(R.id.txt_comment)
    TextView txtComment;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.cv_post)
    CardView cvPost;

    @BindView(R.id.iv_post)
    ImageView ivPost;

    @BindView(R.id.iv_status)
    ImageView ivStatus;

    @BindView(R.id.img_profile)
    ImageView imgProfile;


    public LaporanItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
