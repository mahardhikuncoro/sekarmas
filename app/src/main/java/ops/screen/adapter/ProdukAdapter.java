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

import base.data.pariwisatamodel.KontentWisata;
import base.data.pariwisatamodel.PariwisataModel;
import base.network.callback.NetworkClientNew;
import base.utils.enm.ParameterKey;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import user.pariwisata.DetailPariwisataActivity;


public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {

    private List<KontentWisata> list;
    private Context context;
    private int lastPosition = -1;
    private int id = 0;


    public ProdukAdapter(List<KontentWisata> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public ProdukAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk, parent, false);
        return new ProdukItem(view);
    }

    @Override
    public void onBindViewHolder(final ProdukAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        ((ProdukItem) holder).tvTitle.setText("" + list.get(position).getNama());
        ((ProdukItem) holder).tvSubTitle.setText("" + list.get(position).getDeskripsi());
        ((ProdukItem) holder).tvPrice.setText("Rp. " +list.get(position).getHarga());

        ((ProdukItem) holder).lnProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Integer idPariwisata = list.get(position).getId();
//                Intent intent = new Intent(context, DetailPariwisataActivity.class);
//                intent.putExtra(ParameterKey.ID_UMKM, idPariwisata);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
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
