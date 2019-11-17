package com.example.dell.themoviest.client;

import android.view.View;

public interface IApiPresenter {
    void getMovies(String category , int page);
    void getMovieDetails(View itemView, int movieID);
    void getMovieVideos(int movieID);
}
