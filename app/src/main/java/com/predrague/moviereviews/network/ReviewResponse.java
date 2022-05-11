package com.predrague.moviereviews.network;

import com.predrague.moviereviews.data.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewResponse {
    public enum Status {SUCCESS, ERROR, EMPTY}

    private List<Review> reviewList;
    private boolean hasMore;
    private Status status;

    public ReviewResponse() {
        reviewList = new ArrayList<>();
        hasMore = false;
        status = Status.EMPTY;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public Status getStatus() {
        return status;
    }
}
