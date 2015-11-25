package com.pascalwelsch.getreactive.retrofit;


import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Created by pascalwelsch on 10/24/15.
 */
public interface GitApiInterface {

    @GET("/search/repositories")
    Call<GitHubResponse<Repository>> searchRepositories(
            @Query("q") String name);

    @GET("/search/repositories")
    Observable<GitHubResponse<Repository>> searchRepositoriesObservable(@Query("q") String name);

    @GET("/search/repositories")
    Single<GitHubResponse<Repository>> searchRepositoriesSingle(
            @Query("q") String name);

    @GET("/search/users")
    Single<GitHubResponse<User>> searchUser(@Query("q") String name);
}
