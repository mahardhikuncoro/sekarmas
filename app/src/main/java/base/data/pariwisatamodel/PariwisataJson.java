
package base.data.pariwisatamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import base.data.umkmmodel.UmkmModel;
import base.location.BaseNetworkCallback;

public class PariwisataJson extends BaseNetworkCallback {

    @SerializedName("data")
    @Expose
    private List<PariwisataModel> data = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<PariwisataModel> getData() {
        return data;
    }

    public void setData(List<PariwisataModel> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
