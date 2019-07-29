package com.example.dell.themoviest.database;


import android.arch.persistence.room.Room;
import android.content.Context;

public class MoviesDatabaseSingleton {
    private static MoviesRoomDB moviesRoomDB = null;

    public static MoviesRoomDB getMoviesRoomDB(Context context)
    {
        if (moviesRoomDB == null) {
            // Database access point Not a Presenter
            moviesRoomDB = Room.databaseBuilder(context , MoviesRoomDB.class , "mMovies")
                    .allowMainThreadQueries().build();
        }
        return moviesRoomDB;
    }
}
