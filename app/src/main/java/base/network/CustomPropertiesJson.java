package base.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomPropertiesJson {

    @SerializedName("generated_conversions")
    @Expose
    private GeneratedConversionJson generatedConversions;


    public GeneratedConversionJson getGeneratedConversions() {
        return generatedConversions;
    }

    public void setGeneratedConversions(GeneratedConversionJson generatedConversions) {
        this.generatedConversions = generatedConversions;
    }
}
