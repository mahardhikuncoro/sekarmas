package base.network.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wewenang {

    @SerializedName("groupid")
    @Expose
    private String groupid;
    @SerializedName("groupname")
    @Expose
    private String groupname;

    @SerializedName("useridlos")
    @Expose
    private String useridlos;


    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getUseridlos() {
        return useridlos;
    }

    public void setUseridlos(String useridlos) {
        this.useridlos = useridlos;
    }
}
