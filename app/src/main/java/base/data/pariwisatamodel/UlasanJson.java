
package base.data.pariwisatamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import base.data.laporan.Medium;
import base.location.BaseNetworkCallback;

public class UlasanJson extends BaseNetworkCallback {

    @SerializedName("rating")
    @Expose
    private Integer rating;

    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;

    @SerializedName("media")
    @Expose
    private List<Medium> media = null;
    @SerializedName("foto")
    @Expose
    private List<Medium> foto = null;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }

    public List<Medium> getFoto() {
        return foto;
    }

    public void setFoto(List<Medium> foto) {
        this.foto = foto;
    }
}
