package com.predrague.moviereviews.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class NetworkConnectionUtil extends ConnectivityManager.NetworkCallback {
    private final NetworkRequest networkRequest;
    private final ConnectivityManager connectivityManager;
    private final NetworkStateManager networkStateManager;

    public NetworkConnectionUtil(Context context) {
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkStateManager = NetworkStateManager.getInstance();
    }

    public void registerNetworkCallbackEvents() {
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    public void unregisterNetworkCallbackEvents() {
        connectivityManager.unregisterNetworkCallback(this);
    }

    public void getInitialState() {
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            networkStateManager.setNetworkConnectivityStatus(networkInfo != null && networkInfo.isConnected());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        networkStateManager.setNetworkConnectivityStatus(true);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        networkStateManager.setNetworkConnectivityStatus(false);
    }
}
