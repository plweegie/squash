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

    public static class CommitBody {

        @SerializedName("author")
        @Expose
        private CommitBodyAuthor commitBodyAuthor;
        @SerializedName("committer")
        @Expose
        private Committer committer;
        @SerializedName("message")
        @Expose
        private String message;

        public CommitBodyAuthor getCommitBodyAuthor() {
            return commitBodyAuthor;
        }

        public void setCommitBodyAuthor(CommitBodyAuthor commitBodyAuthor) {
            this.commitBodyAuthor = commitBodyAuthor;
        }

        public Committer getCommitter() {
            return committer;
        }

        public void setCommitter(Committer committer) {
            this.committer = committer;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static class CommitBodyAuthor{

            @SerializedName("name")
            @Expose
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class Committer {

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
}
