package base.service.registrasi;

import java.util.Map;

import base.location.BaseNetworkCallback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Mahardhi Kuncoro on 5/23/2022
 */
public interface RegistrasiEndpoint {


    @Multipart
    @POST("api/register")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> registrasi(@PartMap() Map<String, RequestBody> partMap,
                                         @Part() MultipartBody.Part avatars);

}
