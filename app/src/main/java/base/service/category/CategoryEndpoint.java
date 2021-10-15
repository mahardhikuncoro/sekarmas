package base.service.category;

import java.util.List;

import base.data.laporan.LaporanJson;
import base.service.category.model.CategoryJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface CategoryEndpoint {

    @GET("api/category/get-categories")
    @Headers("X-Requested-With: XMLHttpRequest")
    Call<List<CategoryJson>> getListCategory(@Header("Authorization") String auth);
}
