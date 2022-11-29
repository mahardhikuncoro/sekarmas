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
import base.network.callback.NetworkClientNew;
import base.utils.enm.ParameterKey;
import id.sekarpinter.mobile.application.R;
import okhttp3.OkHttpClient;
import user.pariwisata.DetailPariwisataActivity;


public class PariwisataAdapter extends RecyclerView.Adapter<PariwisataAdapter.ViewHolder> {

    private List<PariwisataModel> list;
    private Context context;
    private int lastPosition = -1;
    private int id = 0;


    public PariwisataAdapter(List<PariwisataModel> list, Context context, Integer id) {
        this.list = list;
        this.context = context;
        this.id = id;
    }


    @Override
    public PariwisataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wisata, parent, false);
        return new PariwisataItem(view);
    }

    @Override
    public void onBindViewHolder(final PariwisataAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if(list.get(position).getKategoriId() == id) {
            ((PariwisataItem) holder).namaNasabah.setText("" + list.get(position).getNama());
            ((PariwisataItem) holder).idNasabah.setText("" + list.get(position).getAlamat());
            ((PariwisataItem) holder).namaNasabah.setGravity(Gravity.CENTER_VERTICAL);
//            if(list.get(position).getStatus().equals("created"))
//                ((PariwisataItem) holder).ivSatus.setImageResource(R.drawable.ic_pending);
//            else if(list.get(position).getStatus().equals("approved"))
//                ((PariwisataItem) holder).ivSatus.setImageResource(R.drawable.ic_done_status);
//            else
//                ((PariwisataItem) holder).ivSatus.setImageResource(R.drawable.ic_reject);

            if (list.get(position).getProfilePicture() != null) {
                String url = list.get(position).getProfilePicture();
                Log.e("URL_PHOTO", " " + url);
                if (url != null && !url.isEmpty()) {
                    OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                    Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient)).build();
                    picasso.setLoggingEnabled(true);
                    picasso.load(url)
                            .placeholder(R.drawable.img_default)
                            .error(R.drawable.img_default)
                            .fit()
                            .centerCrop()
                            .into(((PariwisataItem) holder).ivList, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                }
                            });
                }
            }
        }


        setAnimation(holder.itemView, position);

        ((PariwisataItem) holder).rentalLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer idPariwisata = list.get(position).getId();
                Intent intent = new Intent(context, DetailPariwisataActivity.class);
                intent.putExtra(ParameterKey.ID_UMKM, idPariwisata);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
