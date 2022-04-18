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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.predrague.moviereviews.R;
import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.FragmentReviewListBinding;
import com.predrague.moviereviews.ui.adapters.ReviewListAdapter;

import java.util.List;

public class ReviewListFragment extends Fragment implements ReviewListAdapter.OnReviewItemClickListener {
    private static final String TAG = "ReviewListFragment";
    private FragmentReviewListBinding binding;
    private ReviewsViewModel viewModel;

    public ReviewListFragment() {
        // Required empty public constructor
    }

    public static ReviewListFragment newInstance() {
        ReviewListFragment fragment = new ReviewListFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentReviewListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvReviewsList.setLayoutManager(linearLayoutManager);
        ReviewListAdapter adapter = new ReviewListAdapter(getContext(), this);
        binding.rvReviewsList.setAdapter(adapter);

        // Setting a divider for RecyclerView items
        try {
            binding.rvReviewsList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        } catch (IllegalStateException e) {
            Log.e(TAG, "onViewCreated: ", e);
        }

        // Data part
        ReviewsRepository repository = ReviewsRepository.getInstance();
        // Idea is to share view model with other fragments (single review item fragment for example).
        viewModel = new ViewModelProvider(requireActivity(), new ReviewsViewModel.ReviewsViewModelFactory(repository)).get(ReviewsViewModel.class);
        viewModel.loadReviews();
        viewModel.getReviewList().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                Log.i(TAG, "onChanged: " + reviews.toString());
                adapter.updateLocalDataSet(reviews);
            }
        });
    }

    // ReviewClickListener interface
    @Override
    public void onReviewItemClick(int position) {
        try {
            Review review = viewModel.getReview(position);

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
            Toast.makeText(getContext(), getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }
}