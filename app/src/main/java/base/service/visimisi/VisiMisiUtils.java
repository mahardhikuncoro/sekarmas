package base.service.visimisi;

import base.service.RetrofitClient;
import base.service.URL;

public class VisiMisiUtils {

    public static VisiMisiEndpoint getVisiMisi() {
        return RetrofitClient.getClient(URL.checkUrl()).create(VisiMisiEndpoint.class);
    }
}
