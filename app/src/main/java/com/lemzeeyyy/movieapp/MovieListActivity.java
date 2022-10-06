package com.lemzeeyyy.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.lemzeeyyy.movieapp.adapter.MovieRecyclerAdapter;
import com.lemzeeyyy.movieapp.adapter.OnMovieListener;
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

public class MovieListActivity extends AppCompatActivity implements OnMovieListener {
    private MovieListViewModel movieListViewModel;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchView searchView;
    boolean isPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        setUpSearchView();



       movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

       searchMovieApi("Fast",1);
        observeChange();
        ConfigureRecyclerView();

    }
    private void observeChange(){
        movieListViewModel.getMyMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if (movieModels!=null){
                    for (MovieModel movies :
                            movieModels) {
                        movieRecyclerAdapter.setMovies(movieModels);
                    }
                }
            }
        });
    }
    public void searchMovieApi(String query, int pageNumber){
        movieListViewModel.searchMovieApi(query, pageNumber);
    }

    private void ConfigureRecyclerView() {

        movieRecyclerAdapter = new MovieRecyclerAdapter(this);
        recyclerView.setAdapter(movieRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(movieRecyclerAdapter);

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

    public void setUpSearchView(){

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              isPopular = false;
                Log.v("Tagy", "ispop: " +isPopular);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                movieListViewModel.searchMovieApi(query,1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMovieClick(int position) {
        Toast.makeText(this, "Position is "+position, Toast.LENGTH_SHORT)
                .show();
        Log.d("Lemzy", "onMovieClick: "+position);

    }

    @Override
    public void onCategoryClick(String category) {

    }

}