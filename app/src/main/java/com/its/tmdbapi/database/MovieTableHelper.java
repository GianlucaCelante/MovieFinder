package com.its.tmdbapi.database;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "movies";
    public static final String ID_MOVIE = "ID_MOVIE";
    public static final String POSTER_PATH = "POSTER_PATH";
    public static final String ADULT = "ADULT";
    public static final String OVERVIEW = "OVERVIEW";
    public static final String RELEASE_DATE = "RELEASE_DATE";
    public static final String GENRE_IDS = "GENRE_IDS";
    public static final String ORIGINAL_TITLE = "ORIGINAL_TITLE";
    public static final String ORIGINAL_LANGUAGE = "ORIGINAL_LANGUAGE";
    public static final String TITLE = "TITLE";
    public static final String BACKDROP_PATH = "BACKDROP_PATH";
    public static final String POPULARITY = "POPULARITY";
    public static final String VOTE_COUNT = "VOTE_COUNT";
    public static final String VIDEO = "VIDEO";
    public static final String VOTE_AVERAGE = "VOTE_AVERAGE";
    public static final String WATCH_LATER = "WATCH_LATER";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +

            ID_MOVIE + " INTEGER PRIMARY KEY , " +
            POSTER_PATH + " TEXT , " +
            ADULT + " INTEGER DEFAULT 0 , " +
            OVERVIEW + " TEXT , " +
            RELEASE_DATE + " TEXT , " +
            GENRE_IDS + " TEXT ," +
            ORIGINAL_TITLE + " TEXT ," +
            ORIGINAL_LANGUAGE + " TEXT ," +
            TITLE + " TEXT ," +
            BACKDROP_PATH + " TEXT ," +
            POPULARITY + " REAL ," +
            VOTE_COUNT + " INTEGER ," +
            VIDEO + " INTEGER ," +
            VOTE_AVERAGE + " REAL , " +
            WATCH_LATER + " INTEGER DEFAULT 0 ," +
            " UNIQUE ( " + ID_MOVIE + " ) ON CONFLICT REPLACE ) ;"

    ;
}
