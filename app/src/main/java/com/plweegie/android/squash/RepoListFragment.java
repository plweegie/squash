/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plweegie.android.squash.adapters.RepoAdapter;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.utils.GitHubService;
import com.plweegie.android.squash.utils.Injectors;
import com.plweegie.android.squash.utils.PaginationScrollListener;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RepoListFragment extends Fragment implements RepoAdapter.RepoAdapterOnClickHandler {
    
    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static final int MAXIMUM_LIST_LENGTH = 30;
    private static final int START_PAGE = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = START_PAGE;

    private RepoRepository mDataRepository;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private RepoAdapter mAdapter;
    private ProgressBar mIndicator;
    private InputMethodManager mImm;

    private GitHubService mService;

    public static RepoListFragment newInstance() {
        return new RepoListFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataRepository = Injectors.provideRepository(getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, parent, false);

        mImm = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mRecyclerView = v.findViewById(R.id.commits_recycler_view);
        mIndicator = v.findViewById(R.id.load_indicator);
        mIndicator.setVisibility(View.GONE);

        mAdapter = new RepoAdapter(getActivity(), this);
        mManager = new LinearLayoutManager(getActivity());
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                updateUI();
            }

            @Override
            public int getTotalPageCount() {
                return MAXIMUM_LIST_LENGTH;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mService = retrofit.create(GitHubService.class);
        updateUI();
        
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.repo_list_menu, menu);
        
        final MenuItem searchItem = menu.findItem(R.id.repo_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String string) {
                mImm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                
                mRecyclerView.setVisibility(View.INVISIBLE);
                mIndicator.setVisibility(View.VISIBLE);
                
                QueryPreferences.setStoredQuery(getActivity(), string);
                isLoading = false;
                isLastPage = false;
                currentPage = START_PAGE;

                mAdapter.clear();
                updateUI();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String string) {
                return true;
            }
        });
        
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position) {
        mDataRepository.addFavorite(mAdapter.getItem(position));
        Log.d("RepoListFragment", "pos " + position + " clicked");
    }
    
    private void updateUI() {

        final String apiQuery = QueryPreferences.getStoredQuery(getActivity());
        Call<List<RepoEntry>> call = mService.getRepos(apiQuery, currentPage);
        call.enqueue(new Callback<List<RepoEntry>>() {

            @Override
            public void onResponse(Call<List<RepoEntry>> call,
                                   Response<List<RepoEntry>> response) {

                isLoading = false;

                if (response.body() == null) {
                    Toast.makeText(getActivity(), "No repositories found for " + apiQuery,
                            Toast.LENGTH_SHORT).show();
                    mIndicator.setVisibility(View.GONE);
                    return;
                }

                List<RepoEntry> repos = response.body();

                if (repos.size() > 0) {
                    mAdapter.addAll(repos);
                }

                if (repos.size() < MAXIMUM_LIST_LENGTH) {
                    isLastPage = true;
                }

                mRecyclerView.setVisibility(View.VISIBLE);
                mIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RepoEntry>> call, Throwable t) {
                Log.e("RepoListFragment", "Retrofit error: " + t);
            }
        });
    }

}
