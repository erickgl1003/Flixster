package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;
import com.facebook.stetho.common.ArrayListAccumulator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=";


    public static final String TAG = "MainActivity";

    List<Movie> movies;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Flixster);
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.splash);
        new CountDownTimer(5000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //Set binding from this activity
                MainActivity.this.setContentView(R.layout.activity_main);
            }
        }.start();

         */

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        //Get view from binding

        View view = binding.getRoot();
        setContentView(view);

        //Get recycler view
        RecyclerView rvMovies = binding.rvMovies;

        movies = new ArrayList<>();
        //Create the adapter
        MovieAdapter movieAdapter = new MovieAdapter(this,movies );

        //Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        //Set a Layout Manager
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        //Add separating line
        rvMovies.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL + getString(R.string.tmdb_api_key), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG,"Results: " + results.toString());
                    movies.addAll(Movie.fromJSONArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG,"Movies: " + movies.size());
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