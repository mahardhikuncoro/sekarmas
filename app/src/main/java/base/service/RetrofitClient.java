package base.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

   private static Retrofit retrofit = null;
   private static OkHttpClient okHttpClient = null;

   public static Retrofit getClient(String baseUrl) {

      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);


      OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
      httpClient.addInterceptor(logging);
      httpClient.connectTimeout(10, TimeUnit.SECONDS);
      httpClient.writeTimeout(10, TimeUnit.SECONDS);
      httpClient.readTimeout(30, TimeUnit.SECONDS);
      Gson gson = new GsonBuilder().setLenient().create();

      if (retrofit == null) {
         retrofit = new Retrofit.Builder()
                 .baseUrl(baseUrl)
                 .addConverterFactory(GsonConverterFactory.create())
                 .addConverterFactory(GsonConverterFactory.create(gson))
                 .client(httpClient.build())
                 .build();
      }
      return retrofit;
   }
}