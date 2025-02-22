package com.nomaddeveloper.securai.network.retrofit;

import com.nomaddeveloper.securai.SecuraiInterceptor;
import com.nomaddeveloper.securai.service.TestService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {
    private static Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SecuraiInterceptor())
                .addInterceptor(new HttpLoggingInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static TestService createTestService() {
        return getRetrofit().create(TestService.class);
    }
}
