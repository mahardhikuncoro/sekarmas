package base.service.umkm;

import java.util.Map;

import base.data.sektormodel.SektorJson;
import base.data.umkmmodel.UmkmCallback;
import base.data.umkmmodel.umkmlist.UmkmListJson;
import base.location.BaseNetworkCallback;
import base.data.umkmmodel.UmkmModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UmkmEndpoint {

    @GET("api/umkm")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<UmkmListJson> getListUmkm(@Header("Authorization") String auth);

    @GET("api/umkm/{id_umkm}")
    Call<UmkmModel> getDetailUmkm(@Header("Authorization") String auth,
                                  @Path("id_umkm") String id_umkm);

    @GET("api/umkm/sektor")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<SektorJson> getSektorUmkm(@Header("Authorization") String auth);

    @DELETE("api/umkm/{id_umkm}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<SektorJson> deleteUmkm(@Header("Authorization") String auth,
                                @Path("id_umkm") String post_id);

    @Multipart
    @POST("api/umkm")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<UmkmCallback> uploadSidebaru(@Header("Authorization") String authKey,
                                      @PartMap() Map<String, RequestBody> partMap,
                                      @Part() MultipartBody.Part image);

    @Multipart
    @POST("api/umkm/update/{id_umkm}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<UmkmCallback> updateSidebaru(@Header("Authorization") String authKey,
                                      @PartMap() Map<String, RequestBody> partMap,
                                      @Path("id_umkm") String id_umkm);

    @Multipart
    @POST("api/umkm/produk")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> uploadImageSidebaru(@Header("Authorization") String authKey,
                                         @PartMap() Map<String, RequestBody> partMap,
                                         @Part() MultipartBody.Part image);
}
