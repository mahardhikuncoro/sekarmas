package ops.screen.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import base.network.callback.NetworkClientNew;
import base.network.callback.NetworkConnection;
import base.data.umkmmodel.UmkmModel;
import base.sqlite.model.Config;
import base.utils.enm.ParameterKey;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.adapter.LaporanItem;
import user.sidebaru.DetailSidebaruActivity;


public class UmkmAdapter extends RecyclerView.Adapter<UmkmAdapter.ViewHolder> {

    private List<UmkmModel> list;
    private Context context;
    private int lastPosition = -1;
    private Config config;
    private NetworkConnection networkConnection;


    public UmkmAdapter(List<UmkmModel> list, Context context) {
        this.list = list;
        this.context = context;
//        this.callback = callback;
    }


    @Override
    public UmkmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_umkm_holder, parent, false);
        return new UmkmListItem(view);
    }

    @Override
    public void onBindViewHolder(final UmkmAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            ((UmkmListItem) holder).namaNasabah.setText("" + list.get(position).getNama());
            ((UmkmListItem) holder).idNasabah.setText("" + list.get(position).getAlamat());
            ((UmkmListItem) holder).namaNasabah.setGravity(Gravity.CENTER_VERTICAL);
            if(list.get(position).getStatus().equals("created"))
                ((UmkmListItem) holder).ivSatus.setImageResource(R.drawable.ic_pending);
            else if(list.get(position).getStatus().equals("approved"))
                ((UmkmListItem) holder).ivSatus.setImageResource(R.drawable.ic_done_status);
            else
                ((UmkmListItem) holder).ivSatus.setImageResource(R.drawable.ic_reject);

            if(list.get(position).getProfilePicture() != null) {
                String url = list.get(position).getProfilePicture();
                Log.e("URL_PHOTO", " "+url);
                if (url != null && !url.isEmpty()) {
                    OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                    Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient)).build();
                    picasso.setLoggingEnabled(true);
                    picasso.load(url)
                            .placeholder(R.drawable.img_default)
                            .error(R.drawable.img_default)
                            .fit()
                            .centerCrop()
                            .into(((UmkmListItem) holder).ivList, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                }
                            });
                }
            }


        setAnimation(holder.itemView, position);

        ((UmkmListItem) holder).rentalLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idUmkm = String.valueOf(list.get(position).getId());
                Intent intent = new Intent(context, DetailSidebaruActivity.class);
                intent.putExtra(ParameterKey.ID_UMKM, idUmkm);
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
