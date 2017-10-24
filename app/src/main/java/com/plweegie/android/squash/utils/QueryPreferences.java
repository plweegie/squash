package com.plweegie.android.squash.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;

import java.text.ParseException;
import java.util.Comparator;


public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_SHA = "lastResultSha";

    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getLastResultSha(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_SHA, null);
    }

    public static void setLastResultSha(Context context, String lastResultSha) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_SHA, lastResultSha)
                .apply();
    }

    public static class RepoNameComparator implements Comparator<RepoEntry> {
        @Override
        public int compare(RepoEntry a, RepoEntry b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    public static class RepoCreatedComparator implements Comparator<RepoEntry> {
        @Override
        public int compare(RepoEntry a, RepoEntry b) {
            long bStamp = 0L;
            long aStamp = 0L;
            try {
                aStamp = DateUtils.convertToTimestamp(a.getCreatedAt());
                bStamp = DateUtils.convertToTimestamp(b.getCreatedAt());
            } catch(ParseException e) {
                Log.e("QueryPreferences", "Date parser error: " + e);
            }
            return (int) bStamp - (int) aStamp;
        }
    }

    public static class RepoStarsComparator implements Comparator<RepoEntry> {
        @Override
        public int compare(RepoEntry a, RepoEntry b) {
            Integer aStars = a.getStargazersCount();
            Integer bStars = b.getStargazersCount();
            return bStars.compareTo(aStars);
        }
    }

    public static class CommitCreatedComparator implements Comparator<Commit> {
        @Override
        public int compare(Commit a, Commit b) {
            long bStamp = 0L;
            long aStamp = 0L;
            try {
                aStamp = DateUtils.convertToTimestamp(a.getCommitBody().getCommitBodyAuthor().getDate());
                bStamp = DateUtils.convertToTimestamp(b.getCommitBody().getCommitBodyAuthor().getDate());
            } catch(ParseException e) {
                Log.e("QueryPreferences", "Date parser error: " + e);
            }
            return (int) bStamp - (int) aStamp;
        }
    }
}
