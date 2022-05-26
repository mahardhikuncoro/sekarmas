package base.service.login;

import base.data.loginmodel.LoginNewJson;
import base.data.loginmodel.LogoutJson;
import base.data.loginmodel.ResetPasswordJson;
import base.data.userlogin.UserLoginJson;
import base.location.BaseNetworkCallback;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EndpointLogin {

    @POST("api/login")
    @Headers("X-Requested-With: XMLHttpRequest")
    @FormUrlEncoded
    Call<LoginNewJson> callLogin(@Field("device-id") String deviceId,
                                 @Field("username") String username,
                                 @Field("password") String password);

    @GET("api/user")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<UserLoginJson> getUserLogin(@Header("Authorization") String auth);

    @GET("api/logout")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<LogoutJson> logoutUser(@Header("Authorization") String auth);

    @POST("api/password/reset")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> callReset(@Body ResetPasswordJson  resetPasswordJson);

}
