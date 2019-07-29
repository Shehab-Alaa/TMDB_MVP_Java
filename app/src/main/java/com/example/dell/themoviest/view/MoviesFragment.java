package com.example.dell.themoviest.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.adapter.MoviesDetailsAdapter;
import com.example.dell.themoviest.helpers.ApiMovieDetailsHelper;
import com.example.dell.themoviest.helpers.EndlessRecyclerViewScrollListener;
import com.example.dell.themoviest.adapter.MoviesAdapter;
import com.example.dell.themoviest.helpers.ApiMoviesHelper;
import com.example.dell.themoviest.client.ApiPresenter;
import com.example.dell.themoviest.database.MoviesDatabaseSingleton;
import com.example.dell.themoviest.helpers.GridSpacingItemDecoration;
import com.example.dell.themoviest.helpers.NotifyItemRemoved;
import com.example.dell.themoviest.helpers.OnMovieListener;
import com.example.dell.themoviest.model.Movie;
import com.example.dell.themoviest.model.MovieDetails;

import java.util.ArrayList;

public class MoviesFragment extends Fragment implements ApiMoviesHelper , OnMovieListener
        , ApiMovieDetailsHelper , NotifyItemRemoved {

    private Context mContext;
    private String category;
    private ArrayList<Movie> movies;
    private ArrayList<MovieDetails> moviesDetails;
    private RecyclerView moviesRV;
    private MoviesAdapter moviesAdapter;
    private MoviesDetailsAdapter moviesDetailsAdapter;
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener eScrollListener;
    private GridLayoutManager gridLayoutManager;
    private boolean isFavorite;
    private ApiPresenter apiPresenter;

    @Override
    public void onAttach(Context context) {
        // hold context from an Activity that there lifecycles are tied together
        mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies , container , false);

        category = getArguments().getString("category");

        moviesRV = view.findViewById(R.id.movies_rv);
        initRecyclerView();

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        if (category.equals("favorite")) {

            isFavorite = true;
            moviesDetails = new ArrayList<>();
            // passing activity context because adapter lifecycle is attached to activity lifecycle
            moviesDetailsAdapter = new MoviesDetailsAdapter(mContext , moviesDetails , this);
            moviesRV.setAdapter(moviesDetailsAdapter);
            //read from DB
            // pass an Application Context (to not lead to memory leaks) and not to tied DB with activity Lifecycle
            moviesDetails.addAll(
                    (ArrayList<MovieDetails>)  MoviesDatabaseSingleton
                            .getMoviesRoomDB(mContext.getApplicationContext())
                            .getMoviesDAO()
                            .getFavoriteMovies() );
            notifyMoviesAdapter();
        }else {

            isFavorite = false;

            // pass an Application Context not activity Context to avoid tied lifecycles and activity cannot be garbage collected
            apiPresenter = new ApiPresenter( this , this, mContext.getApplicationContext());

            movies = new ArrayList<>();
            moviesAdapter = new MoviesAdapter(mContext , movies , this);
            moviesRV.setAdapter(moviesAdapter);
            // make an API request
            loadDataFromApi(1);
            // endless page loader;
            pagination();
        }

        return view;
    }

    private void pagination()
    {
        eScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page += 1; // to avoid loading the first page again
                if (page < 11)
                    loadDataFromApi(page);
                else
                    Toast.makeText(mContext, "can not load more", Toast.LENGTH_SHORT).show();
            }
        };
        moviesRV.addOnScrollListener(eScrollListener);
    }

    private void loadDataFromApi(int page)
    {
        apiPresenter.getMovies(category , page);
    }

    private void initRecyclerView()
    {
        gridLayoutManager = new GridLayoutManager(getActivity() , 2);
        moviesRV.setLayoutManager(gridLayoutManager);
        moviesRV.setItemAnimator(new DefaultItemAnimator());
        // equal spaces between grid items
        int spanCount = 2; // 2 columns
        int spacing = 25; // 25px
        boolean includeEdge = true;
        moviesRV.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    @Override
    public void setMoviesData(ArrayList<Movie> movies) {
        if (movies != null) {
            // update data without using = or new to affect adapter data;
            this.movies.addAll(movies);
            notifyMoviesAdapter();
        }else
            Toast.makeText(mContext, "please check your internet connection", Toast.LENGTH_SHORT).show();
    }

    private void notifyMoviesAdapter()
    {
        progressBar.setVisibility(View.INVISIBLE);
        if (moviesAdapter != null) {
            moviesAdapter.notifyDataSetChanged();
        }
        else if(moviesDetailsAdapter != null){
            moviesDetailsAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(mContext, "something went wrong !!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int moviePosition) {
        Intent intent = new Intent(mContext , MovieInformation.class);
        if(isFavorite) {
            intent.putExtra("selectedMovie", moviesDetails.get(moviePosition));
            // to use position to notify the adapter if the movie is removed from favorite
            intent.putExtra("moviePosition" , moviePosition);
            intent.putExtra("favorite" , isFavorite);
            startActivity(intent);
            // i want to sent this instance to an activity i use a static method (until now)
            MovieInformation.setNotifyItemRemovedInstance(this);
        }else{
            // i want to request a Movie Details from here before going to MovieInfoActivity
            apiPresenter.getMovieDetails(movies.get(moviePosition).getId());
        }
    }

    @Override
    public void setMovieDetailsData(MovieDetails movieDetails) {
        // sent requested movie details to movie information-
        if (movieDetails != null) {
            Intent intent = new Intent(mContext, MovieInformation.class);
            intent.putExtra("selectedMovie", movieDetails);
            intent.putExtra("favorite", isFavorite);
            startActivity(intent);
        }else
            Toast.makeText(mContext, "please check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemRemoved(int moviePosition) {
        // removed only from DB (favorite) (movieDetails Array)
        moviesDetails.remove(moviePosition);
        moviesDetailsAdapter.notifyItemRangeRemoved(moviePosition , moviesDetails.size());
        moviesDetailsAdapter.notifyDataSetChanged();
    }
}
