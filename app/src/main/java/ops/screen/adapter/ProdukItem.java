package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;


public class ProdukItem extends ProdukAdapter.ViewHolder {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSubTitle)
    TextView tvSubTitle;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.lnProduk)
    LinearLayout lnProduk;


    public ProdukItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
