package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import base.data.sektormodel.SektorModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarpinter.mobile.application.R;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ReportViewHolder> {

    public interface OnKategoriListener {
        void onItemSelected(int position);
    }

    private final Context context;
    private final List<SektorModel> kategoriList;
    private final OnKategoriListener onKategoriListener;
    private Integer idClicked;

    public KategoriAdapter(Context context, List<SektorModel> kategoriList, OnKategoriListener onKategoriListener, Integer idClicked) {
        this.context = context;
        this.kategoriList = kategoriList;
        this.onKategoriListener = onKategoriListener;
        this.idClicked = idClicked;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kategori, parent, false));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SektorModel kategori = kategoriList.get(position);
        holder.tvCategori.setText(kategori.getName());
        holder.tvCategori.setTextColor(context.getResources().getColor(R.color.orange_dark));
        holder.container.setBackground(context.getDrawable(R.drawable.rounded_outline_orange));
        holder.tvCategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onKategoriListener.onItemSelected(position);
            }
        });
        if(kategori.getId() == idClicked) {
            holder.tvCategori.setTextColor(context.getResources().getColor(R.color.white));
            holder.container.setBackground(context.getDrawable(R.drawable.rounded_orange));
        }
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    @SuppressLint("NonConstantResourceId")
    static class ReportViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.container) public LinearLayout container;
        @BindView(R.id.tv_kategori) public TextView tvCategori;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
