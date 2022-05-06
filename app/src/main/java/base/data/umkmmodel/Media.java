
package base.data.umkmmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Media {
    @SerializedName("id")
    @Expose
    private Integer id;
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
    @SerializedName("manipulations")
    @Expose
    private List<Object> manipulations = null;
    @SerializedName("custom_properties")
    @Expose
    private CustomProperties customProperties;
    @SerializedName("responsive_images")
    @Expose
    private List<Object> responsiveImages = null;
    @SerializedName("order_column")
    @Expose
    private Integer orderColumn;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


}
