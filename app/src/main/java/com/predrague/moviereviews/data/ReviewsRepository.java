package com.predrague.moviereviews.data;

import androidx.lifecycle.MutableLiveData;

import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.network.ApiResponse;
import com.predrague.moviereviews.network.IReviewsApi;
import com.predrague.moviereviews.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsRepository {
    private static ReviewsRepository instance;
    private final IReviewsApi reviewsApi;
    private final MutableLiveData<List<Review>> reviews;

    private ReviewsRepository() {
        reviewsApi = RetrofitClientInstance.getRetrofitInstance().create(IReviewsApi.class);
        reviews = new MutableLiveData<>();
    }

    public static synchronized ReviewsRepository getInstance() {
        if (instance == null) {
            instance = new ReviewsRepository();
        }

        return instance;
    }

    public synchronized MutableLiveData<List<Review>> getReviews(String key) {
        reviewsApi.getReviews(key).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    reviews.setValue(response.body() != null ? response.body().getReviews() : null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                reviews.setValue(null);
            }
        });

        return reviews;
    }
}
