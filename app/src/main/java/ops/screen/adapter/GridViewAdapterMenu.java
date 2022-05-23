package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import base.sqlite.model.DataMenuModel;
import id.sekarmas.mobile.application.R;


public class GridViewAdapterMenu extends ArrayAdapter<DataMenuModel> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<DataMenuModel> mGridData;

    public GridViewAdapterMenu(Context mContext, int layoutResourceId, ArrayList<DataMenuModel> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<DataMenuModel> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            if(mGridData.get(position).getMenudesc().equals("E-Pengaduan")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_laporan);
            }else if(mGridData.get(position).getMenudesc().equals("E-Umkm")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_sidebaru);
            }else if(mGridData.get(position).getMenudesc().equals("Pariwisata")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_pariwisata);
            }else if(mGridData.get(position).getMenudesc().equals("Kontak Darurat")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_kontak_darurat);
            }else if(mGridData.get(position).getMenudesc().equals("Informasi")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_informasi);
            }else if(mGridData.get(position).getMenudesc().equals("Visi & Misi")) {
                holder.imageView.setImageResource(R.mipmap.ic_menu_visi_misi);
            }else{
                holder.imageView.setImageResource(R.mipmap.ic_menu_setting);
            }

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        DataMenuModel item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getMenudesc()));

//        if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_suitcase")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        } else if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_building")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        } else if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_document")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        } else if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_new_pengajuan")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        } else if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_information")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        }else if (mGridData.get(position).getIcon().equalsIgnoreCase("ic_simulasi")) {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        } else {
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        }

//        holder.imageView.setImageResource(R.mipmap.ic_newdoc_foreground);
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);

        return row;
    }


    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}