package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.network.callback.NetworkConnection;
import base.service.laporan.LaporanEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.InformasiModel;
import base.sqlite.model.Userdata;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import ops.screen.fragment.SimpleFragment;
import ops.screen.fragment.TaskListInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import user.informasi.InformasiDetail;


public class InformasiAdapter extends RecyclerView.Adapter<InformasiItem> implements Filterable {

    private ArrayList<InformasiModel> list;
    private static List<InformasiModel> listFiltered;
    private Context context;
    private int lastPosition = -1;
    private Config config;
    private NetworkConnection networkConnection;
    private static TaskListInterface listener;
    private Userdata userdata;
    LaporanEndpoint laporanEndpoint;


    public InformasiAdapter(Context context, ArrayList<InformasiModel> list) {

        notifyDataSetChanged();
        this.list = list;
        this.context = context;
        this.listFiltered = list;
        this.listener = listener;

        config = new Config(context);
        networkConnection = new NetworkConnection(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getServer())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getUnsafeOkHttpClient())
                .build();

        laporanEndpoint = retrofit.create(LaporanEndpoint.class);
        userdata = new Userdata(context);

    }


    @Override
    public InformasiItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_informasi, parent, false);
        return new InformasiItem(view);
    }

    @Override
    public void onBindViewHolder(final InformasiItem holder, @SuppressLint("RecyclerView") final int position) {

         if(list.get(position).getImageUrl() != null) {
             String url = base.service.URL.checkUrl()+list.get(position).getImageUrl();
             if (url != null && !url.isEmpty()) {
                 OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                 Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient)).build();
                 picasso.setLoggingEnabled(true);
                 picasso.load(url)
                         .placeholder(R.drawable.defaultslide)
                         .error(R.drawable.defaultslide)
                         .fit()
                         .centerCrop()
                         .into(((InformasiItem) holder).ivPost, new Callback() {
                             @Override
                             public void onSuccess() {
                                 ((InformasiItem) holder).progressBar.setVisibility(View.GONE);
                                 Log.e("SUKSES", " ");
                             }

                             @Override
                             public void onError() {
                                 ((InformasiItem) holder).progressBar.setVisibility(View.GONE);
                                 Log.e("ERROR", " ");
                             }
                         });
             }
         }


        ((InformasiItem) holder).txtPostDesc.setText("" + list.get(position).getNewsTitle());
        ((InformasiItem) holder).txtDate.setText("" + list.get(position).getCreateDate());


        ((InformasiItem) holder).cvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InformasiDetail.class);
                intent.putExtra("message",list.get(position).getNewsTitle());
                intent.putExtra("desc",list.get(position).getNewsDesc());
                intent.putExtra("image",list.get(position).getImageUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
//                    listener.onListSelected(listFiltered.get(getAdapterPosition()));
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

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
/*                if(charString.isEmpty()) {
                    listFiltered = list;
                }else{
                    List<TaskListDetailModel> filteredList = new ArrayList<>();
                    for(TaskListDetailModel model : list){
                        if(model.getNamaNasabah().toUpperCase().contains(charString.toUpperCase())){
                            filteredList.add(model);
                        }
                    }
                    listFiltered = filteredList;
                }*/
                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                try {
                    Log.e("SIZE ","listFiltered : " + listFiltered.size());
//                    listFiltered = (ArrayList<TaskListDetailModel>) results.values;
//                    notifyDataSetChanged();
//                }catch (Exception e){}

            }
        };
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

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new java.net.URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}
