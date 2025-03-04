package com.nomaddeveloper.example.securai.service;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.internal.model.Field;
import com.nomaddeveloper.example.securai.network.model.PostRequest;
import com.nomaddeveloper.example.securai.network.model.PostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TestService {
    @Secured(fields = {Field.ALL})
    @POST("posts")
    Call<PostResponse> submitData(
            @Body PostRequest data,
            @Header(value = "Authorization") String authToken,
            @Header(value = "Cookie") String cookie,
            @Query(value = "userId") String userId,
            @Query(value = "userId2") String userId2);
}
