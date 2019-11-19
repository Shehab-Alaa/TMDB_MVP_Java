package com.example.dell.themoviest.client;

import android.view.View;

public interface IApiPresenter {
    void getMovies(String category , int page);
    void getMovieDetails(int movieID);
    void getMovieVideos(int movieID);
}
