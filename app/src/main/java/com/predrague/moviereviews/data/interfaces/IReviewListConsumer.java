package com.predrague.moviereviews.data.interfaces;

import com.predrague.moviereviews.network.ReviewResponse;

public interface IReviewListConsumer {
    void consumeReviewListResponse(ReviewResponse response);
}
