package base.service.pariwisata;

import java.util.List;
import java.util.Map;

import base.data.informationmodel.Information;
import base.data.pariwisatamodel.KontentWisata;
import base.data.pariwisatamodel.PariwisataJson;
import base.data.pariwisatamodel.PariwisataModel;
import base.data.pariwisatamodel.UlasanJson;
import base.data.sektormodel.SektorJson;
import base.data.umkmmodel.UmkmCallback;
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

public interface PariwisataEndpoint {

    @GET("api/pariwisata")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<PariwisataJson> getPariwisataList(@Header("Authorization") String auth);

    @GET("api/pariwisata/kategori/{id_kategori}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<PariwisataJson> getPariwisataByKategory(@Header("Authorization") String auth,
                                              @Path("id_kategori") Integer id);

    @GET("api/pariwisata/{id_pariwisata}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<PariwisataModel> getDetailPariwisata(@Header("Authorization") String auth,
                                              @Path("id_pariwisata") Integer id);

    @GET("api/pariwisata/ulasan/{id_pariwisata}/{id_ulasan}")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<UlasanJson>> getUlasan(@Header("Authorization") String auth,
                                     @Path("id_pariwisata") Integer id,
                                     @Path("id_ulasan") String id_ulasan);
    @GET("api/information/index")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<Information>> getInformationList(@Header("Authorization") String auth);

    @GET("api/pariwisata/kategori")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<SektorJson> getKategori(@Header("Authorization") String auth);

    @Multipart
    @POST("api/pariwisata")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<UmkmCallback> addPariwisata(@Header("Authorization") String authKey,
                                      @PartMap() Map<String, RequestBody> partMap,
                                      @Part() MultipartBody.Part image);

    @POST("api/pariwisata/deskripsi")
    Call<PariwisataJson> addDeskripsiWisata(@Header("Authorization") String authKey,
                                             @Body KontentWisata body);

    @POST("api/pariwisata/fasilitas")
    Call<PariwisataJson> addFasilitasWisata(@Header("Authorization") String authKey,
                                             @Body KontentWisata body);

    @POST("api/pariwisata/fasilitas-gratis")
    Call<PariwisataJson> addFasilitasWisataGratis(@Header("Authorization") String authKey,
                                             @Body KontentWisata body);

    @POST("api/pariwisata/produk")
    Call<PariwisataJson> addProduk(@Header("Authorization") String authKey,
                                             @Body KontentWisata body);

    @POST("api/pariwisata/jam")
    Call<PariwisataJson> addOperasional(@Header("Authorization") String authKey,
                                   @Body KontentWisata body);

    @POST("api/pariwisata/ulasan")
    Call<BaseNetworkCallback> addUlasan(@Header("Authorization") String authKey,
                                   @Body KontentWisata body);

    @Multipart
    @POST("api/pariwisata/galeri")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<BaseNetworkCallback> addGallery(@Header("Authorization") String authKey,
                                                  @PartMap() Map<String, RequestBody> partMap,
                                                  @Part() MultipartBody.Part image);

}
