package com.plweegie.android.squash.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RepoReadme(
        @Expose
        val content: String,

        @Expose
        @SerializedName("html_url") val htmlUrl: String,

        @Expose
        @SerializedName("download_url") val downloadUrl: String
)