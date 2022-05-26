package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.sekarmas.mobile.application.R;
import user.changepassword.ChangePasswordActivity;
import user.informasi.InformasiDetail;
import user.pengaturan.PengaturanInterface;
import user.pengaturan.ProfileActivity;


/**
 * @author KUN <robert.kuncoro@pitik.id>
 */
public class PengaturanAdapter extends RecyclerView.Adapter<PengaturanAdapter.ScaleAdapterViewHolder> {


    private final Context context;
    private List<String> menuList;
    private PengaturanInterface pengaturanInterface;

    public PengaturanAdapter(Context context, List<String> notificationList, PengaturanInterface pengaturanInterface) {
        this.context = context;
        this.menuList = notificationList;
        this.pengaturanInterface = pengaturanInterface;
    }

    @NonNull
    @Override
    public ScaleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScaleAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.pengaturan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScaleAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String notification = menuList.get(position);
        holder.tvMenu.setText(menuList.get(position));
        if(menuList.get(position).equals("Ubah Kata Sandi")){
            holder.iconlist.setImageResource(R.drawable.ic_lock);
        }else if(menuList.get(position).equals("Tentang Aplikasi")){
            holder.iconlist.setImageResource(R.drawable.ic_info);
        }else if(menuList.get(position).equals("Pengaturan Perizinan")){
            holder.iconlist.setImageResource(R.drawable.ic_setting);
        }else if(menuList.get(position).equals("Pengaturan Notifikasi")){
            holder.iconlist.setImageResource(R.drawable.ic_notification);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuList.get(position).equals("Ubah Kata Sandi")){
                    Intent intent = new Intent(context, ChangePasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if(menuList.get(position).equals("Tentang Aplikasi")){
                    pengaturanInterface.showAbout();
//                    holder.iconlist.setImageResource(context.getResources().getDrawable(R.drawable.ic_info));
                }else if(menuList.get(position).equals("Pengaturan Perizinan")){
//                    holder.iconlist.setImageResource(context.getResources().getDrawable(R.drawable.ic_setting));
                }else if(menuList.get(position).equals("Pengaturan Notifikasi")){
//                    holder.iconlist.setImageResource(context.getResources().getDrawable(R.drawable.ic_notification));
                }

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<String> notificationList) {
        this.menuList = notificationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class ScaleAdapterViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout container;
        public ImageView iconlist;
        public TextView tvMenu;

        public ScaleAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.cons_pengaturan);
            iconlist = itemView.findViewById(R.id.iconlist);
            tvMenu = itemView.findViewById(R.id.tv_menu);

        }
    }
}
