package com.predrague.moviereviews.network;

import com.predrague.moviereviews.MovieReviews;
import com.predrague.moviereviews.util.network.NetworkStateManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;

    private static final String BASE_URL = "https://api.nytimes.com/svc/movies/v2/";

    private RetrofitClientInstance() {
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(buildOfflineInterceptor())
                .addNetworkInterceptor(buildOnlineInterceptor())
                .cache(MovieReviews.cache)
                .build();
    }

    private static Interceptor buildOnlineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                int maxAge = 60; // Read from cache for one minute.
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            }
        };
    }

    private static Interceptor buildOfflineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (Boolean.FALSE.equals(NetworkStateManager.getInstance().getNetworkConnectivityStatus().getValue())) {
                    int maxStale = 60 * 60 * 24 * 30; // Offline cache available for 30 days.
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }
}
