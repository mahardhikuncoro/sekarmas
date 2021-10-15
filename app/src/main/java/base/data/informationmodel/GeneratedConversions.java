
package base.data.informationmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneratedConversions {

    @SerializedName("thumb")
    @Expose
    private Boolean thumb;
    @SerializedName("original")
    @Expose
    private Boolean original;

    public Boolean getThumb() {
        return thumb;
    }

    public void setThumb(Boolean thumb) {
        this.thumb = thumb;
    }

    public Boolean getOriginal() {
        return original;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

}
