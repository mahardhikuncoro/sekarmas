package base.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseNetworkCallback {
    private String status,message, regno, docid/*, field*/;
    @SerializedName("success")
    @Expose
    private Boolean success;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String
    getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /* public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }*/
}
