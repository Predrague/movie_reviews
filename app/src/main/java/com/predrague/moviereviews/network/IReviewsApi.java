package com.predrague.moviereviews.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IReviewsApi {
    @GET("reviews/all.json")
    Call<ApiResponse> getReviews(@Query("api-key") String apiKey, @Query("offset") int offset);

    @GET("reviews/search.json")
    Call<ApiResponse> searchForReviews(@Query("api-key") String apiKey, @Query("offset") int offset, @Query("query") String query);
}
