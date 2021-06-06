
package base.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("visi")
    @Expose
    private List<Visi> visi = null;
    @SerializedName("misi")
    @Expose
    private List<Misi> misi = null;

    public List<Visi> getVisi() {
        return visi;
    }

    public void setVisi(List<Visi> visi) {
        this.visi = visi;
    }

    public List<Misi> getMisi() {
        return misi;
    }

    public void setMisi(List<Misi> misi) {
        this.misi = misi;
    }

}
