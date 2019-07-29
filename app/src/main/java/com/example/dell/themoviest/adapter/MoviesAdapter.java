package com.example.dell.themoviest.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.helpers.OnMovieListener;
import com.example.dell.themoviest.model.MovieDetails;
import com.example.dell.themoviest.view.MovieInformation;
import com.example.dell.themoviest.R;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.model.Movie;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context context;
    private ArrayList<Movie> movies;
    public OnMovieListener onMovieListener;


    public MoviesAdapter(Context context , ArrayList<Movie> movies ,OnMovieListener onMovieListener)
    {
        this.context = context;
        this.movies = movies;
        this.onMovieListener = onMovieListener;
    }

    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie , parent , false);
        MoviesAdapter.MoviesViewHolder moviesViewHolder = new MoviesAdapter.MoviesViewHolder(view);
        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesViewHolder holder, final int position) {
        holder.onBindMovie(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView moviePoster;
        private TextView movieTitle;

        public MoviesViewHolder(View itemView)  {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieTitle = itemView.findViewById(R.id.movie_title);

            itemView.setOnClickListener(this);
        }

        public void onBindMovie(Movie movie)
        {
            PicassoCache
                    .getPicassoInstance(context)
                    .load(ApiClient.POSTER_BASE_URL + movie.getPosterPath())
                    .placeholder(R.drawable.movie_poster)
                    .into(moviePoster);

            movieTitle.setText(movie.getTitle());
        }

        @Override
        public void onClick(View v) {
            // sent position to activity
            // activity is dealing with all thing not the adapter
            onMovieListener.onItemClick(getAdapterPosition());
        }
    }
}
