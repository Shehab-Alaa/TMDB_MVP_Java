package com.example.dell.themoviest.helpers;


import com.example.dell.themoviest.model.MovieReview;

import java.util.ArrayList;

public interface ApiMovieReviewsHelper {
    void setMovieReviewsData(ArrayList<MovieReview> movieReviews , int totalPages);
}
