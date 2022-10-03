package com.lemzeeyyy.movieapp.API;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lemzeeyyy.movieapp.model.MovieModel;

import java.util.List;

public class MovieApiClient {
    private MutableLiveData<List<MovieModel>> myMovies ;

    private static MovieApiClient instance;
    public static MovieApiClient getInstance(){
        if (instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        myMovies = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return myMovies;
    }
}
