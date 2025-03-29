package com.nomaddeveloper.example.securai.network.request;

import androidx.annotation.NonNull;

import com.nomaddeveloper.example.securai.network.model.PostRequest;
import com.nomaddeveloper.example.securai.network.model.PostResponse;
import com.nomaddeveloper.example.securai.network.retrofit.RetrofitBuilder;
import com.nomaddeveloper.example.securai.service.TestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestRequest {
    private static final TestService service = RetrofitBuilder.createTestService();

    public static void mockPostRequest() {
        final PostRequest request = new PostRequest("Test Title", "<script>alert('XSS')</script>", 1);
        final String authToken = "Bearer 12345";
        final String cookie = "Cookie 123";
        final String userId = "user123";
        final String userId2 = "testUser";

        final Call<PostResponse> call = service.submitData(request, authToken, cookie, userId, userId2);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull final Call<PostResponse> call, @NonNull final Response<PostResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    System.out.println("Response: " + response.body().getTitle());
                } else {
                    System.err.println("Request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull final Call<PostResponse> call, @NonNull final Throwable t) {
                System.err.println("Request error: " + t.getMessage());
            }
        });
    }
}
