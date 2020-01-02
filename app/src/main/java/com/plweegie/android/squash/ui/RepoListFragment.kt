/*
 * Copyright (c) 2017 Jan K Szymanski

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.plweegie.android.squash.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.crashlytics.android.Crashlytics
import com.plweegie.android.squash.App
import com.plweegie.android.squash.R
import com.plweegie.android.squash.adapters.BaseGithubAdapter
import com.plweegie.android.squash.adapters.RepoAdapter
import com.plweegie.android.squash.data.RepoEntry
import com.plweegie.android.squash.rest.GitHubService
import com.plweegie.android.squash.utils.PaginationScrollListener
import com.plweegie.android.squash.utils.QueryPreferences
import com.plweegie.android.squash.viewmodels.RepoListViewModel
import com.plweegie.android.squash.viewmodels.RepoListViewModelFactory
import kotlinx.android.synthetic.main.list_fragment.*
import javax.inject.Inject


class RepoListFragment : Fragment(), RepoAdapter.RepoAdapterOnClickHandler,
        BaseGithubAdapter.GithubAdapterOnClickListener {

    @Inject
    lateinit var service: GitHubService

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var queryPrefs: QueryPreferences

    @Inject
    lateinit var viewModelFactory: RepoListViewModelFactory

    private lateinit var viewModel: RepoListViewModel
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var imm: InputMethodManager

    private var isContentLoading = false
    private var isContentLastPage = false
    private var currentPage = START_PAGE

    private var apiQuery: String? = null

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener {
        _, _ -> repoAdapter.sort()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this, viewModelFactory).get(RepoListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        (activity?.application as App).netComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.list_fragment, parent, false)

        imm = activity?.getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager

        repoAdapter = RepoAdapter(activity).apply {
            setOnAddFavoriteListener(this@RepoListFragment)
            setListener(this@RepoListFragment)
        }
        manager = LinearLayoutManager(activity)

        apiQuery = queryPrefs.storedQuery ?: ""
        fetchRepos(apiQuery!!, currentPage)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        load_indicator?.visibility = View.GONE

        commits_recycler_view?.apply {
            setHasFixedSize(true)
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            adapter = repoAdapter
            addOnScrollListener(object : PaginationScrollListener(manager) {
                override fun loadMoreItems() {
                    isContentLoading = true
                    currentPage++
                    Toast.makeText(activity, getString(R.string.loading_more), Toast.LENGTH_SHORT)
                            .show()
                    fetchRepos(apiQuery!!, currentPage)
                }

                override fun getTotalPageCount(): Int = MAXIMUM_LIST_LENGTH

                override fun isLastPage(): Boolean = isContentLastPage

                override fun isLoading(): Boolean = isContentLoading
            })
        }

        observeUI()
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.repo_list_menu, menu)

        val searchItem = menu.findItem(R.id.repo_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(string: String): Boolean {
                imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                searchView.apply {
                    setQuery("", false)
                    isIconified = true
                }

                commits_recycler_view?.visibility = View.INVISIBLE
                load_indicator?.visibility = View.VISIBLE

                queryPrefs.storedQuery = string
                apiQuery = queryPrefs.storedQuery

                isContentLoading = false
                isContentLastPage = false
                currentPage = START_PAGE

                repoAdapter.clear()
                fetchRepos(apiQuery!!, currentPage)

                return true
            }

            override fun onQueryTextChange(string: String): Boolean = true
        })

        searchView.setOnSearchClickListener {
            val query = queryPrefs.storedQuery
            searchView.setQuery(query, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.sort_by -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onAddFavoriteClick(repo: RepoEntry) {
        viewModel.addFavorite(repo)
        queryPrefs.lastResultDate = System.currentTimeMillis()
    }

    override fun onItemClick(repo: RepoEntry) {
        val intent = RepoReadmeActivity.newIntent(activity as Context, repo.owner.login, repo.name)
        startActivity(intent)
    }

    private fun fetchRepos(query: String, page: Int) {
        viewModel.fetchData(query, page)
    }

    private fun observeUI() {
        viewModel.loadingState.observe(viewLifecycleOwner, Observer { state ->
            isContentLoading = false

            when (state) {
                is RepoListViewModel.LoadingState.Loading -> {
                    commits_recycler_view?.visibility = View.INVISIBLE
                    load_indicator?.visibility = View.VISIBLE
                }
                is RepoListViewModel.LoadingState.Failed -> {
                    Toast.makeText(activity, "No repositories found for $apiQuery",
                            Toast.LENGTH_SHORT).show()
                    load_indicator?.visibility = View.GONE
                    Crashlytics.log(1, "RepoListFragment", "Retrofit error")
                    Crashlytics.logException(state.exception)
                }
                is RepoListViewModel.LoadingState.Succeeded -> {
                    commits_recycler_view?.visibility = View.VISIBLE
                    load_indicator?.visibility = View.GONE
                    processRepos(state.repos)
                }
            }
        })
    }

    private fun processRepos(repos: List<RepoEntry>?) {
        val apiQuery = queryPrefs.storedQuery ?: ""

        if (repos.isNullOrEmpty()) {
            Toast.makeText(activity, "No repositories found for $apiQuery",
                    Toast.LENGTH_SHORT).show()
            load_indicator?.visibility = View.GONE
            return
        } else {
            repoAdapter.apply {
                addAll(repos)
                sort()
            }
        }

        if (repos.size < MAXIMUM_LIST_LENGTH) {
            isContentLastPage = true
        }
    }

    companion object {
        private const val MAXIMUM_LIST_LENGTH = 30
        private const val START_PAGE = 1

        @JvmStatic
        fun newInstance(): RepoListFragment {
            return RepoListFragment()
        }
    }
}
