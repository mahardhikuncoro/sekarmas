package base.network.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PostJson extends BaseJson{


    @SerializedName("category_id")
    @Expose
    private String categoryId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("kabupaten_kota")
    @Expose
    private String kabupoatenKota;

    @SerializedName("is_public")
    @Expose
    private Integer isPublic;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("media")
    @Expose
    private ArrayList<MediaJson> media;


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getKabupoatenKota() {
        return kabupoatenKota;
    }

    public void setKabupoatenKota(String kabupoatenKota) {
        this.kabupoatenKota = kabupoatenKota;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<MediaJson> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<MediaJson> media) {
        this.media = media;
    }
}
