package com.example.dell.themoviest.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MoviesDAO {

    @Insert
    long insertFavoriteMovie(MovieDetails movieDetails);

    @Query("select * from Movies")
    List<MovieDetails> getFavoriteMovies();

    @Query("delete from Movies where id = :movieID")
    int removeFavoriteMovie(long movieID);
}
