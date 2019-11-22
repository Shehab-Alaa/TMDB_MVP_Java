package com.example.dell.themoviest.client;


import android.content.Context;
import android.util.Log;

import com.example.dell.themoviest.helpers.ApiMovieDetailsHelper;
import com.example.dell.themoviest.helpers.ApiMovieReviewsHelper;
import com.example.dell.themoviest.helpers.ApiMovieTrailersHelper;
import com.example.dell.themoviest.helpers.ApiMoviesHelper;
import com.example.dell.themoviest.model.DataResponse;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;
import com.example.dell.themoviest.model.MovieReview;
import com.example.dell.themoviest.model.MovieReviewResponse;
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
    private ApiMovieReviewsHelper movieReviewsHelper;
    private Context context;

    public ApiPresenter(Context context)
    {
        this.context = context;
    }

    public void setMovieDetailsHelper(ApiMovieDetailsHelper movieDetailsHelper) {
        this.movieDetailsHelper = movieDetailsHelper;
    }

    public void setMoviesHelper(ApiMoviesHelper moviesHelper) {
        this.moviesHelper = moviesHelper;
    }

    public void setMovieTrailersHelper(ApiMovieTrailersHelper movieTrailersHelper) {
        this.movieTrailersHelper = movieTrailersHelper;
    }

    public void setMovieReviewsHelper(ApiMovieReviewsHelper movieReviewsHelper) {
        this.movieReviewsHelper = movieReviewsHelper;
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
    public void getSimilarMovies(int movieID, int page) {
        final Call<DataResponse> moviesCall = ApiClient.getApiClient(context)
                .create(ApiService.class).getSimilarMovies(
                        movieID ,
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
    public void getMovieReviews(int movieID , int page){
        final Call<MovieReviewResponse> movieReviewResponseCall = ApiClient.getApiClient(context)
                .create(ApiService.class).getMovieReviews(
                        movieID ,
                        ApiClient.API_KEY ,
                        ApiClient.LANGUAGE ,
                        page);

        movieReviewResponseCall.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                if (response.isSuccessful())
                    movieReviewsHelper.setMovieReviewsData((ArrayList<MovieReview>) response.body().getMovieReviews() , response.body().getTotalPages());
                else
                    movieReviewsHelper.setMovieReviewsData(null , 0);
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                Log.e("API Service Presenter >"  , " error in getting data from API");
                Log.e("error message > " , t.getMessage());
            }
        });

    }

    @Override
    public void getMovieDetails(int movieID) {
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
                    movieDetailsHelper.setMovieDetailsData(response.body());
                else
                    movieDetailsHelper.setMovieDetailsData(null);
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
                        movieTrailersHelper.setMovieTrailers((ArrayList<MovieTrailer>) response.body().getMovieTrailers());
                }else{
                    movieTrailersHelper.setMovieTrailers(null);
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
