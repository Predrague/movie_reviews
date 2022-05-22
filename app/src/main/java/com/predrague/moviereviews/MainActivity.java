package com.predrague.moviereviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.databinding.ActivityMainBinding;
import com.predrague.moviereviews.ui.ReviewSearchFragment;
import com.predrague.moviereviews.ui.ReviewsViewModel;
import com.predrague.moviereviews.util.network.NetworkConnectionUtil;
import com.predrague.moviereviews.util.network.NetworkStateManager;

public class MainActivity extends AppCompatActivity {
    NetworkConnectionUtil networkConnectionUtil;
    private ActivityMainBinding binding;
    private ReviewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.appToolbar);

        // View model
        ReviewsRepository reviewsRepository = ReviewsRepository.getInstance();
        viewModel = new ViewModelProvider(this, new ReviewsViewModel.ReviewsViewModelFactory(reviewsRepository)).get(ReviewsViewModel.class);
        viewModel.loadReviews();

        networkConnectionUtil = new NetworkConnectionUtil(getApplicationContext());
        networkConnectionUtil.getInitialState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkConnectionUtil.registerNetworkCallbackEvents();

        NetworkStateManager.getInstance().getNetworkConnectivityStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean)
                    binding.connectionIndicator.setVisibility(View.VISIBLE);
                else binding.connectionIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkConnectionUtil.unregisterNetworkCallbackEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Search functionality
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_for_reviews));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                viewModel.setSearchQuery(s);
                viewModel.searchForReviews();

                // If search fragment is not already open, navigate to it.
                if (!viewModel.isSearchInProgress()) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.setReorderingAllowed(true);
                    transaction.replace(R.id.main_fragment_container, ReviewSearchFragment.class, null, ReviewSearchFragment.TAG);
                    transaction.commit();
                }

                return true;
            }

            // TODO: Live search?
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }
}