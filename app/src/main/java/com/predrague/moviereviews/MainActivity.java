package com.predrague.moviereviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.widget.Toast;

import com.predrague.moviereviews.databinding.ActivityMainBinding;
import com.predrague.moviereviews.util.network.NetworkConnectionUtil;
import com.predrague.moviereviews.util.network.NetworkStateManager;

public class MainActivity extends AppCompatActivity {
    NetworkConnectionUtil networkConnectionUtil;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.appToolbar);

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