package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;
import ops.screen.TaskListAdapter;


public class KategoriReportItem extends KategoriReportAdapter.ViewHolder {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.rentalLinear)
    LinearLayout rentalLinear;


    public KategoriReportItem(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
