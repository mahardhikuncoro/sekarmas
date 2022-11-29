
package base.data.pariwisatamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import base.data.umkmmodel.Media;
import base.data.umkmmodel.Produk;

public class KontentWisata {

    @SerializedName("pariwisata_id")
    @Expose
    private Integer pariwisataId;

    @SerializedName("judul")
    @Expose
    private String judul;

    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("harga")
    @Expose
    private String harga;

    @SerializedName("satuan")
    @Expose
    private String satuan;

    @SerializedName("senin")
    @Expose
    private String senin;

    @SerializedName("selasa")
    @Expose
    private String selasa;

    @SerializedName("rabu")
    @Expose
    private String rabu;

    @SerializedName("kamis")
    @Expose
    private String kamis;

    @SerializedName("jumat")
    @Expose
    private String jumat;

    @SerializedName("sabtu")
    @Expose
    private String sabtu;

    @SerializedName("minggu")
    @Expose
    private String minggu;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("rating")
    @Expose
    private Integer rating;

    public Integer getPariwisataId() {
        return pariwisataId;
    }

    public void setPariwisataId(Integer pariwisataId) {
        this.pariwisataId = pariwisataId;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getSenin() {
        return senin;
    }

    public void setSenin(String senin) {
        this.senin = senin;
    }

    public String getSelasa() {
        return selasa;
    }

    public void setSelasa(String selasa) {
        this.selasa = selasa;
    }

    public String getRabu() {
        return rabu;
    }

    public void setRabu(String rabu) {
        this.rabu = rabu;
    }

    public String getKamis() {
        return kamis;
    }

    public void setKamis(String kamis) {
        this.kamis = kamis;
    }

    public String getJumat() {
        return jumat;
    }

    public void setJumat(String jumat) {
        this.jumat = jumat;
    }

    public String getSabtu() {
        return sabtu;
    }

    public void setSabtu(String sabtu) {
        this.sabtu = sabtu;
    }

    public String getMinggu() {
        return minggu;
    }

    public void setMinggu(String minggu) {
        this.minggu = minggu;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
