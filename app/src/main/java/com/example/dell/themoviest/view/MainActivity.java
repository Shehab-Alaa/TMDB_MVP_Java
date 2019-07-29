package com.example.dell.themoviest.view;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.dell.themoviest.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private final String NOW_PLAYING = "now_playing";
    private final String POPULAR = "popular";
    private final String TOP_RATED = "top_rated";
    private final String UPCOMING = "upcoming";
    private final String FAVORITE = "favorite";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar
        , R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
        {
            openMoviesFragment(NOW_PLAYING);
            navigationView.setCheckedItem(R.id.nowPlayingMoviesItem);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nowPlayingMoviesItem:
                toolbar.setTitle("Now Playing Movies");
                openMoviesFragment(NOW_PLAYING);
                 break;
            case R.id.popularMoviesItem:
                toolbar.setTitle("Popular Movies");
                openMoviesFragment(POPULAR);
                break;
            case R.id.topRatedMoviesItem:
                toolbar.setTitle("Top Rated Movies");
                openMoviesFragment(TOP_RATED);
                break;
            case R.id.upcomingMoviesItem:
                toolbar.setTitle("Upcoming Movies");
                openMoviesFragment(UPCOMING);
                break;
            case R.id.favoriteMoviesItem:
                toolbar.setTitle("Favorite Movies");
                openMoviesFragment(FAVORITE);
                break;
            case R.id.aboutItem:
                toolbar.setTitle("About");
                openOtherFragments("about");
                break;
            case R.id.settingsItem:
                toolbar.setTitle("Settings");
                openOtherFragments("settings");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openMoviesFragment(String category)
    {
        Bundle bundle = new Bundle();
        bundle.putString("category" , category);

        MoviesFragment moviesFragment = new MoviesFragment();
        moviesFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                moviesFragment).commit();
    }


    public void openOtherFragments(String key)
    {
       if (key.equals("about")){
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                   new AboutFragment()).commit();
       }
       else if (key.equals("settings")){
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                   new SettingsFragment()).commit();
       }
    }
}
