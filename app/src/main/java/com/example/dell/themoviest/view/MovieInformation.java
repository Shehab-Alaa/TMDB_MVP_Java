package com.example.dell.themoviest.view;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.LinkAddress;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.adapter.MovieReviewsAdapter;
import com.example.dell.themoviest.adapter.MovieTrailersAdapter;
import com.example.dell.themoviest.adapter.MoviesAdapter;
import com.example.dell.themoviest.cache.PicassoCache;
import com.example.dell.themoviest.client.ApiClient;
import com.example.dell.themoviest.client.ApiPresenter;
import com.example.dell.themoviest.database.MoviesDatabaseSingleton;
import com.example.dell.themoviest.helpers.ApiMovieDetailsHelper;
import com.example.dell.themoviest.helpers.ApiMovieReviewsHelper;
import com.example.dell.themoviest.helpers.ApiMovieTrailersHelper;
import com.example.dell.themoviest.helpers.ApiMoviesHelper;
import com.example.dell.themoviest.helpers.NotifyItemRemoved;
import com.example.dell.themoviest.helpers.OnMovieListener;
import com.example.dell.themoviest.helpers.OnMovieTrailerListener;
import com.example.dell.themoviest.model.Category;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;
import com.example.dell.themoviest.model.MovieReview;
import com.example.dell.themoviest.model.MovieTrailer;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

public class MovieInformation extends AppCompatActivity implements ApiMovieTrailersHelper , ApiMovieDetailsHelper, ApiMovieReviewsHelper, ApiMoviesHelper, OnMovieListener, OnMovieTrailerListener {

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
    private LinearLayout movieOverViewSection;
    private ConstraintLayout movieDetailsSection;
    private int rightAnimation;
    private int bottomAnimation;
    private ProgressBar movieCoverLoading;
    private TextView isAdultMovie;
    private TextView movieBudget;
    private TextView movieRunTime;
    private ApiPresenter apiPresenter;
    private int totalMovieReviewPages;

    private RecyclerView similarMoviesRV;
    private MoviesAdapter similarMoviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Movie> similarMoviesList;

    private RecyclerView movieReviewsRV;
    private MovieReviewsAdapter movieReviewsAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<MovieReview> movieReviewsList;

    private LinearLayout similarMoviesLayout;
    private TextView reviewText;
    private LinearLayout movieTrailersLayout;

    private RecyclerView movieTrailersRV;
    private MovieTrailersAdapter movieTrailersAdapter;
    private LinearLayoutManager trailersLayoutManager;
    private ArrayList<MovieTrailer> movieTrailerList;

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
        movieCoverLoading = findViewById(R.id.movie_cover_loading);
        isAdultMovie = findViewById(R.id.movie_adult);
        movieBudget = findViewById(R.id.movie_budget);
        movieRunTime = findViewById(R.id.movie_run_time);
        similarMoviesLayout = findViewById(R.id.similar_movies_layout);
        reviewText = findViewById(R.id.review_text);
        movieTrailersLayout = findViewById(R.id.movies_trailers_layout);


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

            reviewText.setVisibility(View.GONE);
            similarMoviesLayout.setVisibility(View.GONE);
            movieTrailersLayout.setVisibility(View.GONE);

            initUI(selectedMovieDetails);
            fabFavorite.setImageResource(R.drawable.ic_favorite);
        }
        else {
            selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
            initKnownUI(selectedMovie);
            fabFavorite.setImageResource(R.drawable.ic_un_favorite);

            // SimilarMovies Section
            similarMoviesList = new ArrayList<>();
            similarMoviesAdapter = new MoviesAdapter(this , similarMoviesList , this);
            similarMoviesRV = findViewById(R.id.rv_similar_movies);
            linearLayoutManager = new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false);
            similarMoviesRV.setLayoutManager(linearLayoutManager);
            similarMoviesRV.setHasFixedSize(true);
            similarMoviesRV.setAdapter(similarMoviesAdapter);
            ///

            // MovieReviews Section
            movieReviewsList = new ArrayList<>();
            movieReviewsAdapter = new MovieReviewsAdapter(movieReviewsList , this);
            movieReviewsRV = findViewById(R.id.rv_movie_reviews);
            layoutManager = new LinearLayoutManager(this);
            movieReviewsRV.setLayoutManager(layoutManager);
            movieReviewsRV.setHasFixedSize(true);
            movieReviewsRV.setAdapter(movieReviewsAdapter);
            ///

            // MovieTrailers Section
            movieTrailerList = new ArrayList<>();
            movieTrailersAdapter = new MovieTrailersAdapter(this , movieTrailerList , this);
            trailersLayoutManager = new LinearLayoutManager(this , LinearLayout.HORIZONTAL , false);
            movieTrailersRV = findViewById(R.id.rv_movie_trailers);
            movieTrailersRV.setLayoutManager(trailersLayoutManager);
            movieTrailersRV.setHasFixedSize(true);
            movieTrailersRV.setAdapter(movieTrailersAdapter);
            ///


            apiPresenter = new ApiPresenter(getApplicationContext());
            apiPresenter.setMovieDetailsHelper(this);
            apiPresenter.setMovieTrailersHelper(this);
            apiPresenter.setMovieReviewsHelper(this);
            apiPresenter.setMoviesHelper(this);
            // request for rest of Data;
            apiPresenter.getMovieDetails(selectedMovie.getId());
            // make movie Trailer Request;
            apiPresenter.getMovieVideos(selectedMovie.getId());
            // request movie reviews
            apiPresenter.getMovieReviews(selectedMovie.getId() , 1);
            totalMovieReviewPages = -1;
            // request similar movies
            apiPresenter.getSimilarMovies(selectedMovie.getId() , 1);

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
        moviePoster.setTransitionName(selectedMovie.getId().toString());
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
         movieBudget.setText(selectedMovieDetails.getBudget().toString() + "$");
         int hours = Integer.parseInt(selectedMovieDetails.getRuntime().toString()) / 60;
         int minutes = Integer.parseInt(selectedMovieDetails.getRuntime().toString()) % 60;
         movieRunTime.setText("0" + hours + ":" + minutes);
         if (selectedMovieDetails.getAdult())
             isAdultMovie.setText("Yes");
         else
             isAdultMovie.setText("No");
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
        movieHolder.setId(movieDetails.getId());
        movieHolder.setBackdropPath(movieDetails.getBackdropPath());
        movieHolder.setPosterPath(movieDetails.getPosterPath());
        movieHolder.setTitle(movieDetails.getTitle());
        movieHolder.setVoteAverage(movieDetails.getVoteAverage());
        movieHolder.setOverview(movieDetails.getOverview());

        initKnownUI(movieHolder);
        initRestUI(movieDetails);
    }

    @Override
    public void setMovieTrailers(ArrayList<MovieTrailer> movieTrailers) {
        if (movieTrailers != null) {
            movieTrailerList.addAll(movieTrailers);
            movieTrailersAdapter.notifyDataSetChanged();
        }else{
            movieTrailersLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setMovieReviewsData(ArrayList<MovieReview> movieReviews , int totalPages) {
        //TODO: make recyclerView with Card View to print movie Reviews
        if (totalMovieReviewPages == -1) {
            totalMovieReviewPages = totalPages; // i will request all MovieReviewsPages;
            requestMovieReviewsPages();
        }
        if (totalPages == 0){
            reviewText.setVisibility(View.GONE);
            movieReviewsRV.setVisibility(View.GONE);
        }
        else{
            movieReviewsList.addAll(movieReviews);
            movieReviewsAdapter.notifyDataSetChanged();
        }
    }

    private void requestMovieReviewsPages() {
        int page = 2;
        while (totalMovieReviewPages > 1 && page != (totalMovieReviewPages+1)){
            apiPresenter.getMovieReviews(selectedMovie.getId() , page);
            page++;
        }
    }

    @Override
    public void setMoviesData(ArrayList<Movie> movies) {
        //TODO: set Arraylist of Similar movies (Horizontal)
        if (movies == null){
            similarMoviesLayout.setVisibility(View.GONE);
        }
        else {
            similarMoviesList.addAll(movies);
            similarMoviesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMovieClick(View itemView, int moviePosition) {
        // on Similar Movies List
        Intent intent = new Intent(this , MovieInformation.class);
        intent.putExtra("selectedMovie" , similarMoviesList.get(moviePosition));
        intent.putExtra("favorite", false);
        // set dynamic transition name by MovieID
        itemView.findViewById(R.id.movie_poster).setTransitionName(similarMoviesList.get(moviePosition).getId().toString());
        // need to share MoviePoster between this Activity And MovieInformation
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        itemView.findViewById(R.id.movie_poster),
                        ViewCompat.getTransitionName(itemView.findViewById(R.id.movie_poster)));
        startActivity(intent , options.toBundle());
    }

    @Override
    public void onMovieTrailerClick(View itemView, int moviePosition) {
        Intent intent = new Intent(this,MovieTrailerActivity.class);
        intent.putExtra("MovieTrailer" , movieTrailerList.get(moviePosition));
        startActivity(intent);
    }
}
