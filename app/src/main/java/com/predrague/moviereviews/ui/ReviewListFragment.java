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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.predrague.moviereviews.MainActivity;
import com.predrague.moviereviews.R;
import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.FragmentReviewListBinding;
import com.predrague.moviereviews.ui.adapters.ReviewListAdapter;
import com.predrague.moviereviews.network.ReviewResponse;

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
        setHasOptionsMenu(true);
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

        // More reviews are loaded when list is scrolled to the end
        // Up to 100 reviews
        // TODO: Loading indicator
        binding.rvReviewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvReviewsList.canScrollVertically(1) && !viewModel.getLoadingLiveData().getValue()) {
                    if (viewModel.isSearchInProgress()) {
                        viewModel.getMoreSearchResults();
                    } else {
                        viewModel.getMoreReviews();
                    }
                }
            }
        });

        // Data part
        ReviewsRepository repository = ReviewsRepository.getInstance();
        // Idea is to share view model with other fragments (single review item fragment for example).
        viewModel = new ViewModelProvider(requireActivity(), new ReviewsViewModel.ReviewsViewModelFactory(repository)).get(ReviewsViewModel.class);
        viewModel.loadReviews();
        viewModel.getReviewListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                Log.i(TAG, "onChanged: " + reviews.toString());
                adapter.updateLocalDataSet(reviews);
            }
        });

        // Observe review loading response status and show appropriate messages.
        viewModel.getResponseStatus().observe(getViewLifecycleOwner(), new Observer<ReviewResponse.Status>() {
            @Override
            public void onChanged(ReviewResponse.Status status) {
                if (status == ReviewResponse.Status.EMPTY) {
                    // TODO: An empty list layout?
                } else if (status == ReviewResponse.Status.ERROR) {
                    Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                }
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
            Toast.makeText(getContext(), getResources().getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_for_reviews));
        
        item.setActionView(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!viewModel.getLoadingLiveData().getValue()) {
                    viewModel.setSearchInProgress(true);
                    viewModel.setSearchQuery(s);
                    viewModel.searchForReviews();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    viewModel.setSearchInProgress(false);
                }
                return true;
            }
        });

        // Had to use this because searchView OnCloseListener is not working properly.
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchView.setQuery("", false);
                viewModel.setSearchQuery("");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                viewModel.setSearchInProgress(false);
                return true;
            }
        });
    }
}
