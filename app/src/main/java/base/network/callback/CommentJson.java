package base.network.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentJson extends BaseJson {

    @SerializedName("post_id")
    @Expose
    private String postId;

    @SerializedName("comment")
    @Expose
    private String comment;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
