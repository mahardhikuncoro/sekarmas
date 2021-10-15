package base.service.laporan;

import java.util.Map;

import base.data.laporan.LaporanJson;
import base.data.visimisimodel.VisiMisi;
import base.endpoint.UploadImageJson;
import base.location.BaseNetworkCallback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface LaporanEndpoint {

    @GET("api/post/index")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<LaporanJson> getListLaporan(@Header("Authorization") String auth);

    @Multipart
    @POST("api/post/store")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> uploadPost(@Header("Authorization") String authKey,
                                         @PartMap() Map<String, RequestBody> partMap,
                                         @Part() MultipartBody.Part image);


    @GET
    Call<BaseNetworkCallback> getImage(@Url String url);
}
