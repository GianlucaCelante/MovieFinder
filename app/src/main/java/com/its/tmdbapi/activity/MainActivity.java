package com.its.tmdbapi.activity;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.its.tmdbapi.OnVerticalScrollListener;
import com.its.tmdbapi.R;
import com.its.tmdbapi.adapters.MovieAdapter;
import com.its.tmdbapi.database.MovieContentProvider;
import com.its.tmdbapi.database.MovieTableHelper;
import com.its.tmdbapi.database.model.Movie;
import com.its.tmdbapi.rest.IWebServer;
import com.its.tmdbapi.rest.WebService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IWebServer {

    public static final String MyPREFERENCES = "MyPreferences";
    public static final String PAGEKEY ="pageKey";
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private WebService webService;
    private ProgressBar progressBar;
    private Parcelable listState;
    int orientation;
    int defaultPage = 1;
    int lastPage = 1;
    List<Movie> fullList;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Toolbar toolbar;
    Cursor cursor;
    String language;
    ImageView favouriteBookmark;
    SearchView searchView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(listState != null){
            listState=savedInstanceState.getParcelable("ListState");
        }

        recyclerView = findViewById(R.id.listMovies);
        progressBar = findViewById(R.id.progressBar);
        webService = WebService.getInstance();
        adapter = new MovieAdapter(this);
        toolbar = findViewById(R.id.toolbar);

        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FF000E\">" + getString(R.string.title_main_activity) + "</font>")));

        ConstraintLayout myLinear = findViewById(R.id.favoriteStarIncluded);
        favouriteBookmark = myLinear.findViewById(R.id.bookmarkImageView);

        language = Locale.getDefault().toLanguageTag();

        fullList = new ArrayList<>();

        orientation = getResources().getConfiguration().orientation;

        setupRecyclerView();

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        if (!isNetworkConnected()){

            cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null, null, null, null);

            cursor.moveToFirst();

            if (cursor.getCount() == 0){


                Toast.makeText(this, R.string.first_launch, Toast.LENGTH_LONG).show();

            } else {

            loadFromDb();

            }

        } else {

            loadLastFetchedPage();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        listState = layoutManager.onSaveInstanceState();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                null, null, null);

        if (cursor.getCount() == 0) {

            Log.d("ciao", "setFavouriteBookmark: ");

        } else {

            cursor.moveToFirst();

            while (cursor.moveToNext()) {

                boolean isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(MovieTableHelper.WATCH_LATER)) > 0;

                if (isFavorite) {


                    favouriteBookmark.setImageResource(R.drawable.bookmark_yellow_60dp);
                    favouriteBookmark.setAlpha(0.75f);

                } else {

                    favouriteBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    favouriteBookmark.setAlpha(0.45f);
                }

            }


        }


            recyclerView.getLayoutManager().onRestoreInstanceState(listState);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();


        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapter.setData(fullList);

                    EditText et= findViewById(R.id.search_src_text);

                    et.setText("");

                    searchView.setQuery("", false);

                    searchView.onActionViewCollapsed();

                    search.collapseActionView();
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    String[] wildCardReplacements = {"%" + query + "%"};

                    Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                            MovieTableHelper.TITLE + " LIKE ?", wildCardReplacements, null);


                    if (cursor.getCount() == 0) {
                        Toast.makeText(MainActivity.this,R.string.movie_not_found, Toast.LENGTH_SHORT).show();



                    } else {


                        List<Movie> movies = new ArrayList<>();

                        cursor.move(-1);
                        while (cursor.moveToNext()) {

                            movies.add(new Movie(cursor));
                        }

                        adapter.setData(movies);
                        recyclerView.setAdapter(adapter);

                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
/*
                    List<Movie> searchedMovies = new ArrayList<>();

                    String likeString = "= %" + query + "%";

                    Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI,
                            null, MovieTableHelper.TITLE + " =?", new String[]{likeString}, null);


                    if(cursor.getCount() == 0){

                        Toast.makeText(MainActivity.this, R.string.found_nothing, Toast.LENGTH_SHORT).show();


                    } else {

                        cursor.moveToFirst();

                        while (cursor.moveToNext()){

                            searchedMovies.add(new Movie(cursor));
                        }
                    }


                    adapter.setData(searchedMovies);*/
                    return true;

                }

            });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this, FavoriteMoviesActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMoviesFetched(boolean success, List<Movie> response, int errorCode, String errorMessage) {

        if(isNetworkConnected()) {

            if (success) {

                for (Movie movie : response) {

                    fullList.add(movie);

                    ContentValues values = movie.movieValues();
                    getContentResolver().insert(MovieContentProvider.MOVIES_URI, values);
                }

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setData(fullList);


            }
        }
    }

    public void setupRecyclerView() {

        recyclerView.setVisibility(View.GONE);

        layoutRecyclerOffOrientation();

        recyclerView.setAdapter(adapter);
        layoutManager.onRestoreInstanceState(listState);


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

                Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                        null, null, null);


                if (cursor.getCount() == 0) {

                    Log.d("ciao", "setFavouriteBookmark: ");

                } else {

                    cursor.moveToFirst();

                    while (cursor.moveToNext()) {

                            boolean isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(MovieTableHelper.WATCH_LATER)) > 0;

                            if (isFavorite) {


                                favouriteBookmark.setImageResource(R.drawable.bookmark_yellow_60dp);
                                favouriteBookmark.setAlpha(0.75f);

                            } else {

                                favouriteBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                                favouriteBookmark.setAlpha(0.45f);
                            }

                        }

                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

                Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                        null, null, null);


                if (cursor.getCount() == 0) {

                    Log.d("ciao", "setFavouriteBookmark: ");

                } else {

                    cursor.moveToFirst();

                    while (cursor.moveToNext()) {

                        boolean valueWatchLater = cursor.getInt(cursor.getColumnIndexOrThrow(MovieTableHelper.WATCH_LATER)) > 0;
                        editor = sharedPreferences.edit();
                        editor.putBoolean(MovieTableHelper.WATCH_LATER, valueWatchLater);
                        editor.apply();
                    }

                }
            }
        });

        recyclerView.addOnScrollListener(new OnVerticalScrollListener() {
                 @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();

                Toast.makeText(MainActivity.this, R.string.fetching_more_movies, Toast.LENGTH_SHORT).show();
                lastPage++;

                webService.getPopular(language, lastPage, MainActivity.this);

                adapter.notifyDataSetChanged();

                editor = sharedPreferences.edit();
                editor.putInt(PAGEKEY, lastPage);
                editor.apply();

            }
        });

        adapter.setListener(new MovieAdapter.IMovieListener() {
            @Override
            public void onMovieSelected(Movie movie) {
                Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
                intent.putExtra(MovieTableHelper.ID_MOVIE, movie.getId());
                intent.putExtra(MovieTableHelper.GENRE_IDS, movie.getGenreIds());
                startActivity(intent);
            }

        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private void loadLastFetchedPage(){

        lastPage = sharedPreferences.getInt(PAGEKEY, 1);

        if (lastPage == 1){

            webService.getPopular(language, defaultPage,this);
        } else {

            webService.getPopular(language, lastPage, this);
        }


    }

    private void loadFromDb(){


        List<Movie> movies = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null, null, null, null);
        cursor.move(-1);

        while (cursor.moveToNext())

            movies.add(new Movie(cursor));

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.setData(movies);

    }

    public void layoutRecyclerOffOrientation(){

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            layoutManager = new GridLayoutManager(
                    this, 3, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);


        } else {

            layoutManager = new GridLayoutManager(
                    this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

        }
    }
}

