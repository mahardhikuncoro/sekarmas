package ops.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;
import ops.screen.adapter.TaskListMisiAdapter;


public class TaskListMisiLine extends TaskListMisiAdapter.ViewHolder {

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


    public TaskListMisiLine(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
