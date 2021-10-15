package base.service.kontak;

import java.util.List;

import base.data.informationmodel.Information;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface KontakEndpoint {

    @GET("api/contact/index")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<Kontak>> getKontak(@Header("Authorization") String auth);
}
