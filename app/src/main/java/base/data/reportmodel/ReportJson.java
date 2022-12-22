
package base.data.reportmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import base.data.sektormodel.SektorModel;

public class ReportJson {

    @SerializedName("data")
    @Expose
    private List<ReportModel> data = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<ReportModel> getData() {
        return data;
    }

    public void setData(List<ReportModel> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
