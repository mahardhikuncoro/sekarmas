package base.service;
import base.data.Information;
import base.network.RetrofitClient;
import base.network.URL;

public class InformationUtils {

    public static InformationEndpoint getInformation() {
        return RetrofitClient.getClient(URL.checkUrl()).create(InformationEndpoint.class);
    }
}
