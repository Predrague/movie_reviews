package com.predrague.moviereviews.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.predrague.moviereviews.data.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse {
    @SerializedName("results")
    @Expose
    private final List<Review> reviews = new ArrayList<>();
    @SerializedName("num_results")
    @Expose
    private int numberOfResults;
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;

    public List<Review> getReviews() {
        return reviews;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public boolean getHasMore() {
        return hasMore;
    }
}
