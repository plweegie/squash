package com.plweegie.android.squash.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jan on 17/09/17.
 */

@Entity(tableName = "repos", indices = {@Index(value = "name", unique = true)})

public class RepoEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("id")
    @Expose
    private long repoId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("stargazers_count")
    @Expose
    private int stargazersCount;
    @SerializedName("watchers_count")
    @Expose
    private int watchersCount;
    @SerializedName("owner")
    @Expose
    @Embedded
    private Owner owner;
    private String lastCommitSha;

    @Ignore
    public RepoEntry() {}

    @Ignore
    public RepoEntry(long repoId, String name, String language, int stargazersCount,
                     int watchersCount, String ownerLogin) {
        this.repoId = repoId;
        this.name = name;
        this.language = language;
        this.stargazersCount = stargazersCount;
        this.watchersCount = watchersCount;
        this.owner.login = ownerLogin;
        this.lastCommitSha = "";
    }

    public RepoEntry(int id, long repoId, String name, String language, int stargazersCount,
                     int watchersCount, Owner owner) {
        this.id = id;
        this.repoId = repoId;
        this.name = name;
        this.language = language;
        this.stargazersCount = stargazersCount;
        this.watchersCount = watchersCount;
        this.owner = owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setRepoId(long id) {
        this.repoId = id;
    }

    public long getRepoId() {
        return repoId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.watchersCount = watchersCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }

    public void setLastCommitSha(String lastCommitSha) {
        this.lastCommitSha = lastCommitSha;
    }

    public static class Owner {

        @SerializedName("login")
        @Expose
        private String login;

        @Ignore
        public Owner() {}

        public Owner(String login) {
            this.login = login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }
}
