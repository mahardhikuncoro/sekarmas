package base.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaJson {

    @SerializedName("model_type")
    @Expose
    private String modelType;

    @SerializedName("model_id")
    @Expose
    private String modelId;

    @SerializedName("collection_name")
    @Expose
    private String collectionName;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("file_name")
    @Expose
    private String fileName;

    @SerializedName("mime_type")
    @Expose
    private String mimeType;

    @SerializedName("disk")
    @Expose
    private String disk;

    @SerializedName("size")
    @Expose
    private Integer size;

    @SerializedName("order_column")
    @Expose
    private Integer orderColumn;

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(Integer orderColumn) {
        this.orderColumn = orderColumn;
    }
}
