package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import base.data.pariwisatamodel.PariwisataModel;
import base.data.pariwisatamodel.UlasanJson;
import base.network.callback.NetworkClientNew;
import base.utils.enm.ParameterKey;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import user.pariwisata.DetailPariwisataActivity;


public class UlasanAdapter extends RecyclerView.Adapter<UlasanAdapter.ViewHolder> {

    private List<UlasanJson> list;
    private Context context;
    private int lastPosition = -1;
    private int id = 0;


    public UlasanAdapter(List<UlasanJson> list, Context context) {
        this.list = list;
        this.context = context;
        this.id = id;
    }


    @Override
    public UlasanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ulasan, parent, false);
        return new UlasanItem(view);
    }

    @Override
    public void onBindViewHolder(final UlasanAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        ((UlasanItem) holder).tvNama.setText("" + list.get(position).getCreatedByUsername());
        ((UlasanItem) holder).tvDate.setText("" + list.get(position).getCreatedAt());
        ((UlasanItem) holder).tvDesc.setText("" + list.get(position).getDeskripsi());

        if(list.get(position).getRating() == 1){
            ((UlasanItem) holder).star1.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star2.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star3.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star4.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star5.setImageResource(R.drawable.ic_star_grey);
        }else if(list.get(position).getRating() == 2){
            ((UlasanItem) holder).star1.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star2.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star3.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star4.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star5.setImageResource(R.drawable.ic_star_grey);
        }else if(list.get(position).getRating() == 3){
            ((UlasanItem) holder).star1.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star2.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star3.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star4.setImageResource(R.drawable.ic_star_grey);
            ((UlasanItem) holder).star5.setImageResource(R.drawable.ic_star_grey);
        }else if(list.get(position).getRating() == 4){
            ((UlasanItem) holder).star1.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star2.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star3.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star4.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star5.setImageResource(R.drawable.ic_star_grey);
        }else if(list.get(position).getRating() == 5){
            ((UlasanItem) holder).star1.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star2.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star3.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star4.setImageResource(R.drawable.ic_star_orange);
            ((UlasanItem) holder).star5.setImageResource(R.drawable.ic_star_orange);
        }

        setAnimation(holder.itemView, position);

        ((UlasanItem) holder).rentalLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
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

    protected void dialog(String rString) {
        new MaterialDialog.Builder(context)
                .content(rString)
                .positiveText(R.string.buttonClose)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(true)
                .show();
    }

}
