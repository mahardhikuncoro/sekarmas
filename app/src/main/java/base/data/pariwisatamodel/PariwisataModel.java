
package base.data.pariwisatamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import base.data.umkmmodel.Media;
import base.data.umkmmodel.Produk;

public class PariwisataModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("kelurahan")
    @Expose
    private String kelurahan;
    @SerializedName("kecamatan")
    @Expose
    private String kecamatan;
    @SerializedName("kabupaten")
    @Expose
    private String kabupaten;
    @SerializedName("provinsi")
    @Expose
    private String provinsi;
    @SerializedName("kodepos")
    @Expose
    private String kodepos;
    @SerializedName("telepon")
    @Expose
    private String telepon;
    @SerializedName("handphone")
    @Expose
    private Object handphone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("kategori_id")
    @Expose
    private Integer kategoriId;
    @SerializedName("produk_utama")
    @Expose
    private Object produkUtama;
    @SerializedName("jumlah_sdm")
    @Expose
    private Object jumlahSdm;
    @SerializedName("sistem_penjualan")
    @Expose
    private Object sistemPenjualan;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("produk")
    @Expose
    private List<KontentWisata> produk = null;
    @SerializedName("media")
    @Expose
    private List<Media> media = null;

    @SerializedName("deskripsi_model")
    @Expose
    private List<KontentWisata> deskripsiModel = new ArrayList<>();

    @SerializedName("fasilitas")
    @Expose
    private List<KontentWisata> fasilitas = new ArrayList<>();

    @SerializedName("fasilitas_gratis")
    @Expose
    private List<KontentWisata> fasilitasGratis = new ArrayList<>();

//    @SerializedName("galeri")
//    @Expose
//    private List<KontentWisata> galeri = new ArrayList<>();

    @SerializedName("jam")
    @Expose
    private List<KontentWisata> jam = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public Object getHandphone() {
        return handphone;
    }

    public void setHandphone(Object handphone) {
        this.handphone = handphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(Integer kategoriId) {
        this.kategoriId = kategoriId;
    }

    public Object getProdukUtama() {
        return produkUtama;
    }

    public void setProdukUtama(Object produkUtama) {
        this.produkUtama = produkUtama;
    }

    public Object getJumlahSdm() {
        return jumlahSdm;
    }

    public void setJumlahSdm(Object jumlahSdm) {
        this.jumlahSdm = jumlahSdm;
    }

    public Object getSistemPenjualan() {
        return sistemPenjualan;
    }

    public void setSistemPenjualan(Object sistemPenjualan) {
        this.sistemPenjualan = sistemPenjualan;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<KontentWisata> getProduk() {
        return produk;
    }

    public void setProduk(List<KontentWisata> produk) {
        this.produk = produk;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<KontentWisata> getDeskripsiModel() {
        return deskripsiModel;
    }

    public void setDeskripsiModel(List<KontentWisata> deskripsiModel) {
        this.deskripsiModel = deskripsiModel;
    }

    public List<KontentWisata> getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(List<KontentWisata> fasilitas) {
        this.fasilitas = fasilitas;
    }

    public List<KontentWisata> getFasilitasGratis() {
        return fasilitasGratis;
    }

    public void setFasilitasGratis(List<KontentWisata> fasilitasGratis) {
        this.fasilitasGratis = fasilitasGratis;
    }

    public List<KontentWisata> getJam() {
        return jam;
    }

    public void setJam(List<KontentWisata> jam) {
        this.jam = jam;
    }

//    public List<KontentWisata> getGaleri() {
//        return galeri;
//    }
//
//    public void setGaleri(List<KontentWisata> galeri) {
//        this.galeri = galeri;
//    }
}
