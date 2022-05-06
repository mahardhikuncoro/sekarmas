
package base.data.umkmmodel.umkmlist;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import base.data.umkmmodel.UmkmModel;

public class UmkmListJson {

    @SerializedName("data")
    @Expose
    private List<UmkmModel> data = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<UmkmModel> getData() {
        return data;
    }

    public void setData(List<UmkmModel> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
