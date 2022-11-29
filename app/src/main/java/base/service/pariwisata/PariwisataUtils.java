package base.service.pariwisata;
import base.service.RetrofitClient;
import base.service.URL;
import base.service.information.InformationEndpoint;

public class PariwisataUtils {

    public static PariwisataEndpoint getPariwisata() {
        return RetrofitClient.getClient(URL.checkUrl()).create(PariwisataEndpoint.class);
    }
}
