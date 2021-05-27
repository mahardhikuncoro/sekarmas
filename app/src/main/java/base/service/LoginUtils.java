package base.service;
import base.network.RetrofitClient;
import base.network.URL;

public class LoginUtils {

    public static EndpointLogin getLogin() {
        return RetrofitClient.getClient(URL.checkUrl()).create(EndpointLogin.class);
    }
}
