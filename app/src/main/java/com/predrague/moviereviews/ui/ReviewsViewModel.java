package com.predrague.moviereviews.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.predrague.moviereviews.BuildConfig;
import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.interfaces.IReviewListConsumer;
import com.predrague.moviereviews.data.interfaces.ISearchConsumer;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.network.ReviewResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ReviewsViewModel extends ViewModel implements IReviewListConsumer, ISearchConsumer {
    private final ReviewsRepository repository;
    private final MutableLiveData<List<Review>> reviewListLiveData = new MutableLiveData<>();

    // Review list
    private final List<Review> reviews = new ArrayList<>();
    private int offset = 0;

    // Search reviews list
    private final List<Review> reviewsSearch = new ArrayList<>();
    private int searchOffset = 0;
    private String searchQuery = "";

    private boolean searchInProgress = false;

    // Latest response status, useful for UI changes
    private final MutableLiveData<ReviewResponse.Status> responseStatus = new MutableLiveData<>(ReviewResponse.Status.EMPTY);
    
    // Loading LiveData tells us if loading is currently in progress.
    // TODO: Try to make fragment use this to show loading indicator. If it is not useful it can be replaced with simple boolean.
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    public ReviewsViewModel(ReviewsRepository repository) {
        this.repository = repository;
    }

    public void loadReviews() {
        offset = 0;
        loadingLiveData.setValue(true);
        repository.getReviews(BuildConfig.API_KEY, offset, this);
    }

    public void getMoreReviews() {
        if (offset > 100) return;
        loadingLiveData.setValue(true);
        repository.getReviews(BuildConfig.API_KEY, offset, this);
    }

    public void searchForReviews() {
        searchOffset = 0;
        reviewsSearch.clear();
        loadingLiveData.setValue(true);
        repository.searchForReviews(BuildConfig.API_KEY, searchOffset, searchQuery, this);
    }

    public void getMoreSearchResults() {
        if (searchOffset > 100) return;
        loadingLiveData.setValue(true);
        repository.searchForReviews(BuildConfig.API_KEY, searchOffset, searchQuery, this);
    }

    public LiveData<List<Review>> getReviewListLiveData() {
        return reviewListLiveData;
    }

    public Review getReview(int position) throws NullPointerException {
        return Objects.requireNonNull(reviewListLiveData.getValue()).get(position);
    }

    public void removeItem(int position) {
        reviews.remove(position);
        reviewListLiveData.setValue(reviews);
    }

    public boolean isSearchInProgress() {
        return searchInProgress;
    }

    public void setSearchInProgress(boolean searchInProgress) {
        this.searchInProgress = searchInProgress;
        if (!this.searchInProgress) {
            reviewListLiveData.setValue(reviews);
        }
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public MutableLiveData<ReviewResponse.Status> getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void consumeReviewListResponse(ReviewResponse response) {
        responseStatus.setValue(response.getStatus());
        if (response.getStatus() == ReviewResponse.Status.SUCCESS) {
            reviews.addAll(response.getReviewList());
            reviewListLiveData.setValue(reviews);
            offset += 20;
        }
        loadingLiveData.setValue(false);
    }

    @Override
    public void consumeSearchResponse(ReviewResponse response) {
        responseStatus.setValue(response.getStatus());
        if (response.getStatus() == ReviewResponse.Status.SUCCESS) {
            reviewsSearch.addAll(response.getReviewList());
            reviewListLiveData.setValue(reviewsSearch);
            if (response.isHasMore()) {
                searchOffset += 20;
            } else searchOffset += 101;     // This way search for more results will not be requested anymore.
        }
        loadingLiveData.setValue(false);
    }

    // Factory used to provide repository instance to ViewModel
    public static class ReviewsViewModelFactory implements ViewModelProvider.Factory {
        private final ReviewsRepository repository;

        public ReviewsViewModelFactory(ReviewsRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ReviewsViewModel(repository);
        }
    }
}
