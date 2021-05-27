package ops.screen.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import base.sqlite.NewsModel;
import id.sekarmas.mobile.application.R;
import ops.screen.CameraActivity;
import ops.screen.offline.FormOfflineDocument;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> pages;
    Context myContextl;
    public MyPagerAdapter(FragmentManager fm, ArrayList<NewsModel> list, Context myContext) {
        super(fm);
        this.myContextl = myContext;
        notifyDataSetChanged();
        initPages(list);
    }

    /*@Override
    public Fragment getItem(int pos) {
        switch(pos) {
            case 0:
                return SimpleFragment.newInstance("Page 1");
            case 1:
                return SimpleFragment.newInstance("Page 2");
            case 2:
                return SimpleFragment.newInstance("Page 3");
            case 3:
                return SimpleFragment.newInstance("Page 4");
            default:
                return SimpleFragment.newInstance("Page 99");
        }
    }*/

    private void initPages(ArrayList id) {
        pages = new ArrayList<Fragment>();
        addPage(id);
    }

    /**
     * Add new BookListFragment to the ViewPager.
     *
     * @param categoryId
     *            - the category id
     */
    public void addPage(ArrayList<NewsModel> categoryId) {
        for(int i = 0; i<categoryId.size() ;i++)
            pages.add(SimpleFragment.newInstance(categoryId.get(i).getNewsTitle()));
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("WOISSS "," : " + position);
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}

class BookOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
    private int currentPage;

    @Override
    public void onPageSelected(int position) {
        // current page from the actual position
        currentPage = position;
        Log.e("TEST "," " + currentPage);
    }

    public int getCurrentPage() {
        return currentPage;
    }

//    private void showDetailImage(/*String url, final String doc_code, final String doc_id*/){
//        final AlertDialog dialogBuilder = new AlertDialog.Builder().create();
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.detail_image, null);
////        final ScrollView _scrool = (ScrollView) dialogView.findViewById(R.id.scroolImage);
//        final ImageView _detailimage = (ImageView) dialogView.findViewById(R.id.detailimage);
//        final Button _fotoUlangButton = (Button) dialogView.findViewById(R.id.fotoUlangButton);
//        final Button _kembaliButton = (Button) dialogView.findViewById(R.id.kembaliButton);
//        File imgFile = new  File(getDokumen(idDokumen).getImageUrl());
//        if(imgFile.exists()){
//
//            Bitmap bitmap = decodeFile(imgFile);
//            _detailimage.setImageBitmap(bitmap);
//
//        }
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.show();
//        _fotoUlangButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("ID IMAGE "," : " + idImage );
//                Log.e("ID REGNO "," : " + getIntent().getStringExtra("REGNO") );
//                Intent intentprofile = new Intent(getApplicationContext(), CameraActivity.class);
//                intentprofile.putExtra("REGNO", getIntent().getStringExtra("REGNO"));
//                intentprofile.putExtra("TC", getIntent().getStringExtra("TC"));
//                intentprofile.putExtra("UPLOAD_TYPE","survey_offline");
//                intentprofile.putExtra("IDIMAGE",idImage);
//                intentprofile.putExtra("IDDOCTYPE",idTypeDokumen);
//                intentprofile.putExtra("DOCTYPEDESC",typeDokumenDesc);
//                intentprofile.putExtra("DOCNAME",String.valueOf(_etnamadocument.getText()));
//                intentprofile.putExtra("DOCID","NEW");
//                startActivity(intentprofile);
//            }
//        });
//        _kembaliButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogBuilder.cancel();
//            }
//        });
//    }

}