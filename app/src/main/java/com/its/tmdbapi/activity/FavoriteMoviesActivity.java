package com.its.tmdbapi.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.its.tmdbapi.R;
import com.its.tmdbapi.adapters.MovieAdapter;
import com.its.tmdbapi.database.MovieContentProvider;
import com.its.tmdbapi.database.MovieTableHelper;
import com.its.tmdbapi.database.model.Movie;
import com.its.tmdbapi.fragments.ConfirmDialogFragment;
import com.its.tmdbapi.fragments.ConfirmDialogFragmentListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMoviesActivity extends AppCompatActivity implements ConfirmDialogFragmentListener {

    private MovieAdapter adapter;
    int orientation;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView listFavoriteMovies;
    ProgressBar progressBar;
    TextView placeholder;
    List<Movie> listFavorite;
    Cursor cursor;
    ImageView bookmark;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        setTitle("");

        listFavorite = new ArrayList<>();

        bookmark = findViewById(R.id.bookmarkImageView);

        listFavoriteMovies = findViewById(R.id.listFavoriteMovies);
        progressBar = findViewById(R.id.progressFavoriteBar);
        placeholder = findViewById(R.id.placeholderText);

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new MovieAdapter(this);

        orientation = getResources().getConfiguration().orientation;
        setupRecyclerView();

        cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                MovieTableHelper.WATCH_LATER + " = " + 1, null, MovieTableHelper.TITLE + " ASC");

        if(cursor.getCount() == 0){

            emptyList();

        } else {

            while (cursor.moveToNext()){

                listFavorite.add(new Movie(cursor));

            }

            progressBar.setVisibility(View.GONE);
            listFavoriteMovies.setVisibility(View.VISIBLE);
            adapter.setData(listFavorite);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setupRecyclerView() {

        listFavoriteMovies.setVisibility(View.GONE);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            layoutManager = new GridLayoutManager(
                    this, 3, GridLayoutManager.VERTICAL, false);
            listFavoriteMovies.setLayoutManager(layoutManager);


        } else {

            layoutManager = new GridLayoutManager(
                    this, 2, GridLayoutManager.VERTICAL, false);
            listFavoriteMovies.setLayoutManager(layoutManager);

        }

        listFavoriteMovies.setAdapter(adapter);

        adapter.setListener(new MovieAdapter.IMovieListener() {
            @Override
            public void onMovieSelected(Movie movie) {

                Intent intent = new Intent(FavoriteMoviesActivity.this, DetailMovieActivity.class);
                intent.putExtra(MovieTableHelper.ID_MOVIE, movie.getId());
                startActivity(intent);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourite_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_delete:

                FragmentManager fragmentManager = getSupportFragmentManager();
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment(
                        getString(R.string.remove_from_favorite_title),
                        getString(R.string.remove_from_favorite_message));

                dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
                return true;

            case R.id.action_refresh:

                List<Movie> list = new ArrayList<>();

                cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                        MovieTableHelper.WATCH_LATER + " = " + 1, null, MovieTableHelper.TITLE + " ASC");

                if(cursor.getCount() == 0){

                    listFavoriteMovies.setVisibility(View.GONE);
                    emptyList();

                } else {
                    cursor.move(-1);

                    while (cursor.moveToNext()) {


                        list.add(new Movie(cursor));

                    }

                    adapter.setData(list);
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPositivePressed() {

        ContentValues values = new ContentValues();
        values.put(MovieTableHelper.WATCH_LATER, 0);

        getContentResolver().update(MovieContentProvider.MOVIES_URI, values, null, null);

        listFavorite.clear();
        adapter.setData(listFavorite);
        emptyList();

    }

    @Override
    public void onNegativePressed() {

    }

    public void emptyList(){

        progressBar.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
    }
}
