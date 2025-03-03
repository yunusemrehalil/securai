package com.nomaddeveloper.example.securai.network.retrofit;

import static com.nomaddeveloper.example.securai.SecuraiApp.getAppContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nomaddeveloper.securai.SecuraiInterceptor;
import com.nomaddeveloper.example.securai.service.TestService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {
    private static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SecuraiInterceptor(getAppContext(), true))
                .addInterceptor(new HttpLoggingInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public static TestService createTestService() {
        return getRetrofit().create(TestService.class);
    }
}
