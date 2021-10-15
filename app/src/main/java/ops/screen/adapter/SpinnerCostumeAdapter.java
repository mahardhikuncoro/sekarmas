package ops.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import id.sekarmas.mobile.application.R;

/**
 * Created by Human on 12/28/2017.
 */

public class SpinnerCostumeAdapter extends BaseAdapter {

    Context context;
    private int image[];
    private ArrayList<String> privacySelect;
    LayoutInflater inflater;

    public SpinnerCostumeAdapter(Context context, ArrayList<String> privacySelect) {
        this.context = context;
        this.image = image;
        this.privacySelect = privacySelect;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return privacySelect.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.spinner_item_post, null);
        ImageView icon = convertView.findViewById(R.id.iv_spin_post);
        TextView item = convertView.findViewById(R.id.tv_spin_post);
//        icon.setImageResource(image[position]);
        item.setText(privacySelect.get(position));

        return convertView;
    }
}
