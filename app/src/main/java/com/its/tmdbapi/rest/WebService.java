package com.its.tmdbapi.rest;
import android.util.Log;

import com.its.tmdbapi.database.model.MovieResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WebService {

    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY= "7873986e561f058429c7ff12b1a3cb63";
    private static WebService instance;
    private MovieService movieService;

    public WebService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieService = retrofit.create(MovieService.class);
    }

    public static WebService getInstance() {
        if (instance == null)
            instance = new WebService();
        return instance;
    }

    public void getPopular(String language, int page, final IWebServer listener) {

        Call<MovieResponse> call = movieService.getPopular(API_KEY,language, page);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (response.code() == 200) {
                    listener.onMoviesFetched(true, response.body().getResults(), -1, null);


                } else {
                    try {
                        listener.onMoviesFetched(true, null, response.code(), response.errorBody().string());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString());
                        listener.onMoviesFetched(true, null, response.code(), "Generic error message");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, t.toString());

            }
        });
    }
}



