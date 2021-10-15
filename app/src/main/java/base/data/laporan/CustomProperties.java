
package base.data.laporan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomProperties {

    @SerializedName("generated_conversions")
    @Expose
    private GeneratedConversions generatedConversions;

    public GeneratedConversions getGeneratedConversions() {
        return generatedConversions;
    }

    public void setGeneratedConversions(GeneratedConversions generatedConversions) {
        this.generatedConversions = generatedConversions;
    }

}
