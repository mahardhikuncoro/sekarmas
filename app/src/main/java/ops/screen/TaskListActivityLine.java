package ops.screen;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;


public class TaskListActivityLine extends TaskListActivityAdapter.ViewHolder {

    @BindView(R.id.namaNasabah)
    TextView namaNasabah;
    @BindView(R.id.idNasabah)
    TextView idNasabah;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.rentalLinear)
    LinearLayout rentalLinear;
    @BindView(R.id.iconlist)
    ImageView _iconlist;


    public TaskListActivityLine(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
