package com.example.dell.themoviest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.model.MovieReview;

import java.util.ArrayList;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewVHolder>{

    private ArrayList<MovieReview> movieReviews;
    private Context mContext;

    public MovieReviewsAdapter(ArrayList<MovieReview> movieReviews, Context mContext) {
        this.movieReviews = movieReviews;
        this.mContext = mContext;
    }

    @Override
    public MovieReviewVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_review, parent , false);
        MovieReviewsAdapter.MovieReviewVHolder movieReviewVHolder = new MovieReviewsAdapter.MovieReviewVHolder(view);
        return movieReviewVHolder;
    }

    @Override
    public void onBindViewHolder(MovieReviewVHolder movieReviewVHolder, int position) {
        movieReviewVHolder.onBind(movieReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return movieReviews.size();
    }


     class MovieReviewVHolder extends RecyclerView.ViewHolder {

        private TextView authorName;
        private TextView authorReview;

        public MovieReviewVHolder(View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.author_name);
            authorReview = itemView.findViewById(R.id.author_review);
        }

        public void onBind(MovieReview movieReview){
            authorName.setText(movieReview.getAuthor());
            authorReview.setText(movieReview.getContent());
        }
    }
}
