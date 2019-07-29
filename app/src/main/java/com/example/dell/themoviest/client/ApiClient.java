package com.example.dell.themoviest.client;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.themoviedb.org";
    public static String API_KEY = "37e2f7d4b4302a4b13a350d54da32fd9";
    public static String LANGUAGE = "en-US";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w1280";
    private static Retrofit retrofit = null;
    public static Context mContext;

    public static Retrofit getApiClient(Context context)
    {
        if (retrofit == null){

            mContext = context;

            int cacheSize = 10*1024*1024;
            final Cache cache = new Cache(mContext.getCacheDir() , cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            if (!isNetworkAvailable()) {
                                int maxStale = 60 * 60 * 24 * 28; // 4-weeks-stale
                                request = request.newBuilder()
                                        .header("Cache-Control" , "public, only-if-cached, max-stale" + maxStale)
                                        .build();
                            }
                            else {
                                int maxAge = 5; // fresh data
                                request = request.newBuilder()
                                        .header("Cache-Control" , "public, max-age = " + maxAge)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    })
                    .build();

            // network library access point
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
