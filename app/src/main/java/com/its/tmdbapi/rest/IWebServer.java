package com.its.tmdbapi.rest;

import com.its.tmdbapi.database.model.Movie;

import java.util.List;

public interface IWebServer {
    void onMoviesFetched(boolean success, List<Movie> response, int errorCode, String errorMessage);
}
