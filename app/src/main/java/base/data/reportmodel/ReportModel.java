
package base.data.reportmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import base.data.laporan.DataLaporan;

public class ReportModel implements Serializable {

    @SerializedName("object_id")
    @Expose
    private String objectId;
    @SerializedName("object_type")
    @Expose
    private String objectType;
    @SerializedName("kategori_id")
    @Expose
    private Integer kategoriId;
    @SerializedName("is_hidden")
    @Expose
    private Boolean isHidden;
    @SerializedName("is_reported")
    @Expose
    private Boolean isReported;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("object")
    @Expose
    private DataLaporan object;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Integer getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(Integer kategoriId) {
        this.kategoriId = kategoriId;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public Boolean getReported() {
        return isReported;
    }

    public void setReported(Boolean reported) {
        isReported = reported;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public DataLaporan getObject() {
        return object;
    }

    public void setObject(DataLaporan object) {
        this.object = object;
    }
}
