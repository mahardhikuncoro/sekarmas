package ops.screen.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import id.sekarmas.mobile.application.R;
import user.informasi.InformasiDetail;

public class SimpleFragment extends Fragment {


    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMAGE = "image";

    public SimpleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_simple, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.textview_in_fragment);
        FrameLayout frame = (FrameLayout) rootView.findViewById(R.id.frame_information);
        String msg = getArguments().getString(KEY_MESSAGE);
        textView.setText(msg);

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), InformasiDetail.class);
                intent.putExtra(KEY_MESSAGE,  getArguments().getString(KEY_MESSAGE));
                intent.putExtra(KEY_IMAGE,  getArguments().getString(KEY_IMAGE));
                intent.putExtra(KEY_DESC,  getArguments().getString(KEY_DESC));
                Log.e("HAHAHAHHA "," clicked news : " +  getArguments().getString(KEY_IMAGE));
                startActivity(intent);

            }
        });

        return rootView;
    }

    public static Fragment newInstance(String message, String image,String desc, Integer i) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MESSAGE, message);
        bundle.putString(KEY_DESC,desc);
        bundle.putString(KEY_IMAGE,image);
        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}