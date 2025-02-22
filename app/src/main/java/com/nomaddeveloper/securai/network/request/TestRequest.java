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
        PostRequest request = new PostRequest("Test Title", "This is a mock body", 1);
        String authToken = "Bearer 12345";
        String userId = "user123";

        Call<PostResponse> call = service.submitData(request, authToken, userId);
        call.enqueue(new Callback<PostResponse>() {
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
