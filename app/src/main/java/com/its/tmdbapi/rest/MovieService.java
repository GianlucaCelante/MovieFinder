package com.its.tmdbapi.rest;


import com.its.tmdbapi.database.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

    @GET("movie/popular")
    Call<MovieResponse> getPopular(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

}
