package com.predrague.moviereviews;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;

import java.util.List;


public class ReviewsViewModel extends ViewModel {
    private final ReviewsRepository repository;
    private MutableLiveData<List<Review>> reviewList = new MutableLiveData<>();

    public ReviewsViewModel(ReviewsRepository repository) {
        this.repository = repository;
    }

    public void loadReviews() {
        reviewList = repository.getReviews(BuildConfig.API_KEY);
    }

    public LiveData<List<Review>> getReviewList() {
        return reviewList;
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
