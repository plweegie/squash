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
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.plweegie.android.squash.App
import com.plweegie.android.squash.R
import com.plweegie.android.squash.adapters.BaseGithubAdapter
import com.plweegie.android.squash.adapters.FaveAdapter
import com.plweegie.android.squash.data.RepoEntry
import com.plweegie.android.squash.viewmodels.FaveListViewModel
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory
import kotlinx.android.synthetic.main.list_fragment.*
import javax.inject.Inject

class FaveListFragment : Fragment(), FaveAdapter.FaveAdapterOnClickHandler,
        BaseGithubAdapter.GithubAdapterOnClickListener {

    @Inject
    lateinit var viewModelFactory: FaveListViewModelFactory

    private lateinit var viewModel: FaveListViewModel
    private lateinit var faveAdapter: FaveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FaveListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        (activity?.application as App).netComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.list_fragment, parent, false)

        faveAdapter = FaveAdapter(activity).apply {
            setOnFaveDeleteClickedHandler(this@FaveListFragment)
            setListener(this@FaveListFragment)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        load_indicator?.visibility = View.GONE

        commits_recycler_view?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            adapter = faveAdapter
        }

        viewModel.faveList.observe(viewLifecycleOwner, Observer {
            it?.let {
                repoEntries -> faveAdapter.setContent(repoEntries)
            }
        })
    }

    override fun onFaveDeleteClicked(repoId: Long) {
        viewModel.deleteRepo(repoId)
    }

    override fun onLastCommitClicked(repo: RepoEntry) {
        val intent = LastCommitDetailsActivity.newIntent(activity,
                arrayOf(repo.owner.login, repo.name))
        startActivity(intent)
    }

    override fun onItemClick(repo: RepoEntry) {
        val intent = RepoReadmeActivity.newIntent(activity as Context, repo.owner.login, repo.name)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fave_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_db -> {
                viewModel.deleteAllRepos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): FaveListFragment = FaveListFragment()
    }
}
