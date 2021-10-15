
package base.data.laporan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomProperties__1 {

    @SerializedName("generated_conversions")
    @Expose
    private GeneratedConversions__1 generatedConversions;

    public GeneratedConversions__1 getGeneratedConversions() {
        return generatedConversions;
    }

    public void setGeneratedConversions(GeneratedConversions__1 generatedConversions) {
        this.generatedConversions = generatedConversions;
    }

}
