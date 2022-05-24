package base.service.registrasi;

import base.location.BaseNetworkCallback;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import retrofit2.http.Path;

/**
 * Created by Mahardhi Kuncoro on 5/23/2022
 */
public interface RegistrasiEndpoint {


    @POST("api/register")
    @Headers("X-Requested-With: XMLHttpRequest")
    @FormUrlEncoded
    Call<BaseNetworkCallback> registrasi(@Field("fullname") String fullname,
                                         @Field("username") String username,
                                         @Field("email") String email,
                                         @Field("password") String password,
                                         @Field("phone") String phone,
                                         @Field("gender") String gender,
                                         @Field("password_confirmation") String password_confirmation,
                                         @Field("date_of_birth") String date_of_birth);

}
