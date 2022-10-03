package com.lemzeeyyy.movieapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lemzeeyyy.movieapp.API.MovieApiClient;
import com.lemzeeyyy.movieapp.model.MovieModel;

import java.util.List;

public class MovieRepository {
    private MovieApiClient movieApiClient;
    private static MovieRepository instance;


    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
        movieApiClient = MovieApiClient.getInstance();
    }

    public MovieRepository(MovieApiClient movieApiClient) {
        this.movieApiClient = movieApiClient;
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }
}
