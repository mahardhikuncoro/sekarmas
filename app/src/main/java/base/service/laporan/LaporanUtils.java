package base.service.laporan;

import base.service.RetrofitClient;
import base.service.URL;

public class LaporanUtils {

    public static LaporanEndpoint registrasi() {
        return RetrofitClient.getClient(URL.checkUrl()).create(LaporanEndpoint.class);
    }
}
