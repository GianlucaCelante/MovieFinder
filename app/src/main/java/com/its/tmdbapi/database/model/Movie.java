package com.its.tmdbapi.database.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.its.tmdbapi.database.MovieTableHelper;

public class Movie {

    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("genre_ids")
    private int[] genreIds;
    @SerializedName("id")
    private Integer id;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("popularity")
    private Double popularity;
    @SerializedName("vote_count")
    private Integer voteCount;
    @SerializedName("video")
    private Boolean video;
    @SerializedName("vote_average")
    private Double voteAverage;

    private boolean watchLater = false;

    public Movie() {
    }

    public Movie(String posterPath, boolean adult, String overview, String releaseDate,
                 int[] genreIds, Integer id, String originalTitle, String originalLanguage,
                 String title, String backdropPath, Double popularity, Integer voteCount, Boolean video, Double voteAverage, Boolean watchLater) {
        this.posterPath = posterPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
        this.watchLater = watchLater;
    }

    public Movie(Cursor cursor){

        id = cursor.getInt(cursor.getColumnIndex(MovieTableHelper.ID_MOVIE));
        posterPath = cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH));
        adult = (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.ADULT)) > 0);
        overview = cursor.getString(cursor.getColumnIndex(MovieTableHelper.OVERVIEW));
        releaseDate = cursor.getString(cursor.getColumnIndex(MovieTableHelper.RELEASE_DATE));
        genreIds = convertStringtoIntArray(cursor.getString(cursor.getColumnIndex(MovieTableHelper.GENRE_IDS)));
        originalTitle = cursor.getString(cursor.getColumnIndex(MovieTableHelper.ORIGINAL_TITLE));
        originalLanguage = cursor.getString(cursor.getColumnIndex(MovieTableHelper.ORIGINAL_LANGUAGE));
        title = cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE));
        backdropPath = cursor.getString(cursor.getColumnIndex(MovieTableHelper.BACKDROP_PATH));
        popularity = cursor.getDouble(cursor.getColumnIndex(MovieTableHelper.POPULARITY));
        voteCount = cursor.getInt(cursor.getColumnIndex(MovieTableHelper.VOTE_COUNT));
        video = (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.VIDEO)) > 0);
        voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieTableHelper.VOTE_AVERAGE));
        watchLater = (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.WATCH_LATER)) > 0);


    }

    private static int[] convertStringtoIntArray(String arrayString) {
        String [] str = arrayString.split(",");
        int size = (arrayString.compareTo("[]")!=0 ? str.length : 0);
        int [] arr = new int [size];
        for(int i=0; i<size; i++) {
            String s = str[i].replace("[","").replace("]","").trim();
            try {
                arr[i] = Integer.parseInt(s);
            }catch (Exception e)
            {
                Log.e("Malformed Json", "convertStringtoIntArray: " + arrayString,e );
                arr[i] = 0;
            }
        }
        return arr;
    }


    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public boolean isWatchLater() {
        return watchLater;
    }

    public void setWatchLater(boolean watchLater) {
        this.watchLater = watchLater;
    }

    public ContentValues movieValues() {

        ContentValues values = new ContentValues();
        values.put(MovieTableHelper.ID_MOVIE, id);
        values.put(MovieTableHelper.POSTER_PATH, posterPath);
        values.put(MovieTableHelper.ADULT, adult);
        values.put(MovieTableHelper.OVERVIEW, overview);
        values.put(MovieTableHelper.RELEASE_DATE, releaseDate);
        values.put(MovieTableHelper.GENRE_IDS, String.valueOf(genreIds));
        values.put(MovieTableHelper.ORIGINAL_TITLE, originalTitle);
        values.put(MovieTableHelper.ORIGINAL_LANGUAGE, originalLanguage);
        values.put(MovieTableHelper.TITLE, title);
        values.put(MovieTableHelper.BACKDROP_PATH, backdropPath);
        values.put(MovieTableHelper.POPULARITY, popularity);
        values.put(MovieTableHelper.VOTE_COUNT, voteCount);
        values.put(MovieTableHelper.VIDEO, video);
        values.put(MovieTableHelper.VOTE_AVERAGE, voteAverage);
        values.put(MovieTableHelper.WATCH_LATER, watchLater);
        return values;
    }

}
