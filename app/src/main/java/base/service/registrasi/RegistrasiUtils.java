package base.service.registrasi;

import base.service.RetrofitClient;
import base.service.URL;
import base.service.laporan.LaporanEndpoint;

/**
 * Created by Mahardhi Kuncoro on 5/23/2022
 */
public class RegistrasiUtils {
    public static RegistrasiEndpoint registrasiUser() {
        return RetrofitClient.getClient(URL.checkUrl()).create(RegistrasiEndpoint.class);
    }
}
