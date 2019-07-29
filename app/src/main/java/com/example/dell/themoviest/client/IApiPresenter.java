package com.example.dell.themoviest.client;

public interface IApiPresenter {
    void getMovies(String category , int page);
    void getMovieDetails(int movieID);
    void getMovieVideos(int movieID);
}
