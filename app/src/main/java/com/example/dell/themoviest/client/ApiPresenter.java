package com.example.dell.themoviest.client;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.dell.themoviest.helpers.ApiMovieDetailsHelper;
import com.example.dell.themoviest.helpers.ApiMovieTrailersHelper;
import com.example.dell.themoviest.helpers.ApiMoviesHelper;
import com.example.dell.themoviest.model.DataResponse;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;
import com.example.dell.themoviest.model.MovieTrailer;
import com.example.dell.themoviest.model.MovieVideosResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiPresenter implements IApiPresenter {

    private ApiMovieDetailsHelper movieDetailsHelper;
    private ApiMoviesHelper moviesHelper;
    private ApiMovieTrailersHelper movieTrailersHelper;
    private Context context;

    public ApiPresenter(ApiMoviesHelper moviesHelper , Context context)
    {
        this.moviesHelper = moviesHelper;
        this.context = context;
    }

    public ApiPresenter(ApiMovieDetailsHelper movieDetailsHelper ,Context context)
    {
        // presenter pridge to reach views throw interface
        this.movieDetailsHelper = movieDetailsHelper;
        this.context = context;
    }

    public ApiPresenter(ApiMoviesHelper moviesHelper, ApiMovieDetailsHelper movieDetailsHelper ,Context context)
    {
        this.moviesHelper = moviesHelper;
        this.movieDetailsHelper = movieDetailsHelper;
        this.context = context;
    }


    public ApiPresenter(ApiMovieTrailersHelper movieTrailersHelper , Context context)
    {
        this.movieTrailersHelper = movieTrailersHelper;
        this.context = context;
    }

    @Override
    public void getMovies(String category , int page) {
        final Call<DataResponse> moviesCall = ApiClient.getApiClient(context)
                .create(ApiService.class).getMovies(
                        category ,
                        ApiClient.API_KEY ,
                        ApiClient.LANGUAGE ,
                        page);

        moviesCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful())
                    moviesHelper.setMoviesData((ArrayList<Movie>) response.body().getMovies());
                else
                    moviesHelper.setMoviesData(null);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.e("API Service Presenter >"  , " error in getting data from API");
                Log.e("error message > " , t.getMessage());
            }
        });
    }

    @Override
    public void getMovieDetails(final View itemView, int movieID) {
        final Call<MovieDetails> movieDetailsCall = ApiClient.getApiClient(context)
                .create(ApiService.class).getMovieDetails(
                        movieID ,
                        ApiClient.API_KEY ,
                        ApiClient.LANGUAGE
                );
        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful())
                    movieDetailsHelper.setMovieDetailsData(itemView,response.body());
                else
                    movieDetailsHelper.setMovieDetailsData(itemView , null);
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e("API Service Presenter >"  , " error in getting data from API");
                Log.e("error message > " , t.getMessage());
            }
        });
    }

    @Override
    public void getMovieVideos(int movieID) {
        final Call<MovieVideosResponse> movieVideosResponseCall = ApiClient.getApiClient(context)
                .create(ApiService.class)
                .getMovieTrailers(
                        movieID ,
                        ApiClient.API_KEY ,
                        ApiClient.LANGUAGE);
        movieVideosResponseCall.enqueue(new Callback<MovieVideosResponse>() {
            @Override
            public void onResponse(Call<MovieVideosResponse> call, Response<MovieVideosResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getMovieTrailers().size() > 0)
                        movieTrailersHelper.setMovieTrailer(response.body().getMovieTrailers().get(0)); // sent first official trailer
                }else{
                    movieTrailersHelper.setMovieTrailer(null);
                }
            }

            @Override
            public void onFailure(Call<MovieVideosResponse> call, Throwable t) {
                Log.e("API Service Presenter >"  , " error in getting data from API");
                Log.e("error message > " , t.getMessage());
            }
        });
    }
}
