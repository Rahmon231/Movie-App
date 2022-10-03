package com.lemzeeyyy.movieapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lemzeeyyy.movieapp.model.MovieModel;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private MutableLiveData<List<MovieModel>> myMovies = new MutableLiveData<>();


    public MovieListViewModel() {
    }

    public LiveData<List<MovieModel>> getMyMovies() {
        return myMovies;
    }
}
