
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
    private Integer isHidden;
    @SerializedName("is_reported")
    @Expose
    private Integer isReported;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("object")
    @Expose
    private Object object;

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

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getIsReported() {
        return isReported;
    }

    public void setIsReported(Integer isReported) {
        this.isReported = isReported;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
