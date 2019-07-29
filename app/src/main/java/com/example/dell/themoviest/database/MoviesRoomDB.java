package com.example.dell.themoviest.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.dell.themoviest.model.MovieDetails;

@Database(entities = {MovieDetails.class} , version = 1)
@TypeConverters({CategoriesConverter.class})
abstract public class MoviesRoomDB extends RoomDatabase {
    public abstract MoviesDAO getMoviesDAO();
}
