/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.utils;

import com.plweegie.android.squash.data.RepoEntry;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface GitHubService {
    @GET("users/{username}/repos")
    Call<List<RepoEntry>> getRepos(@Path("username") String userName,
                                   @Query("per_page") int perPage);
    
    @GET("repos/{owner}/{repo}/commits")
    Call<List<Commit>> getCommits(@Path("owner") String owner,
            @Path("repo") String repo, @Query("per_page") int perPage);
}
