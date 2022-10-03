package com.lemzeeyyy.movieapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lemzeeyyy.movieapp.model.MovieModel;

import java.util.List;

public class MovieRepository {

    private static MovieRepository instance;

    private MutableLiveData<List<MovieModel>> myMovies ;
    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
        myMovies = new MutableLiveData<>();
    }
    public LiveData<List<MovieModel>> getMovies(){
        return myMovies;
    }
}
