package com.nomaddeveloper.securai.service;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.model.Field;
import com.nomaddeveloper.securai.network.model.PostRequest;
import com.nomaddeveloper.securai.network.model.PostResponse;

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
            @Header("Authorization") String authToken,
            @Query("userId") String userId);
}
