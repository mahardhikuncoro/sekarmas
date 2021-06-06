package base.service;

import java.util.List;

import base.data.Information;
import base.network.LoginNewJson;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InformationEndpoint {

    @GET("api/information/index")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<Information>> getInformationList(@Header("Authorization") String auth);
}
