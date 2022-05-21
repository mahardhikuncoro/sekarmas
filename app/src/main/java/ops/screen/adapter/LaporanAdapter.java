package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import base.data.laporan.DataLaporan;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkClientNew;
import base.network.callback.NetworkConnection;
import base.service.laporan.LaporanEndpoint;
import base.sqlite.model.Config;
import base.sqlite.model.Userdata;
import id.sekarmas.mobile.application.R;
import okhttp3.OkHttpClient;
import user.laporan.LaporanDetail;
import ops.screen.fragment.TaskListInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LaporanAdapter extends RecyclerView.Adapter<LaporanItem> implements Filterable {

    private ArrayList<DataLaporan> list;
    private static List<DataLaporan> listFiltered;
    private Context context;
    private int lastPosition = -1;
    private Config config;
    private NetworkConnection networkConnection;
    private static TaskListInterface listener;
    private Userdata userdata;
    LaporanEndpoint laporanEndpoint;


    public LaporanAdapter(Context context, ArrayList<DataLaporan> list) {

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
    public LaporanItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_laporan, parent, false);
        return new LaporanItem(view);
    }

    @Override
    public void onBindViewHolder(final LaporanItem holder, @SuppressLint("RecyclerView") final int position) {

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
                         .into(((LaporanItem) holder).ivPost, new Callback() {
                             @Override
                             public void onSuccess() {
                                 ((LaporanItem) holder).progressBar.setVisibility(View.GONE);
                                 Log.e("SUKSES", " ");
                             }

                             @Override
                             public void onError() {
                                 ((LaporanItem) holder).progressBar.setVisibility(View.GONE);
                                 Log.e("ERROR", " ");
                             }
                         });
             }

             String img_url = userdata.select().getPhotoprofile();
             if(img_url!= null) {
                 OkHttpClient picassoClient = NetworkClientNew.getUnsafeOkHttpClient();
                 Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient)).build();
                 picasso.setLoggingEnabled(true);
                 picasso.load(base.service.URL.checkUrl()+img_url)
                         .placeholder(R.drawable.ic_profile)// Place holder image from drawable folder
                         .error(R.drawable.ic_profile).resize(200, 200).rotate(90)
                         .into( ((LaporanItem) holder).imgProfile, new com.squareup.picasso.Callback() {
                             @Override
                             public void onSuccess() {
                             }

                             @Override
                             public void onError() {
                             }
                         });
             }



             Log.e("IMAGE URL"," : " + base.service.URL.checkUrl()+list.get(position).getImageUrl());
             Log.e(" URL"," : " + list.get(position).getImageUrl());
         }



            ((LaporanItem) holder).txtPostName.setText("" + list.get(position).getUser().getFullname());
            ((LaporanItem) holder).txtPostTitle.setText("" + list.get(position).getTitle());
            ((LaporanItem) holder).txtPosition.setText("" + list.get(position).getKabupatenKota());
            ((LaporanItem) holder).txtCategory.setText("" + list.get(position).getCategory().getName());
            ((LaporanItem) holder).txtPostDesc.setText("" + list.get(position).getDescription());
            ((LaporanItem) holder).txtComment.setText("" + list.get(position).getCommentCount() + " komentar");

            if(list.get(position).getStatus()==1)
                ((LaporanItem) holder).ivStatus.setImageResource(R.drawable.ic_reject);
            else if(list.get(position).getStatus()==2)
                ((LaporanItem) holder).ivStatus.setImageResource(R.drawable.ic_pending);
            else
                ((LaporanItem) holder).ivStatus.setImageResource(R.drawable.ic_done_status);

        ((LaporanItem) holder).txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_laporan = list.get(position).getId();
                Intent intent = new Intent(context, LaporanDetail.class);
                intent.putExtra("id_laporan",id_laporan);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        ((LaporanItem) holder).cvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_laporan = list.get(position).getId();
                Intent intent = new Intent(context, LaporanDetail.class);
                intent.putExtra("id_laporan",id_laporan);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewHolder(View view) {
            super(view);
//            name = view.findViewById(R.id.namaNasabah);
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
