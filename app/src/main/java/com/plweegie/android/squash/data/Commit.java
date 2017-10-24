package com.plweegie.android.squash.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Commit {

    @SerializedName("sha")
    @Expose
    private String sha;
    @SerializedName("commit")
    @Expose
    private CommitBody commitBody;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    @SerializedName("author")
    @Expose
    private Author author;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitBody getCommitBody() {
        return commitBody;
    }

    public void setCommitBody(CommitBody commitBody) {
        this.commitBody = commitBody;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }


    public static class CommitBody {

        @SerializedName("author")
        @Expose
        private CommitBodyAuthor commitBodyAuthor;
        @SerializedName("message")
        @Expose
        private String message;

        public CommitBodyAuthor getCommitBodyAuthor() {
            return commitBodyAuthor;
        }

        public void setCommitBodyAuthor(CommitBodyAuthor commitBodyAuthor) {
            this.commitBodyAuthor = commitBodyAuthor;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static class CommitBodyAuthor{

            @SerializedName("date")
            @Expose
            private String date;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }
        }
    }

    public static class Author {

        @SerializedName("login")
        @Expose
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }
}
