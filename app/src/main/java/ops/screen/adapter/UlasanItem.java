package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;


public class UlasanItem extends UlasanAdapter.ViewHolder {

    @BindView(R.id.tvNama)
    TextView tvNama;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.rentalLinear)
    LinearLayout rentalLinear;
    @BindView(R.id.ic_star_1)
    ImageView star1;
    @BindView(R.id.ic_star_2)
    ImageView star2;
    @BindView(R.id.ic_star_3)
    ImageView star3;
    @BindView(R.id.ic_star_4)
    ImageView star4;
    @BindView(R.id.ic_star_5)
    ImageView star5;


    public UlasanItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
