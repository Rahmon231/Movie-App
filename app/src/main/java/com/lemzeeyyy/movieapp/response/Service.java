package com.lemzeeyyy.movieapp.response;

import com.lemzeeyyy.movieapp.utils.Credentials;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Credentials.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private MovieApi movieApi = retrofit.create(MovieApi.class);

    public MovieApi getMovieApi() {
        return movieApi;
    }
}
