package com.its.tmdbapi.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.its.tmdbapi.R;
import com.its.tmdbapi.database.MovieContentProvider;
import com.its.tmdbapi.database.MovieTableHelper;
import com.its.tmdbapi.database.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private IMovieListener listener;
    private Context context;
    private static final String IMAGE_BASE_PATH = "https://image.tmdb.org/t/p/w500/";
    private boolean isWatchLater;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        holder.bind(movieList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setData(List<Movie> movies)
    {
        this.movieList = movies;
        this.notifyDataSetChanged();
    }

    public void update(List<Movie> movies){
        movieList.clear();
        movieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void setListener(IMovieListener listener)
    {
        this.listener = listener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        Movie movie;
        ImageView posterImageView, favouriteStar, placeholderMovieImageView;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.imageMovie);
            favouriteStar = itemView.findViewById(R.id.bookmarkImageView);
            placeholderMovieImageView = itemView.findViewById(R.id.placeholderMovie);
        }

        public void bind(final Movie movie, final IMovieListener listener) {

            this.movie = movie;

            isWatchLater = movie.isWatchLater();

            Glide.with(itemView)
                    .load(IMAGE_BASE_PATH + movie.getPosterPath())
                    .placeholder(R.drawable.ic_noun_pop_corn_2663344)
                    .error(R.drawable.ic_broken_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(posterImageView);


            if (listener != null)
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onMovieSelected(MovieViewHolder.this.movie);
                    }
                });

            setFavouriteBookmark(favouriteStar, movie);


            if(listener != null){

                favouriteStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isWatchLater) {

                            favouriteStar.setImageResource(R.drawable.ic_bookmark_black_24dp);
                            favouriteStar.setAlpha(0.75f);
                            isWatchLater = false;

                            ContentValues values = new ContentValues();
                            values.put(MovieTableHelper.WATCH_LATER, 1);
                            context.getContentResolver().update(MovieContentProvider.MOVIES_URI, values, MovieTableHelper.ID_MOVIE + " = " + movie.getId(), null);


                        } else {

                            favouriteStar.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            favouriteStar.setAlpha(0.45f);
                            isWatchLater = true;

                            ContentValues values = new ContentValues();
                            values.put(MovieTableHelper.WATCH_LATER, 0);
                            context.getContentResolver().update(MovieContentProvider.MOVIES_URI, values, MovieTableHelper.ID_MOVIE + " = " + movie.getId(), null);
                        }

                    }
                });
            }
        }
    }




    public void setFavouriteBookmark(ImageView favouriteStar, Movie movie){



        Cursor cursor = context.getContentResolver().query(MovieContentProvider.MOVIES_URI, null,
                null, null, null);

        if(cursor.getCount() == 0){

            Log.d("ciao", "setFavouriteBookmark: ");

        } else {

            cursor.moveToFirst();

            while(cursor.moveToNext()){

                if(movie.isWatchLater()) {

                    favouriteStar.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    favouriteStar.setAlpha(0.75f);

                } else {

                    favouriteStar.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    favouriteStar.setAlpha(0.45f);

                }
            }

        }

    }



    public interface IMovieListener {
        void onMovieSelected(Movie movie);
    }
}


