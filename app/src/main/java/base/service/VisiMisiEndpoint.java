package base.service;

import base.data.VisiMisi;
import base.network.LoginNewJson;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VisiMisiEndpoint {

    @GET("api/visimisi")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<VisiMisi> getVisiMisi(@Header("Authorization") String auth);
}
