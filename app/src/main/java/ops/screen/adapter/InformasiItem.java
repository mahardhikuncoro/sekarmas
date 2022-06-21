package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;


public class InformasiItem extends InformasiAdapter.ViewHolder {


    @BindView(R.id.txt_post_desc)
    TextView txtPostDesc;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.cv_post)
    CardView cvPost;

    @BindView(R.id.iv_post_informasi)
    ImageView ivPost;



    public InformasiItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
