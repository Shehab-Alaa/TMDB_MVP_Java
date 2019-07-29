package com.example.dell.themoviest.view;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.client.ApiPresenter;
import com.example.dell.themoviest.database.MoviesDatabaseSingleton;
import com.example.dell.themoviest.helpers.ApiMovieTrailersHelper;
import com.example.dell.themoviest.helpers.NotifyItemRemoved;
import com.example.dell.themoviest.model.Category;
import com.example.dell.themoviest.model.MovieDetails;

public class MovieInformation extends AppCompatActivity implements ApiMovieTrailersHelper {

    private MovieDetails selectedMovieDetails;
    private boolean favorite;

    private ImageView backPoster;
    private ImageView moviePoster;
    private FloatingActionButton fabFavorite;
    private TextView movieTitle;
    private TextView movieCategories;
    private TextView movieStatus;
    private ImageView movieStatusImage;
    private RatingBar movieRate;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView movieOverview;
    private Button expandCollapseBtn;
    private boolean isExpanded;
    private int moviePosition;
    private static NotifyItemRemoved notifyItemRemoved;
    private boolean clickedOnce;
    private Button openMovieTrailerBtn;
    private com.example.dell.themoviest.model.MovieTrailer movieTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_information);

        backPoster = findViewById(R.id.movie_backposter);
        moviePoster = findViewById(R.id.movie_poster);
        fabFavorite = findViewById(R.id.fab_favorite);
        movieTitle = findViewById(R.id.movie_title);
        movieCategories = findViewById(R.id.movie_categories);
        movieStatusImage = findViewById(R.id.movie_status_image);
        movieStatus = findViewById(R.id.movie_status);
        movieRate = findViewById(R.id.movie_rating);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(true);
        movieOverview = findViewById(R.id.movie_overview);
        expandCollapseBtn = findViewById(R.id.expand_collapse_btn);
        isExpanded = false;
        clickedOnce = false;
        openMovieTrailerBtn = findViewById(R.id.open_movie_trailer);

        favorite = getIntent().getExtras().getBoolean("favorite");
        if (favorite) {
            selectedMovieDetails = (MovieDetails) getIntent().getSerializableExtra("selectedMovie");
            moviePosition = getIntent().getExtras().getInt("moviePosition");
            initUI(selectedMovieDetails);
            fabFavorite.setImageResource(R.drawable.ic_favorite);
            openMovieTrailerBtn.setVisibility(View.INVISIBLE);
        }
        else {
            selectedMovieDetails = (MovieDetails) getIntent().getSerializableExtra("selectedMovie");
            initUI(selectedMovieDetails);
            fabFavorite.setImageResource(R.drawable.ic_un_favorite);
            // make movie Trailer Request;
            ApiPresenter apiPresenter = new ApiPresenter(this , getApplicationContext());
            apiPresenter.getMovieVideos(selectedMovieDetails.getId());
        }


        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite == true && clickedOnce == false ) {

                    clickedOnce = true;
                    fabFavorite.setImageResource(R.drawable.ic_un_favorite);

                    // remove from database;
                    int deletedRows = MoviesDatabaseSingleton.getMoviesRoomDB(getApplicationContext())
                            .getMoviesDAO()
                            .removeFavoriteMovie(selectedMovieDetails.getId());

                    if (deletedRows > 0) {
                        Toast.makeText(MovieInformation.this, "the movie is removed", Toast.LENGTH_SHORT).show();
                        // notify Adapter that a movie is removed from the list;
                        // notify activity to notify adapter that is on it;
                        notifyItemRemoved.onItemRemoved(moviePosition);
                        finish();
                    }
                    else
                        Toast.makeText(MovieInformation.this, "something went wrong !!", Toast.LENGTH_SHORT).show();

                }
                else if (favorite == false && clickedOnce == false){

                    clickedOnce = true;
                    fabFavorite.setImageResource(R.drawable.ic_favorite);

                    try {
                        //add to database
                        long insertedRows = MoviesDatabaseSingleton.getMoviesRoomDB(getApplicationContext())
                                .getMoviesDAO()
                                .insertFavoriteMovie(selectedMovieDetails);

                        if (insertedRows > 0) {
                            Toast.makeText(MovieInformation.this, "the movie is added", Toast.LENGTH_SHORT).show();
                        }
                    }catch (SQLiteConstraintException ex){
                        Toast.makeText(MovieInformation.this, "it's already added", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    public static void setNotifyItemRemovedInstance(NotifyItemRemoved notifyItemRemovedInstance)
    {
        notifyItemRemoved = notifyItemRemovedInstance;
    }

    private void initUI(MovieDetails movieDetails)
    {
        // came from Database(Favorite) or from API Request
        PicassoCache
                .getPicassoInstance(getApplicationContext())
                .load(ApiClient.BACKDROP_BASE_URL + movieDetails.getBackdropPath())
                .placeholder(R.drawable.movie_poster)
                .into(backPoster);

        PicassoCache
                .getPicassoInstance(getApplicationContext())
                .load(ApiClient.POSTER_BASE_URL + movieDetails.getPosterPath())
                .placeholder(R.drawable.movie_poster)
                .into(moviePoster);

        collapsingToolbarLayout.setTitle(movieDetails.getTitle());

        movieTitle.setText(movieDetails.getTitle());

        String categoriesHolder = "";
        for (Category category : movieDetails.getCategories())
            categoriesHolder += category.getName() + ". ";
        movieCategories.setText(categoriesHolder);

        if (movieDetails.getStatus().equals("Released")){
            movieStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_released));
            movieStatus.setText("Released");
        }else{
            movieStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_released));
            movieStatus.setText(movieDetails.getStatus());
        }

        float rating = (float) (( movieDetails.getVoteAverage() * 5 ) / 9);
        movieRate.setNumStars(5);
        movieRate.setStepSize(0.1f);
        movieRate.setRating(rating);
        movieRate.setIsIndicator(true);

        final String movieOverviewHolder = movieDetails.getOverview();
        final int MAX_CHARS = 100;
        if (movieOverviewHolder.length() >= MAX_CHARS) {
            // will show first 100 and show the rest when expanded
            expandCollapseBtn.setVisibility(View.VISIBLE);
            movieOverview.setText(movieOverviewHolder.substring(0 , MAX_CHARS) + "..");
            expandCollapseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded){
                        movieOverview.setText(movieOverviewHolder.substring(0 , MAX_CHARS) + "..");
                        isExpanded = false;
                        expandCollapseBtn.setText("EXPAND");
                    }else{
                        movieOverview.setText(movieOverviewHolder);
                        isExpanded = true;
                        expandCollapseBtn.setText("COLLAPSE");
                    }
                }
            });
        }else{
            movieOverview.setText(movieOverviewHolder);
            expandCollapseBtn.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void setMovieTrailer(com.example.dell.themoviest.model.MovieTrailer movieTrailer) {
        if (movieTrailer != null) {
            this.movieTrailer = movieTrailer;
            setMovieTrailerButton();
        }
    }

    private void setMovieTrailerButton()
    {
        openMovieTrailerBtn.setVisibility(View.VISIBLE);
        openMovieTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pass Movie Trailer to it to use trailer (key) with Youtube API
              Intent intent = new Intent(MovieInformation.this , MovieTrailerActivity.class);
              intent.putExtra("MovieTrailer" , movieTrailer);
              startActivity(intent);
            }
        });
    }
}
