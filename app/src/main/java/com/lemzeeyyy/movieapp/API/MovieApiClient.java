package com.lemzeeyyy.movieapp.API;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lemzeeyyy.movieapp.AppExecutors;
import com.lemzeeyyy.movieapp.model.MovieModel;
import com.lemzeeyyy.movieapp.request.Service;
import com.lemzeeyyy.movieapp.response.MovieSearchResponse;
import com.lemzeeyyy.movieapp.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    private MutableLiveData<List<MovieModel>> myMovies ;

    private static MovieApiClient instance;

    private RetrieveMoviesRunnable retrieveMoviesRunnable;
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

    public void searchMovieApi(String query, int pageNumber){
        if (retrieveMoviesRunnable!=null){
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query, pageNumber);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //Canceling the retrofit call
                myHandler.cancel(true);
            }
        },3000, TimeUnit.MILLISECONDS);
    }

    //Retrieving data from RestApi by runnable class
    private class RetrieveMoviesRunnable implements Runnable{
        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {

           try{
               Response response = getMovies(query,pageNumber).execute();
               if(cancelRequest){
                   return;
               }
               if (response.code() == 200){
                   List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());
                   if (pageNumber == 1){
                       // Sending data to live data
                       // PostValue: used for background thread
                       // setValue: not for background thread
                       myMovies.postValue(list);

                   }else{
                       List<MovieModel> currentMovies = myMovies.getValue();
                       currentMovies.addAll(list);
                       myMovies.postValue(currentMovies);

                   }

               }else{
                   String error = response.errorBody().toString();
                   Log.v("Tagy", "Error " +error);
                   myMovies.postValue(null);

               }
           } catch (IOException e) {
               e.printStackTrace();
               myMovies.postValue(null);
           }



        }


        private Call<MovieSearchResponse> getMovies(String query, int pageNumber){
            return Service.getMovieApi().searchMovie(
                    Credentials.API_KEY,
                    query,
                    pageNumber
            );
        }

        private void setCancelRequest(){
            Log.d("CancelReq", "setCancelRequest: Cancelling Search Request");
            cancelRequest = true;
        }
    }
}
