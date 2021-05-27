package base.service;

import base.network.LoginNewJson;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
}
