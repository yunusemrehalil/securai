package com.nomaddeveloper.securai.network.request;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.network.model.PostRequest;
import com.nomaddeveloper.securai.network.model.PostResponse;
import com.nomaddeveloper.securai.network.retrofit.RetrofitBuilder;
import com.nomaddeveloper.securai.service.TestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestRequest {
    private static final TestService service = RetrofitBuilder.createTestService();

    public static void mockPostRequest() {
        PostRequest request = new PostRequest("Test Title", "<script>alert('XSS')</script>", 1);
        String authToken = "Bearer 12345";
        String cookie = "Cookie 123";
        String userId = "user123";
        String userId2 = "testUser";

        Call<PostResponse> call = service.submitData(request, authToken, cookie, userId, userId2);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    System.out.println("Response: " + response.body().getTitle());
                } else {
                    System.err.println("Request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                System.err.println("Request error: " + t.getMessage());
            }
        });
    }
}
