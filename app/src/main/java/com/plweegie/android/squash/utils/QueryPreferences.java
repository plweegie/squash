package com.plweegie.android.squash.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;

import java.text.ParseException;
import java.util.Comparator;

import javax.inject.Inject;


public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_DATE = "lastResultDate";

    SharedPreferences mPrefs;

    @Inject
    public QueryPreferences(SharedPreferences prefs) {
        this.mPrefs = prefs;
    }

    public String getStoredQuery() {
        return mPrefs.getString(PREF_SEARCH_QUERY, null);
    }

    public void setStoredQuery(String query) {
        mPrefs.edit().putString(PREF_SEARCH_QUERY, query).apply();
    }

    public String getStoredAccessToken() {
        return mPrefs.getString(AuthUtils.PREFERENCE_NAME, "");
    }

    public long getLastResultDate() {
        return mPrefs.getLong(PREF_LAST_RESULT_DATE, 0L);
    }

    public void setLastResultDate(long date) {
        mPrefs.edit().putLong(PREF_LAST_RESULT_DATE, date).apply();
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
            return Long.valueOf(bStamp).compareTo(aStamp);
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
                aStamp = DateUtils.convertToTimestamp(a.getCommitBody().getCommitter().getDate());
                bStamp = DateUtils.convertToTimestamp(b.getCommitBody().getCommitter().getDate());
            } catch(ParseException e) {
                Log.e("QueryPreferences", "Date parser error: " + e);
            }
            return Long.valueOf(bStamp).compareTo(aStamp);
        }
    }
}
