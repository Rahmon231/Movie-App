package com.lemzeeyyy.movieapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.lemzeeyyy.movieapp.model.MovieModel;
import com.lemzeeyyy.movieapp.API.MovieApi;
import com.lemzeeyyy.movieapp.request.Service;
import com.lemzeeyyy.movieapp.response.MovieSearchResponse;
import com.lemzeeyyy.movieapp.utils.Credentials;
import com.lemzeeyyy.movieapp.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity {
    private MovieListViewModel movieListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
    }
    private void observeChange(){
        movieListViewModel.getMyMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {

            }
        });
    }

    private void getMovieByIdSearch() {
        MovieApi movieApi = Service.getMovieApi();
        Call<MovieModel> responseCall = movieApi.getMovie(
                550,
                Credentials.API_KEY
        );
        responseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if(response.code() == 200){
                   // Log.d("TAG", "onResponse: "+response.body());
                    MovieModel model = response.body();
                    Log.d("LEMZY", "onResponse: "+model.getTitle());
                }else {
                    try {
                        Log.d("LEMZY", "onResponse: " + response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {

            }
        });
    }

    private void getMovieSearchResponse() {
        MovieApi movieApi = Service.getMovieApi();
        Call<MovieSearchResponse> responseCall = movieApi.searchMovie(
                Credentials.API_KEY,
                "Fight",
                1
        );
        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.code() == 200) {
                    Log.d("TAGY", "onResponse: " + response.body().toString());

                    List<MovieModel> movies = new ArrayList<>(response.body().getMovies());
                    for (MovieModel movieResponse : movies) {
                        Log.d("TAGY", "onResponse: Movie Title : " + movieResponse.getTitle());
                    }

                }else {
                    try{
                        Log.d("TAGY", "onResponse: "+response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {

            }
        });
    }
}