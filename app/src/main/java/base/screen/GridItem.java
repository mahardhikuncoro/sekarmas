package base.screen;

import android.graphics.Bitmap;
import android.net.Uri;

import okhttp3.MultipartBody;

public class
GridItem {
    private String image;
    private Integer id;
    private String desc;
    private Bitmap bitmapImage;
    private Uri uri;
    private MultipartBody.Part fileToUpload;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public MultipartBody.Part getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(MultipartBody.Part fileToUpload) {
        this.fileToUpload = fileToUpload;
    }
}
