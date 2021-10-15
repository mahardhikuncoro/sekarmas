package base.service.information;

import java.util.List;

import base.data.informationmodel.Information;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface InformationEndpoint {

    @GET("api/information/index")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<Information>> getInformationList(@Header("Authorization") String auth);
}
