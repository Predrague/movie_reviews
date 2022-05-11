package com.predrague.moviereviews.data;

import androidx.annotation.NonNull;

import com.predrague.moviereviews.network.ApiResponse;
import com.predrague.moviereviews.network.IReviewsApi;
import com.predrague.moviereviews.network.RetrofitClientInstance;
import com.predrague.moviereviews.data.interfaces.IReviewListConsumer;
import com.predrague.moviereviews.data.interfaces.ISearchConsumer;
import com.predrague.moviereviews.network.ReviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewsRepository {
    private static ReviewsRepository instance;
    private final IReviewsApi reviewsApi;

    private ReviewsRepository() {
        reviewsApi = RetrofitClientInstance.getRetrofitInstance().create(IReviewsApi.class);
    }

    public static synchronized ReviewsRepository getInstance() {
        if (instance == null) {
            instance = new ReviewsRepository();
        }

        return instance;
    }

    public synchronized void getReviews(String key, int offset, IReviewListConsumer consumer) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewsApi.getReviews(key, offset).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getNumberOfResults() != 0) {
                        reviewResponse.setStatus(ReviewResponse.Status.SUCCESS);
                        reviewResponse.setHasMore(response.body().getHasMore());
                        reviewResponse.setReviewList(response.body().getReviews());
                    }
                    // If number of results is 0 returned object will have Status.EMPTY by default.
                    // Check ReviewResponse constructor.
                } else {
                    reviewResponse.setStatus(ReviewResponse.Status.ERROR);
                }
                consumer.consumeReviewListResponse(reviewResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                reviewResponse.setStatus(ReviewResponse.Status.ERROR);
                consumer.consumeReviewListResponse(reviewResponse);
            }
        });
    }

    public synchronized void searchForReviews(String key, int offset, String query, ISearchConsumer consumer) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewsApi.searchForReviews(key, offset, query).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getNumberOfResults() != 0) {
                        reviewResponse.setStatus(ReviewResponse.Status.SUCCESS);
                        reviewResponse.setHasMore(response.body().getHasMore());
                        reviewResponse.setReviewList(response.body().getReviews());
                    }
                    // If number of results is 0 returned object will have Status.EMPTY by default.
                    // Check ReviewResponse constructor.
                } else {
                    reviewResponse.setStatus(ReviewResponse.Status.ERROR);
                }
                consumer.consumeSearchResponse(reviewResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                reviewResponse.setStatus(ReviewResponse.Status.ERROR);
                consumer.consumeSearchResponse(reviewResponse);
            }
        });
    }
}
