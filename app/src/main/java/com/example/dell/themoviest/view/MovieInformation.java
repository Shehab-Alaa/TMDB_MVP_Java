package com.example.dell.themoviest.view;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.client.ApiPresenter;
import com.example.dell.themoviest.database.MoviesDatabaseSingleton;
import com.example.dell.themoviest.helpers.ApiMovieDetailsHelper;
import com.example.dell.themoviest.helpers.ApiMovieTrailersHelper;
import com.example.dell.themoviest.helpers.NotifyItemRemoved;
import com.example.dell.themoviest.model.Category;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;
import com.squareup.picasso.Callback;

public class MovieInformation extends AppCompatActivity implements ApiMovieTrailersHelper , ApiMovieDetailsHelper {

    private MovieDetails selectedMovieDetails;
    private Movie selectedMovie;
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
    private LinearLayout movieOverViewSection;
    private ConstraintLayout movieDetailsSection;
    private int rightAnimation;
    private int bottomAnimation;
    private ProgressBar movieCoverLoading;
    private TextView isAdultMovie;
    private TextView movieBudget;
    private TextView movieRunTime;
    private ApiPresenter apiPresenter;

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
        movieCoverLoading = findViewById(R.id.movie_cover_loading);
        isAdultMovie = findViewById(R.id.movie_adult);
        movieBudget = findViewById(R.id.movie_budget);
        movieRunTime = findViewById(R.id.movie_run_time);

        // Animations Section
        movieDetailsSection = findViewById(R.id.movie_details_layout);
        rightAnimation = R.anim.layout_animation_slide_right;
        LayoutAnimationController rightAnimationController = AnimationUtils.loadLayoutAnimation(this, rightAnimation);
        movieDetailsSection.setLayoutAnimation(rightAnimationController);

        movieOverViewSection = findViewById(R.id.movie_overview_layout);
        bottomAnimation = R.anim.layout_animation_from_bottom;
        LayoutAnimationController bottomAnimationController = AnimationUtils.loadLayoutAnimation(this, bottomAnimation);
        movieOverViewSection.setLayoutAnimation(bottomAnimationController);
        
        ////

        favorite = getIntent().getExtras().getBoolean("favorite");
        if (favorite) {
            selectedMovieDetails = (MovieDetails) getIntent().getSerializableExtra("selectedMovie");
            moviePosition = getIntent().getExtras().getInt("moviePosition");
            initUI(selectedMovieDetails);
            fabFavorite.setImageResource(R.drawable.ic_favorite);
            openMovieTrailerBtn.setVisibility(View.INVISIBLE);
        }
        else {
            selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
            initKnownUI(selectedMovie);
            fabFavorite.setImageResource(R.drawable.ic_un_favorite);

            apiPresenter = new ApiPresenter(getApplicationContext());
            apiPresenter.setMovieDetailsHelper(this);
            apiPresenter.setMovieTrailersHelper(this);
            // request for rest of Data;
            apiPresenter.getMovieDetails(selectedMovie.getId());
            // make movie Trailer Request;
            apiPresenter.getMovieVideos(selectedMovie.getId());
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

    private void initKnownUI(Movie selectedMovie) {
        movieCoverLoading.setVisibility(View.VISIBLE);
        // Movie Cover Poster
        PicassoCache
                .getPicassoInstance(getApplicationContext())
                .load(ApiClient.BACKDROP_BASE_URL + selectedMovie.getBackdropPath())
                //.placeholder(R.drawable.movie_poster)
                .into(backPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        movieCoverLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        movieCoverLoading.setVisibility(View.INVISIBLE);
                        backPoster.setImageResource(R.drawable.movie_poster);
                    }
                });
        // Shard Element Movie Poster
        PicassoCache
                .getPicassoInstance(getApplicationContext())
                .load(ApiClient.POSTER_BASE_URL + selectedMovie.getPosterPath())
                //.placeholder(R.drawable.movie_poster)
                .into(moviePoster);

        // Movie Title
        collapsingToolbarLayout.setTitle(selectedMovie.getTitle());
        movieTitle.setText(selectedMovie.getTitle());
        // Movie Average Rate
        float rating = (float) (( selectedMovie.getVoteAverage() * 5 ) / 9);
        movieRate.setNumStars(5);
        movieRate.setStepSize(0.1f);
        movieRate.setRating(rating);
        movieRate.setIsIndicator(true);
        // Movie Overview
        final String movieOverviewHolder = selectedMovie.getOverview();
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

    private void initRestUI(MovieDetails selectedMovieDetails){

        if (selectedMovieDetails == null)
        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Movie Categories
        String categoriesHolder = "";
        for (Category category : selectedMovieDetails.getCategories())
            categoriesHolder += category.getName() + ". ";
        movieCategories.setText(categoriesHolder);
        // Movie Status
        if (selectedMovieDetails.getStatus().equals("Released")){
            movieStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_released));
            movieStatus.setText("Released");
        }else{
            movieStatusImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_released));
            movieStatus.setText(selectedMovieDetails.getStatus());
        }
        // Movie Budget && Is For Adult && RunTime
         movieBudget.setText(selectedMovieDetails.getBudget().toString());
         movieRunTime.setText(selectedMovieDetails.getRuntime().toString());
         if (selectedMovieDetails.getAdult())
             isAdultMovie.setText("Yes");
         else
             isAdultMovie.setText("No");
        // TODO: Movie Reviews
        // TODO: See theMDB another requests for better UX => Trailers || Related Movies
    }

    @Override
    public void setMovieDetailsData(MovieDetails movieDetails) {
        selectedMovieDetails = movieDetails;
        initRestUI(movieDetails);
    }

    public static void setNotifyItemRemovedInstance(NotifyItemRemoved notifyItemRemovedInstance)
    {
        notifyItemRemoved = notifyItemRemovedInstance;
    }

    private void initUI(final MovieDetails movieDetails)
    {
        // initUI from an object came from Database (Favorite)
        // i will use the 2 upper methods to set the Views Data
        Movie movieHolder = new Movie();
        movieHolder.setBackdropPath(movieDetails.getBackdropPath());
        movieHolder.setPosterPath(movieDetails.getPosterPath());
        movieHolder.setTitle(movieDetails.getTitle());
        movieHolder.setVoteAverage(movieDetails.getVoteAverage());
        movieHolder.setOverview(movieDetails.getOverview());

        initKnownUI(movieHolder);
        initRestUI(movieDetails);
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
