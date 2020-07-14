package com.its.tmdbapi.activity;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.its.tmdbapi.R;
import com.its.tmdbapi.database.MovieContentProvider;
import com.its.tmdbapi.database.MovieTableHelper;
import com.its.tmdbapi.database.model.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailMovieActivity extends AppCompatActivity {

    ImageView backPoster;
    TextView titleMovie, subtitleMovie, descriptionMovie, dataUscita, ratingValue;
    Toolbar toolbar;
    RatingBar ratingBar;
    private static final String IMAGE_BASE_PATH = "https://image.tmdb.org/t/p/w500/";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        setTitle("");

        titleMovie = findViewById(R.id.titleMovieTextView);
        subtitleMovie = findViewById(R.id.originalTitleMovieTextView);
        descriptionMovie = findViewById(R.id.descriptionMovieTextView);
        backPoster = findViewById(R.id.backPosterImageView);
        ratingBar = findViewById(R.id.ratingValueRatingBar);
        ratingValue = findViewById(R.id.ratingValueTextView);
        toolbar = findViewById(R.id.toolbar);
        dataUscita = findViewById(R.id.dataDiUscitaValueTextView);

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        int idMovie = bundle.getInt(MovieTableHelper.ID_MOVIE, -1);

        Movie movie = getMovie(idMovie);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupUI(movie);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Movie getMovie(int id){

        Movie movie = new Movie();
        String[] args = {String.valueOf(id)};
        Cursor cursor = getContentResolver().query(MovieContentProvider.MOVIES_URI, null, MovieTableHelper.ID_MOVIE + " = ?", args, null);

        try{
            cursor.moveToFirst();
            movie =  new Movie(cursor);

        } catch (Exception e) {

            if (!(cursor.getCount()>0))
                Log.e(getClass().getName(), "Error retrieving movies from local storage", e);
        }

        return movie;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupUI(Movie movie) {

        //color RatingBar
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        double rating = movie.getVoteAverage();

        //rating : 10 = x : 5
        double convertedRating = rating/10 * 5;
        Log.d("ciao", "setupUI: " + convertedRating);


        //data di uscita
        String formattedDate = null;
        String fetchedDate = movie.getReleaseDate();
        String language = Locale.getDefault().toLanguageTag();

        if (language.equalsIgnoreCase("us-US")){

            dataUscita.setText(fetchedDate);

        } else if(language.equalsIgnoreCase("it-IT")){

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(fetchedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);


        }

        ratingBar.setRating((float) convertedRating);
        ratingValue.setText("(" + rating + "/10)");
        subtitleMovie.setText(movie.getOriginalTitle());
        dataUscita.setText(formattedDate);
        titleMovie.setText(movie.getTitle());

        String description = movie.getOverview();

        if(!description.isEmpty()){

            descriptionMovie.setText(movie.getOverview());

        } else {

            descriptionMovie.setText(R.string.missing_description);
            descriptionMovie.setFontFeatureSettings("sans-serif-light");
        }

        Glide.with(this)
                .load(IMAGE_BASE_PATH + movie.getBackdropPath())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_noun_pop_corn_2663344)
                .error(R.drawable.ic_broken_image)
                .into(backPoster);

        backPoster.setVisibility(View.VISIBLE);
    }
}
