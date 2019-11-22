package com.example.dell.themoviest.view;


import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import com.example.dell.themoviest.R;
import com.example.dell.themoviest.client.YoutubeClient;
import com.example.dell.themoviest.model.MovieTrailer;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private MovieTrailer movieTrailer;
    private ImageButton playTrailerBtn;
    private YouTubePlayer mYouTubePlayer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
        setWidthAndHeight();

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        playTrailerBtn = findViewById(R.id.play_trailer_btn);

        movieTrailer = (MovieTrailer) getIntent().getSerializableExtra("MovieTrailer");

        playTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init player
                youTubePlayerView.initialize(YoutubeClient.API_KEY , onInitializedListener);
                playTrailerBtn.setVisibility(View.INVISIBLE);
            }
        });

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                if (!wasRestored) {
                    mYouTubePlayer = youTubePlayer;
                    mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    mYouTubePlayer.setFullscreen(true);
                    mYouTubePlayer.loadVideo(movieTrailer.getKey());
                    mYouTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };


    }

    private void  setWidthAndHeight()
    {
        // TODO:: Better way to show only Video Screen; or watch it in youtube;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8) ,(int) (height*.5));
    }

}
