package com.predrague.moviereviews.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.predrague.moviereviews.R;
import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.FragmentSearchListBinding;
import com.predrague.moviereviews.network.ReviewResponse;
import com.predrague.moviereviews.ui.adapters.ReviewSearchRecyclerViewAdapter;
import com.predrague.moviereviews.util.network.NetworkStateManager;

import java.util.List;


public class ReviewSearchFragment extends Fragment implements ReviewSearchRecyclerViewAdapter.IReviewSearchListClickListener {
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
        ReviewSearchRecyclerViewAdapter adapter = new ReviewSearchRecyclerViewAdapter(this);
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

        // More reviews are loaded when list is scrolled to the end
        // Up to 100 reviews
        binding.rvSearchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvSearchList.canScrollVertically(1) && !viewModel.getLoadingLiveData().getValue()) {
                    viewModel.getMoreSearchResults();
                }
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

        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), new Observer<ReviewResponse.Status>() {
            @Override
            public void onChanged(ReviewResponse.Status status) {
                if (status == ReviewResponse.Status.EMPTY) {
                    binding.rvSearchList.setVisibility(View.GONE);
                    binding.emptyListView.setVisibility(View.VISIBLE);
                } else if (status == ReviewResponse.Status.SUCCESS) {
                    binding.rvSearchList.setVisibility(View.VISIBLE);
                    binding.emptyListView.setVisibility(View.GONE);
                } else if (status == ReviewResponse.Status.ERROR) {
                    Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onItemClick(int position) {
        try {
            if (NetworkStateManager.getInstance().getNetworkConnectivityStatus().getValue() == false) {
                return;
            }

            Review review = viewModel.getReviewFromSearch(position);

            FragmentManager fragmentManager = getParentFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(ReviewDetailFragment.ARG_URL, review.getLink().getUrl());

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.setReorderingAllowed(true);
            transaction.replace(R.id.main_fragment_container, ReviewDetailFragment.class, bundle, ReviewDetailFragment.TAG);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "onReviewItemClick: ", e);
            Toast.makeText(getContext(), getResources().getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }
}