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
    private MutableLiveData<List<MovieModel>> mMoviesPop ;
    private static MovieApiClient instance;

    private RetrieveMoviesRunnable retrieveMoviesRunnable;
    private RetrieveMoviesRunnablePop retrieveMoviesRunnablePop;


    public static MovieApiClient getInstance(){
        if (instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        myMovies = new MutableLiveData<>();
        mMoviesPop = new MutableLiveData<>();
//        Log.d("Checking", "MovieApiClient: "+myMovies.getValue());
//        Log.d("Checking", "MovieApiClient: "+mMoviesPop.getValue());
//        Log.d("Checking", "MovieApiClient: "+getPop().getValue());
//        Log.d("Checking", "MovieApiClient: "+getMovies().getValue());
//        searchMoviesPop(1);
//        searchMovieApi("Fast",1);

    }

    public LiveData<List<MovieModel>> getMovies(){
       // Log.d("MoviesApi", "getMovies: "+myMovies.getValue().size());
        return myMovies;
    }
    public LiveData<List<MovieModel>> getPop(){
       // Log.d("PopularMoviesList", "getPop: "+mMoviesPop.getValue());
        return mMoviesPop;
    }

    public void searchMovieApi(String query, int pageNumber){
        if (retrieveMoviesRunnable!=null){
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query, pageNumber);
//        try {
//            Log.d("Checking", "searchMovieApi: "+retrieveMoviesRunnable.getMovies("Fast",1).execute().body().getMovies());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnable);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //Canceling the retrofit call
                myHandler.cancel(true);
            }
        },3000, TimeUnit.MILLISECONDS);
    }

    public void searchMoviesPop(int pageNumber) {

        if (retrieveMoviesRunnablePop != null){
            retrieveMoviesRunnablePop = null;
        }

        retrieveMoviesRunnablePop = new RetrieveMoviesRunnablePop(pageNumber);

        final Future myHandler2 = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnablePop);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler2.cancel(true);

            }
        }, 1000, TimeUnit.MILLISECONDS);


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

    private class RetrieveMoviesRunnablePop implements Runnable{
        private int pageNumber;
        boolean cancelRequest;


        public RetrieveMoviesRunnablePop(int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try{

                Response response2 = getPop(pageNumber).execute();

                if (cancelRequest) {

                    return;
                }
                if (response2.code() == 200){
                    assert response2.body() != null;
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response2.body()).getMovies());
                    for (MovieModel movies :
                            list) {
                        Log.d("Checking Movies", "run: "+movies.getTitle());
                    }
                    if (pageNumber == 1){
                        // Sending data to live data
                        // PostValue: used for background thread
                        // setValue: not for background thread
                        mMoviesPop.postValue(list);



                    }else{
                        List<MovieModel> currentMovies = mMoviesPop.getValue();
                        assert currentMovies != null;
                        currentMovies.addAll(list);
                        mMoviesPop.postValue(currentMovies);



                    }

                }else{
                    String error = response2.errorBody().string();
                    Log.v("Tagy", "Error " +error);
                    mMoviesPop.postValue(null);

                }

            } catch (IOException e) {
                e.printStackTrace();
                mMoviesPop.postValue(null);

            }




        }

        // Search Method/ query
        private Call<MovieSearchResponse> getPop( int pageNumber){
            return Service.getMovieApi().getPopular(
                    Credentials.API_KEY,
                    pageNumber

            );

        }
        private void cancelRequest(){
            Log.v("Tag", "Cancelling Search Request" );
            cancelRequest = true;
        }





    }
}
