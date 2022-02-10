package com.predrague.moviereviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.predrague.moviereviews.data.ReviewsRepository;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.util.network.NetworkConnectionUtil;
import com.predrague.moviereviews.util.network.NetworkStateManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    NetworkConnectionUtil networkConnectionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkConnectionUtil = new NetworkConnectionUtil(getApplicationContext());
        networkConnectionUtil.getInitialState();
        networkConnectionUtil.registerNetworkCallbackEvents();

        ReviewsRepository repository = ReviewsRepository.getInstance();

        if(Boolean.TRUE.equals(NetworkStateManager.getInstance().getNetworkConnectivityStatus().getValue())) {
            repository.getReviews(BuildConfig.API_KEY).observe(this, new Observer<List<Review>>() {
                @Override
                public void onChanged(List<Review> reviews) {
                    Log.i(TAG, "onChanged: " + reviews.toString());
                }
            });
        }

        NetworkStateManager.getInstance().getNetworkConnectivityStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // TODO make UI for network connection status
                Toast.makeText(getBaseContext(), Boolean.TRUE.equals(aBoolean) ? "There is internet connection \\o/" : "There is no internet connection :( ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkConnectionUtil.unregisterNetworkCallbackEvents();
    }
}