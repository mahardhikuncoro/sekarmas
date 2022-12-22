package base.service.report;

import java.util.Map;

import base.data.laporan.LaporanJson;
import base.data.loginmodel.ResetPasswordJson;
import base.data.reportmodel.ReportJson;
import base.data.reportmodel.ReportModel;
import base.data.sektormodel.SektorJson;
import base.location.BaseNetworkCallback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ReportEndpoint {

    @GET("api/report/kategori")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<SektorJson> getListKategori(@Header("Authorization") String auth);

    @GET("api/report/user/{user_id}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<ReportJson> getBlackList(@Header("Authorization") String auth,
                                  @Path("user_id") String user_id);

    @Multipart
    @POST("api/post/store")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> uploadPost(@Header("Authorization") String authKey,
                                         @PartMap() Map<String, RequestBody> partMap,
                                         @Part() MultipartBody.Part image);

    @POST("api/report")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> report(@Header("Authorization") String authKey,
                                     @Body ReportModel reportModel);


    @GET
    Call<BaseNetworkCallback> getImage(@Url String url);
}
