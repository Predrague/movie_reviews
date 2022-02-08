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

    public List<Review> getReviews() {
        return reviews;
    }
}
