package base.service.comment;

import java.util.List;

import base.data.commentmodel.CommentJson;
import base.data.informationmodel.Information;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentEndpoint {

    @GET("api/post/show/{id_comment}")
    Call<CommentJson> getDetailComment(@Header("Authorization") String auth,
                                       @Path("id_comment") String id_comment);

    @POST("api/post/addComment")
    Call<CommentJson> postComment(@Header("Authorization") String auth,
                                       @Query("post_id") String post_id,
                                       @Query("comment") String comment);


}
