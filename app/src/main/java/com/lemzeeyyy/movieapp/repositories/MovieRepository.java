package com.lemzeeyyy.movieapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lemzeeyyy.movieapp.API.MovieApiClient;
import com.lemzeeyyy.movieapp.model.MovieModel;

import java.util.List;

public class MovieRepository {
    private MovieApiClient movieApiClient;
    private static MovieRepository instance;
    private String mQuery;
    private int mPageNumber;


    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
        movieApiClient = MovieApiClient.getInstance();
    }


    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }

    public void searchMovieApi(String query, int pageNumber){
        mQuery = query;
        mPageNumber = pageNumber;
        MovieApiClient.getInstance().searchMovieApi(query, pageNumber);
    }
    public LiveData<List<MovieModel>> getPop(){
        return movieApiClient.getPop();
    }

    // 2- Calling the method in repository
    public void searchMoviePop(int pageNumber){

        mPageNumber = pageNumber;
        movieApiClient.searchMoviesPop( pageNumber);
    }

    public void searchNextPage(){
        searchMovieApi(mQuery, mPageNumber+1);
    }
}
