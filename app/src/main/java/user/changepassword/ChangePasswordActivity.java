package user.changepassword;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import base.data.loginmodel.ResetPasswordJson;
import base.location.BaseNetworkCallback;
import base.network.callback.ChangePassJson;
import base.screen.BaseDialogActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.sekarmas.mobile.application.R;
import ops.screen.MainActivityDashboard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.login.LoginActivity;

public class ChangePasswordActivity extends BaseDialogActivity {

    @BindView(R.id.etOldpass) EditText etOldpass;
    @BindView(R.id.etNewpass) EditText etNewpass;
    @BindView(R.id.etNewpassconf) EditText etNewpassconf;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.input_layout_password) TextInputLayout layoutInputPassword;
    @BindView(R.id.input_layout_password_confirm) TextInputLayout layoutInputPasswordconfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        initiateApiData();
        if(getIntent().getExtras()!= null)
            etEmail.setText(userdata.select().getEmail());
    }

    @OnClick(R.id.buttonSavePass)
    public void saveNewPassword(){
        if(getIntent().getExtras()!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setView(R.layout.progress_bar).setCancelable(false);
            }
            dialog = builder.create();
            dialog.show();
            if (!networkConnection.isNetworkConnected() && !etEmail.getText().toString().equals("")) {
                dialog.dismiss();
                dialog(R.string.errorNoInternetConnection);
            } else {

                final ResetPasswordJson request = new ResetPasswordJson();
                request.setEmail(userdata.select().getEmail());

                Call<BaseNetworkCallback> resetPasswordJsonCall = newEndPoint.callReset(request);
                resetPasswordJsonCall.enqueue(new Callback<BaseNetworkCallback>() {
                    @Override
                    public void onResponse(Call<BaseNetworkCallback> call, Response<BaseNetworkCallback> response) {
                        if (response.body().getSuccess()) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            dialogMessage(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseNetworkCallback> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
            }



//                Call<ChangePassJson.ChangePassCallback> callChangePass = endPoint.changePassword(userdata.select().getAccesstoken(), request);
//                callChangePass.enqueue(new Callback<ChangePassJson.ChangePassCallback>() {
//                    @Override
//                    public void onResponse(Call<ChangePassJson.ChangePassCallback> call, Response<ChangePassJson.ChangePassCallback> response) {
//                        if(response.isSuccessful()){
//                            if(response.body().getStatus().equalsIgnoreCase("1")){
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successupdatepassword), Toast.LENGTH_LONG).show();
//                                dialog.dismiss();
//                                userdata.deleteAll();
//                                formData.deleteAll();
//                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                            }else if(response.body().getStatus().equalsIgnoreCase("100")){
//                                dialog.dismiss();
//                                removeUserData(response.body().getMessage());
//                            }else{
//                                dialog.dismiss();
//                                dialogMessage(response.body().getMessage());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ChangePassJson.ChangePassCallback> call, Throwable t) {
//                        dialog.dismiss();
//                    }
//                });
//
//            }
//        }else{
//            layoutInputPasswordconfirm.setError(getResources().getString(R.string.passnotmatch));
//        }
            }else{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("  https://www.sekarmas.com/password/reset"));
            startActivity(browserIntent);

        }
    }

    public boolean passwordIdentic(String newPass, String confirmPass){
        return newPass.equalsIgnoreCase(confirmPass);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.iv_backbutton)
    public void backButton(){
        finish();
    }
}
