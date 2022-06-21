package user.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

import base.data.userlogin.UserLoginJson;
import base.network.callback.FormJson;
import base.network.callback.LoginJson;
import base.data.loginmodel.LoginNewJson;
import base.network.callback.Slider;
import base.network.callback.Wewenang;
import base.screen.BaseDialogActivity;
import base.sqlite.model.SliderSQL;
import id.sekarpinter.mobile.application.R;
import ops.screen.MainActivityDashboard;
import base.sqlite.model.TaskListDetailModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.DashboardActivity;


public class LoginAcitivityApiData extends BaseDialogActivity  {

    protected String noHp;
    private ArrayList<Slider> sliderDataList;
    SliderSQL sliderdql;
    private ArrayList<String> sliderList;
    private ArrayList<String> roleList;
    private ArrayList<Wewenang> roleDataList;
    private String groupId="";

    protected RadioGroup rg;
    protected RadioButton[] rb;
    AlertDialog.Builder builder;

    protected void  callLoginAuthentication(String userId, final String password, final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()){
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            final LoginJson.LoginRequest request = new LoginJson().new LoginRequest();

            request.setLoginType("loginauthentication");
            request.setUserid(userId.toUpperCase());
            request.setPassword(password);
            request.setLon(String.valueOf(getLongitude()));
            request.setLat(String.valueOf(getLatitude()));
            request.setAddr(String.valueOf(getAddress()));

            Call<LoginJson.loginAutenticationCallback> call = endPoint.getAutentication(request);
            call.enqueue(new Callback<LoginJson.loginAutenticationCallback>() {
                @Override
                public void onResponse(Call<LoginJson.loginAutenticationCallback> call, Response<LoginJson.loginAutenticationCallback> response) {
                    try {
                        if(response.isSuccessful()){
                            if(response.body().getStatus().equalsIgnoreCase("1")) {

//                                final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
//                                LayoutInflater inflater = activity.getLayoutInflater();
//                                View dialogView = inflater.inflate(R.layout.pilih_user, null);
//                                final TextView _namaAplikasi = (TextView) dialogView.findViewById(R.id.namaUserLabel);
//                                final TextView _noregAplikasiLabel = (TextView) dialogView.findViewById(R.id.nikUserLabel);
//                                final TextView _etName = (TextInputEditText) dialogView.findViewById(R.id.etNama);
//                                final TextView _etNoreg = (TextInputEditText) dialogView.findViewById(R.id.etNik);
//                                final TextView _etDesc = (TextInputEditText) dialogView.findViewById(R.id.etDesc);
//                                final Spinner _branchSpinner = (Spinner) dialogView.findViewById(R.id.branchSpinner);
//                                final Button _branchSaveButton = (Button) dialogView.findViewById(R.id.branchSaveButton);
//
//                                List<String> reasonRejectList = new ArrayList<String>();
//                                for(String user : response.body().getWewenang()){
//                                    reasonRejectList.add(user);
//                                }
//
//                                ArrayAdapter<String> branchListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, reasonRejectList);
//                                branchListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                                _branchSpinner.setAdapter(branchListAdapter);
//                                _branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                            ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                                        }
//                                    }
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                                    }
//
//                                });

                                {
                                    if(response.body().getStatus().equalsIgnoreCase("1")) {
                                        userdata.save(response.body().getUserid(),
                                                response.body().getFullname(),
                                                response.body().getPhotoprofile(),
                                                response.body().getGroupid(),
                                                response.body().getGroupname(),
                                                response.body().getBranchid(),
                                                response.body().getBranchname(),
                                                response.body().getAccesstoken(),
                                                response.body().getTokentype(),
                                                response.body().getExpiredin());

                                        sliderDataList = new ArrayList<Slider>();
                                        sliderList = new ArrayList<String>();
                                        Integer idBannerAndVideo = null;
                                        String videoLink="",videoDesc ="" ,bannerLink="",bannerDesc="";

                                        sliderdql.deleteAll();
                                        for(LoginJson.SliderObject slider : response.body().getSlider()){
                                            saveApiSlider(slider);
                                        }
//                                        if(BuildConfig.FLAVOR.equalsIgnoreCase("mikro")) {
//                                            for (LoginJson.Banner banner : response.body().getBanner()) {
//                                                bannerLink = banner.getBannerUrl();
//                                                bannerDesc = banner.getBannerDesc();
//                                            }
//                                            for (LoginJson.Video video : response.body().getVideo()) {
//                                                videoLink = video.getVideoUrl();
//                                                videoDesc = video.getVideoDesc();
//                                            }
//                                            Log.e("BANNER AND VIDEO ", " : " + videoLink + bannerLink);
//                                            bannerAndVideoData.save(idBannerAndVideo, videoLink, videoDesc, bannerLink, bannerDesc);
//                                        }

//                                        if(!BuildConfig.FLAVOR.equalsIgnoreCase("mikro")) {
//                                            setFormMaster(response.body().getAccesstoken());
//                                        }else {
                                            Intent intentdashboard = new Intent(getApplicationContext(), MainActivityDashboard.class);
                                            startActivity(intentdashboard);
                                            finish();
//                                        }
                                    }else{
                                        dialog.dismiss();
                                        dialogMessage(response.body().getMessage());
                                    }
                                }
                            }else{
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }
                        }else{
                            dialog.dismiss();
                            dialog(R.string.errorBackend);
                        }
                    }catch (Exception e){
                        dialog.dismiss();
                        dialog(R.string.errorBackend);
                    }
                }

                @Override
                public void onFailure(Call<LoginJson.loginAutenticationCallback> call, Throwable t) {
                    dialog.dismiss();
                    dialog(R.string.errorBackend);
                }
            });
        }
    }

    private void saveUserData(Response<LoginNewJson> response, Response<UserLoginJson> userModel){

        userdata.save(userModel.body().getUser().getUsername(),
                userModel.body().getUser().getFullname(),
                userModel.body().getUser().getAvatar(),
                userModel.body().getUser().getDateOfBirth(),
                userModel.body().getUser().getGender(),
                userModel.body().getUser().getPhone(),
                userModel.body().getUser().getId(),
                response.body().getAccessToken(),
                userModel.body().getUser().getEmail(),
                "Test");
//        setFormMaster(response.body().getAccesstoken());

      /*  sliderDataList = new ArrayList<Slider>();
        sliderList = new ArrayList<String>();
        sliderdql.deleteAll();
        for(LoginJson.SliderObject slider : response.body().getSlider()){
            saveApiSlider(slider);
        }*/
        Intent intentdashboard = new Intent(getApplicationContext(), MainActivityDashboard.class);
        startActivity(intentdashboard);
        finish();
    }


    private void saveApiSlider(LoginJson.SliderObject sliderObject) {


//        for (int i=0;i<=5 ;i++) {

//            Log.e("SLIDER SIZE INPUT " , " + "+ i);
            Slider slider = new Slider();
            slider.setId(Long.parseLong(sliderObject.getSliderPriority()));
            slider.setName(sliderObject.getSliderDesc());
            slider.setImage( sliderObject.getSliderUrl());
            slider.setLink("https://www.youtube.com/watch?v=c-74OSumhNk");
            slider.setPublish("Y");
            slider.setPackage_name("");

            sliderDataList.add(slider);
            sliderdql.save(slider);
//        }
    }


    protected void verifyUser(final String username, final ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()) {
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        }
        else {

            final LoginJson.LoginRequest request = new LoginJson().new LoginRequest();

            request.setLoginType("verifyuser");
            request.setUserid(username.toString());
            request.setLon(String.valueOf(getLongitude()));
            request.setLat(String.valueOf(getLatitude()));
            request.setAddr(String.valueOf(getAddress()));
            Call<LoginJson.loginAutenticationCallback> callverify = endPoint.getAutentication(request);
            callverify.enqueue(new Callback<LoginJson.loginAutenticationCallback>() {
                @Override
                public void onResponse(Call<LoginJson.loginAutenticationCallback> call, Response<LoginJson.loginAutenticationCallback> response) {
                    try {
                        if(response.isSuccessful()){
                            dialog.dismiss();
                            if(response.body().getStatus().equalsIgnoreCase("1")){
                                String photoprofile = response.body().getPhotoprofile();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("photoprofile", photoprofile);
                                startActivity(intent);
                                finish();
                            }else{
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }
                        }
                        else{
                            dialog(R.string.errorBackend);
                            dialog.dismiss();
                        }
                    }catch (Exception e){
                        dialog.dismiss();
                        dialog(R.string.errorBackend);
                    }
                }
                @Override
                public void onFailure(Call<LoginJson.loginAutenticationCallback> call, Throwable t) {
                    dialog.dismiss();
                    dialog(R.string.errorBackend);
                }
            });
        }

    }
    protected void setFormMaster(String token) {
        if (!networkConnection.isNetworkConnected()) {
//            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {

            final FormJson.RequestForm requestForm = new FormJson().new RequestForm();
            requestForm.setType("form");
            requestForm.setUserid(userdata.select().getUsername());
//            String token = userdata.select().getAccesstoken();

            Call<FormJson.CallbackForm> callForm = endPoint.getDataMaster(token,requestForm);
            callForm.enqueue(new Callback<FormJson.CallbackForm>() {
                @Override
                public void onResponse(Call<FormJson.CallbackForm> call, final Response<FormJson.CallbackForm> response) {
                    try {
                        if (response.isSuccessful()) {
                            if(response.body().getStatus().equalsIgnoreCase("1")) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        String successResponsedata = "", successResponse = "";
                                        Gson gsondatabody = new Gson();
                                        successResponse = gsondatabody.toJson(response.body());
                                        formData.save("1", "FORM_SURVEY_DATA", "SECTION", successResponse);
                                        for (FormJson.CallbackForm.Data model : response.body().getData()) {
                                            Gson gsondata = new Gson();
                                            TaskListDetailModel datamodel = new TaskListDetailModel();
                                            datamodel.setNamaNasabah(model.getSectionName());
                                            successResponsedata = gsondata.toJson(model);
//                                                for (FormJson.CallbackForm.Data.Field fieldmodel : model.getField()) {
//                                                formData.save("1", "FORM_SURVEY", model.getSectionName(), successResponsedata);
                                            formData.save("1", "FORM_SURVEY", model.getSectionId(), successResponsedata);
//                                                }
                                            Log.e("Data SECTION ", " : " + model.getSectionName());
                                            Log.e("FORMDATA ", " : " + successResponsedata);
                                        }
                                    }
                                }).start();
                            }else if(response.body().getStatus().equalsIgnoreCase("0")){
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }else if(response.body().getStatus().equalsIgnoreCase("100")){
                                dialog.dismiss();
                                removeUserData(response.body().getMessage());
                            }else{
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }
                        } else {
                            dialogMessage(response.body().getMessage());
                        }
                    }catch (Exception e){
                    }
                    Intent intentdashboard = new Intent(getApplicationContext(), MainActivityDashboard.class);
                    startActivity(intentdashboard);
                    finish();
                }

                @Override
                public void onFailure(Call<FormJson.CallbackForm> call, Throwable t) {
                    dialog(R.string.errorBackend);
                    dialog.dismiss();
                }

            });

        }
    }

 /*   protected void  callNewLogin(String userId, final String password, final Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()){
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {

            final LoginJson.LoginRequest request = new LoginJson().new LoginRequest();
            request.setLoginType("loginldap");
            request.setUserid(userId);
            request.setPassword(password);
            request.setDeviceId(getdeviceId());
            request.setLon(String.valueOf(getLongitude()));
            request.setLat(String.valueOf(getLatitude()));
            request.setAddr(String.valueOf(getAddress()));

            Call<LoginJson.loginAutenticationCallback> call = endPoint.getAutentication(request);
            call.enqueue(new Callback<LoginJson.loginAutenticationCallback>() {
                @Override
                public void onResponse(Call<LoginJson.loginAutenticationCallback> call, Response<LoginJson.loginAutenticationCallback> response) {
                    try {
                        if(response.isSuccessful()){
                            if(response.body().getStatus().equalsIgnoreCase("1")) {
//                                userdata.save(response.body().getUserid(),
//                                        response.body().getFullname(),
//                                        response.body().getPhotoprofile(),
//                                        response.body().getGroupid(),
//                                        response.body().getGroupname(),
//                                        response.body().getBranchid(),
//                                        response.body().getBranchname(),
//                                        response.body().getAccesstoken(),
//                                        response.body().getTokentype(),
//                                        response.body().getExpiredin());
//                                setFormMaster(response.body().getAccesstoken());
//
//                                sliderDataList = new ArrayList<Slider>();
//                                sliderList = new ArrayList<String>();
//                                sliderdql.deleteAll();
//                                for(LoginJson.SliderObject slider : response.body().getSlider()){
//                                    saveApiSlider(slider);
//                                }

                                roleDataList = new ArrayList<Wewenang>();
                                // Create an array to populate the spinner
                                roleList = new ArrayList<String>();

                                for (Wewenang wewenang : response.body().getWewenang()) {
                                    Wewenang contentmodel = new Wewenang();
                                    contentmodel.setGroupid(wewenang.getGroupid());
                                    contentmodel.setUseridlos(wewenang.getUseridlos());
                                    contentmodel.setGroupname(wewenang.getGroupname());
                                    roleDataList.add(contentmodel);
                                    roleList.add(wewenang.getGroupname());
                                }
                                dialog.dismiss();
                                try {
                                    Log.e("LOG ROLEDATALIST "," : " + roleDataList.size());
                                    if (roleDataList.size() > 1)
                                        showUser(activity, response);
                                    else {
                                        saveUserData(response, response.body().getWewenang().get(0).getUseridlos());
                                    }
                                }catch (Exception e){
                                    Log.e("LOG "," : " + e.toString());
                                    dialogMessage("Anda tidak memiliki group id");
                                }
                            }else if(response.body().getStatus().equalsIgnoreCase("0")){
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }else{
                                dialog.dismiss();
                                dialogMessage(response.body().getMessage());
                            }
                        }else{
                            dialog.dismiss();
                            dialog(R.string.errorBackend);
                        }
                    }catch (Exception e){
                        Log.e("error ha "," " + e);
                        dialog.dismiss();
                        dialog(R.string.errorBackend);
                    }
                }

                @Override
                public void onFailure(Call<LoginJson.loginAutenticationCallback> call, Throwable t) {
                    dialog.dismiss();
                    dialog(R.string.errorBackend);
                }
            });


        }
    }
*/
    protected void showUser(Activity activity, final Response<LoginJson.loginAutenticationCallback> response){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pilih_user, null);
        final Button _branchSaveButton = (Button) dialogView.findViewById(R.id.branchSaveButton);
        final Button _branchCancelButton = (Button) dialogView.findViewById(R.id.branchCancelButton);
        final LinearLayout _linearLayout = (LinearLayout) dialogView.findViewById(R.id.fieldBranch);

        rb = new RadioButton[roleDataList.size()];
        rg = new RadioGroup(activity); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL

        for (Wewenang wewenang : roleDataList) {
            rb[roleDataList.indexOf(wewenang)] = new RadioButton(activity);
            rb[roleDataList.indexOf(wewenang)].setText(" " + wewenang.getGroupname());
            rb[roleDataList.indexOf(wewenang)].setId(Integer.valueOf(wewenang.getGroupid()));
            rb[roleDataList.indexOf(wewenang)].setTag(wewenang.getUseridlos());
            rb[roleDataList.indexOf(wewenang)].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                    // TODO Auto-generated method stub
//                    savePreferences("" + radio_id, value);
                    Log.e("HIHI "," : " +arg0.getTag());
                    Log.e("HOHO "," : " +arg0.getText());
                    groupId = String.valueOf(arg0.getTag());
                    _branchSaveButton.setBackgroundColor(R.color.orange_light);
                }

            });
            rg.addView(rb[roleDataList.indexOf(wewenang)]);
        }
        _linearLayout.addView(rg);//you add the whole RadioGroup to the layout

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();


        _branchSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupId.equalsIgnoreCase("")) {
//                    saveUserData(response, groupId);
                }
            }
        });

        _branchCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupId ="";
                dialogBuilder.cancel();
            }
        });
    }


    //SEKARMAS

    protected void  callLoginUser(String userId, final String password, final Activity activity) {
        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar).setCancelable(false);
        }
        dialog = builder.create();
        dialog.show();
        if (!networkConnection.isNetworkConnected()) {
            dialog.dismiss();
            dialog(R.string.errorNoInternetConnection);
        } else {
            newEndPoint.callLogin("test",userId,password).enqueue(new Callback<LoginNewJson>() {
                @Override
                public void onResponse(Call<LoginNewJson> call, Response<LoginNewJson> response) {
                    if(response.isSuccessful()){
//                        dialog.dismiss();
                        getUserLogin(response,activity);
                    }else {
                        dialog(R.string.errorPassword);
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<LoginNewJson> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void getUserLogin(final Response<LoginNewJson> responseLogin, final Activity activity){

        newEndPoint.getUserLogin("Bearer " + responseLogin.body().getAccessToken()).enqueue(new Callback<UserLoginJson>() {
            @Override
            public void onResponse(Call<UserLoginJson> call, Response<UserLoginJson> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Log.e("AVATAR" ," + " + response.body().getUser().getAvatar());
                    saveUserData(responseLogin,response);
                    startActivity(new Intent(activity, DashboardActivity.class));
                }
            }

            @Override
            public void onFailure(Call<UserLoginJson> call, Throwable t) {

            }
        });


    }

}
