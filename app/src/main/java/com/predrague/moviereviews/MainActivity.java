package com.predrague.moviereviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.predrague.moviereviews.util.network.NetworkConnectionUtil;
import com.predrague.moviereviews.util.network.NetworkStateManager;

public class MainActivity extends AppCompatActivity {
    NetworkConnectionUtil networkConnectionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getBaseContext().getString(R.string.search_for_reviews));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // TODO: Search API
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // TODO: Local search
                return false;
            }
        });

        return true;
    }
}