package com.example.dell.themoviest.client;

import com.example.dell.themoviest.model.DataResponse;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;
import com.example.dell.themoviest.model.MovieReviewResponse;
import com.example.dell.themoviest.model.MovieVideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/3/movie/{category}")
    Call<DataResponse> getMovies(
            @Path("category") String category ,
            @Query("api_key") String apiKey ,
            @Query("language") String language  ,
            @Query("page") int page
    );

    @GET("/3/movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(
            @Path("movie_id") int id ,
            @Query("api_key") String apiKey ,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/videos")
    Call<MovieVideosResponse> getMovieTrailers(
            @Path("movie_id") int id ,
            @Query("api_key") String apiKey ,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/reviews")
    Call<MovieReviewResponse> getMovieReviews(
            @Path("movie_id") int id ,
            @Query("api_key") String apiKey ,
            @Query("language") String language ,
            @Query("page") int page
    );

    @GET("/3/movie/{movie_id}/similar")
    Call<DataResponse> getSimilarMovies(
            @Path("movie_id") int id ,
            @Query("api_key") String apiKey ,
            @Query("language") String language ,
            @Query("page") int page
    );



}
