package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    Movie movie;

    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;

    public static String API_URL = "https://api.themoviedb.org/3/movie/" ;
    public static String API_URL_2 = "/videos?api_key=";
    public static final String TAG = "MovieDetails";
    public static String API_URL_3 = "&language=en-US";
    String vKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get binding created from enabling ViewBinding
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        //Set view
        View view = binding.getRoot();
        setContentView(view);
        //Binding every element needed.
        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        rbVoteAverage = binding.rbVoteAverage;


        //unwrap movie passed via intent, using its simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, so we convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);

        //  video id
        String videoId = String.valueOf(movie.getId());

        // resolve the player view from the layout
        YouTubePlayerView playerView = binding.player;

        // API call to get videoId
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(TAG, API_URL + videoId + API_URL_2 + getString(R.string.tmdb_api_key) + API_URL_3);
        client.get(API_URL + videoId + API_URL_2 + getString(R.string.tmdb_api_key) + API_URL_3, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    vKey = results.getJSONObject(0).getString("key");
                    // initialize with API key stored in secrets.xml
                    playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {

                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            // do any work here to cue video, play video, etc.
                            youTubePlayer.cueVideo(vKey);

                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            // log the error
                            Log.e(TAG, "Error initializing YouTube player");
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception",e);
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });




    }
}