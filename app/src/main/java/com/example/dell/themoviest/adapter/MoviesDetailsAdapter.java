package com.example.dell.themoviest.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.helpers.OnMovieListener;
import com.example.dell.themoviest.model.MovieDetails;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

public class MoviesDetailsAdapter extends RecyclerView.Adapter<MoviesDetailsAdapter.MoviesViewHolder> {

    private Context context;
    private ArrayList<MovieDetails> moviesDetails;
    public OnMovieListener onMovieListener;


    public MoviesDetailsAdapter(Context context , ArrayList<MovieDetails> moviesDetails
            , OnMovieListener onMovieListener)
    {
        this.context = context;
        this.moviesDetails = moviesDetails;
        this.onMovieListener = onMovieListener;
    }


    @Override
    public MoviesDetailsAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie , parent , false);
        MoviesDetailsAdapter.MoviesViewHolder moviesViewHolder = new MoviesDetailsAdapter.MoviesViewHolder(view);
        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesDetailsAdapter.MoviesViewHolder holder, final int position) {
        holder.onBindMovie(moviesDetails.get(position));
    }

    @Override
    public int getItemCount() {
        return moviesDetails.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView moviePoster;
        private TextView movieTitle;
        private ProgressBar loadingMoviePoster;

        public MoviesViewHolder(View itemView)  {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieTitle = itemView.findViewById(R.id.movie_title);
            loadingMoviePoster = itemView.findViewById(R.id.movie_poster_loading);

            itemView.setOnClickListener(this);
        }

        public void onBindMovie(MovieDetails movieDetails)
        {
            PicassoCache
                    .getPicassoInstance(context)
                    .load(ApiClient.POSTER_BASE_URL + movieDetails.getPosterPath())
                    //.placeholder(R.drawable.movie_poster)
                    .into(moviePoster, new Callback() {
                        public void onSuccess() {
                            loadingMoviePoster.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            loadingMoviePoster.setVisibility(View.INVISIBLE);
                            moviePoster.setImageResource(R.drawable.movie_poster);
                        }
                    });

            movieTitle.setText(movieDetails.getTitle());
        }

        @Override
        public void onClick(View itemView) {
            // sent position to activity
            // activity is dealing with all thing not the adapter
            onMovieListener.onMovieClick(itemView,getAdapterPosition());
        }
    }
}
