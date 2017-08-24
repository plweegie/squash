/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.utils;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface GitHubService {
    @GET("users/{username}/repos")
    Call<List<Repository>> getRepos(@Path("username") String userName,
            @Query("per_page") int perPage);
}
