package ops.screen;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.List;

import base.data.visimisimodel.Visi;
import base.network.callback.EndPoint;
import base.network.callback.NetworkClient;
import base.network.callback.NetworkConnection;
import base.sqlite.model.Config;
import id.sekarmas.mobile.application.R;
import ops.screen.fragment.TaskListInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> implements Filterable {

    private List<Visi> list;
    private static List<Visi> listFiltered;
    private Context context;
    private int lastPosition = -1;
    private Config config;
    private NetworkConnection networkConnection;
    private EndPoint endPoint;
    private String typelist;
    private static TaskListInterface listener;


    public TaskListAdapter(Context context, List<Visi> list) {

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

        endPoint = retrofit.create(EndPoint.class);
    }


    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_holder, parent, false);
        return new TaskListLine(view);
    }

    @Override
    public void onBindViewHolder(final TaskListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            ((TaskListLine) holder).namaNasabah.setText("" + list.get(position).getContent());

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

}
