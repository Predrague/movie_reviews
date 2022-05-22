package com.predrague.moviereviews.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.FragmentSearchListBinding;
import com.predrague.moviereviews.ui.adapters.ReviewSearchRecyclerViewAdapter;

import java.util.List;


public class ReviewSearchFragment extends Fragment {
    public static final String TAG = "ReviewSearchFragment";

    private FragmentSearchListBinding binding;
    private ReviewsViewModel viewModel;

    // Mandatory empty constructor
    public ReviewSearchFragment() {
    }

    public static ReviewSearchFragment newInstance(int columnCount) {
        ReviewSearchFragment fragment = new ReviewSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvSearchList.setLayoutManager(linearLayoutManager);
        ReviewSearchRecyclerViewAdapter adapter = new ReviewSearchRecyclerViewAdapter();
        binding.rvSearchList.setAdapter(adapter);

        // Setting a divider for RecyclerView items
        try {
            binding.rvSearchList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        } catch (IllegalStateException e) {
            Log.e(TAG, "onViewCreated: ", e);
        }

        // Data part
        ReviewsRepository repository = ReviewsRepository.getInstance();
        // Idea is to share view model with other fragments (single review item fragment for example).
        viewModel = new ViewModelProvider(requireActivity(), new ReviewsViewModel.ReviewsViewModelFactory(repository)).get(ReviewsViewModel.class);
        viewModel.getReviewListSearchLiveData().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                Log.i(TAG, "onChanged: " + reviews.toString());
                adapter.updateLocalDataSet(reviews);
            }
        });

        // Progress bar - loading indicator
        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                int visibility = (Boolean.TRUE.equals(loading) ? View.VISIBLE : View.INVISIBLE);
                binding.progressBarSearch.setVisibility(visibility);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.setSearchInProgress(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.setSearchInProgress(false);
    }
}