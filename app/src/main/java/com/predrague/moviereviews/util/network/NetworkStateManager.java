package com.predrague.moviereviews.util.network;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkStateManager {
    private static NetworkStateManager instance;
    private static final MutableLiveData<Boolean> activeNetworkState = new MutableLiveData<>();

    private NetworkStateManager() {
    }

    public static synchronized NetworkStateManager getInstance() {
        if (instance == null) {
            instance = new NetworkStateManager();
        }

        return instance;
    }

    public void setNetworkConnectivityStatus(boolean connectivityStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {      // Checking if we are on UI thread
            activeNetworkState.setValue(connectivityStatus);
        } else {
            activeNetworkState.postValue(connectivityStatus);
        }
    }

    public LiveData<Boolean> getNetworkConnectivityStatus() {
        return activeNetworkState;
    }
}
