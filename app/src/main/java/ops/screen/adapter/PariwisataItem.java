package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;
import ops.screen.fragment.UmkmAdapter;


public class PariwisataItem extends PariwisataAdapter.ViewHolder {

    @BindView(R.id.namaNasabah)
    TextView namaNasabah;
    @BindView(R.id.idNasabah)
    TextView idNasabah;
    @BindView(R.id.rentalLinear)
    LinearLayout rentalLinear;
    @BindView(R.id.iconlist)
    ImageView _iconlist;
    @BindView(R.id.iv_status)
    ImageView ivSatus;
    @BindView(R.id.iv_list)
    ImageView ivList;


    public PariwisataItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
