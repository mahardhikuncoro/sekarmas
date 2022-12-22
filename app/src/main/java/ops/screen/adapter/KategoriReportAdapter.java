package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import base.data.sektormodel.SektorModel;
import base.sqlite.model.TaskListDetailModel;
import id.sekarpinter.mobile.application.R;


public class KategoriReportAdapter extends RecyclerView.Adapter<KategoriReportAdapter.ViewHolder> {

    private List<SektorModel> list;
    private Context context;
    private int lastPosition = -1;
    private static ReportInterface reportInterface;

    public interface ReportInterface {
        void onListSelected(SektorModel list);
    }
    public KategoriReportAdapter(Context context, List<SektorModel> list, ReportInterface reportInterface) {

        notifyDataSetChanged();
        this.list = list;
        this.context = context;
        this.reportInterface = reportInterface;
    }


    @Override
    public KategoriReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_kontent, parent, false);
        return new KategoriReportItem(view);
    }

    @Override
    public void onBindViewHolder(final KategoriReportAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            ((KategoriReportItem) holder).tvName.setText("" + list.get(position).getName());
            ((KategoriReportItem) holder).rentalLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportInterface.onListSelected(list.get(position));
                }
            });


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
