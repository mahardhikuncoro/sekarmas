package base.service.comment;
import base.service.RetrofitClient;
import base.service.URL;
import base.service.information.InformationEndpoint;

public class CommentUtils {

    public static CommentEndpoint getCommentUrl() {
        return RetrofitClient.getClient(URL.checkUrl()).create(CommentEndpoint.class);
    }
}
