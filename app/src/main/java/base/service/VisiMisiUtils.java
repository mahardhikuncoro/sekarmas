package base.service;
import base.network.RetrofitClient;
import base.network.URL;

public class VisiMisiUtils {

    public static VisiMisiEndpoint getVisiMisi() {
        return RetrofitClient.getClient(URL.checkUrl()).create(VisiMisiEndpoint.class);
    }
}
