package com.predrague.moviereviews.data.interfaces;

import com.predrague.moviereviews.network.ReviewResponse;

public interface ISearchConsumer {
    void consumeSearchResponse(ReviewResponse response);
}
