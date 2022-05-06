
package base.data.sektormodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SektorJson {

    @SerializedName("data")
    @Expose
    private List<SektorModel> data = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<SektorModel> getData() {
        return data;
    }

    public void setData(List<SektorModel> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
