package com.example.dell.themoviest.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.client.YoutubeClient;
import com.example.dell.themoviest.helpers.OnMovieTrailerListener;
import com.example.dell.themoviest.model.MovieTrailer;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MovieTrailer> movieTrailers;
    private OnMovieTrailerListener movieTrailerListener;

    public MovieTrailersAdapter(Context mContext, ArrayList<MovieTrailer> movieTrailers , OnMovieTrailerListener movieTrailerListener) {
        this.mContext = mContext;
        this.movieTrailers = movieTrailers;
        this.movieTrailerListener = movieTrailerListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_trailer, parent , false);
        MovieTrailersAdapter.ViewHolder viewHolder = new MovieTrailersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(movieTrailers.get(position));
    }

    @Override
    public int getItemCount() {
        return movieTrailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView movieTrailerThumbnail;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            movieTrailerThumbnail = itemView.findViewById(R.id.movie_trailer_thumbnail);
            progressBar = itemView.findViewById(R.id.movie_trailer_loading);

            itemView.setOnClickListener(this);
        }

        public void onBind(MovieTrailer movieTrailer){

            progressBar.setVisibility(View.VISIBLE);

            PicassoCache
                    .getPicassoInstance(mContext)
                    .load(YoutubeClient.YOUTUBE_VIDEO_THUMBNAIL + movieTrailer.getKey() + "/0.jpg")
                    .into(movieTrailerThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            movieTrailerThumbnail.setImageResource(R.drawable.movie_trailer_temp);
                        }
                    });
        }

        @Override
        public void onClick(View itemView) {
            movieTrailerListener.onMovieTrailerClick(itemView , getAdapterPosition());
        }
    }
}
