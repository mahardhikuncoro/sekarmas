package base.service.report;

import base.service.RetrofitClient;
import base.service.URL;
import base.service.laporan.LaporanEndpoint;

public class ReportUtils {

    public static ReportEndpoint registrasi() {
        return RetrofitClient.getClient(URL.checkUrl()).create(ReportEndpoint.class);
    }
}
