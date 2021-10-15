package base.service.information;
import base.service.RetrofitClient;
import base.service.URL;

public class InformationUtils {

    public static InformationEndpoint getInformation() {
        return RetrofitClient.getClient(URL.checkUrl()).create(InformationEndpoint.class);
    }
}
