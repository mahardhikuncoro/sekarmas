
package base.data.umkmmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import base.location.BaseNetworkCallback;

public class UmkmCallback extends BaseNetworkCallback {

    private Integer data;

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
