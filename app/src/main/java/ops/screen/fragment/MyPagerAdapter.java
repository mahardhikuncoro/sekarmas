package ops.screen.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import base.sqlite.model.NewsModel;
import id.sekarmas.mobile.application.R;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> pages;
    public Context myContext;
    public MyPagerAdapter(FragmentManager fm, ArrayList<NewsModel> list, Context myContext) {
        super(fm);
        this.myContext = myContext;
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
            pages.add(SimpleFragment.newInstance(categoryId.get(i).getNewsTitle(),categoryId.get(i).getImageUrl(), categoryId.get(i).getNewsDesc(),i));

    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

//    @Override
//    public Object instantiateItem(View collection, final int pos) { //have to make final so we can see it inside of onClick()
//        LayoutInflater inflater = (LayoutInflater) collection.getContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//
//        View page = inflater.inflate(R.layout.tasklist_fragment, null);
//
//        page.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("TAG", "This page was clicked: " + pos);
//            }
//        });
//
//        ((ViewPager) collection).addView(page, 0);
//        return page;
//    }

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