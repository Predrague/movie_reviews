package com.predrague.moviereviews.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.network.ApiResponse;
import com.predrague.moviereviews.network.IReviewsApi;
import com.predrague.moviereviews.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO: Use hasMore field from api response?
public class ReviewsRepository {
    private static ReviewsRepository instance;
    private final IReviewsApi reviewsApi;
    private final MutableLiveData<List<Review>> reviews;
    private int offset = 0;

    private ReviewsRepository() {
        reviewsApi = RetrofitClientInstance.getRetrofitInstance().create(IReviewsApi.class);
        reviews = new MutableLiveData<>();
        reviews.setValue(new ArrayList<>());
    }

    public static synchronized ReviewsRepository getInstance() {
        if (instance == null) {
            instance = new ReviewsRepository();
        }

        return instance;
    }

    // TODO: Some error handling
    public synchronized MutableLiveData<List<Review>> getReviews(String key) {
        reviewsApi.getReviews(key, offset).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    // Updates current reviews list
                    ArrayList<Review> valueToUpdate = new ArrayList<>();
                    valueToUpdate.addAll(reviews.getValue());
                    valueToUpdate.addAll(response.body().getReviews());
                    reviews.setValue(valueToUpdate);
                    offset += 20;
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                reviews.setValue(null);
            }
        });

        return reviews;
    }

    public int getOffset() {
        return offset;
    }
}
