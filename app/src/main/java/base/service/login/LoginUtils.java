package base.service.login;

import base.service.RetrofitClient;
import base.service.URL;

public class LoginUtils {

    public static EndpointLogin getLogin() {
        return RetrofitClient.getClient(URL.checkUrl()).create(EndpointLogin.class);
    }
}
