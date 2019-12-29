package com.plweegie.android.squash.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.plweegie.android.squash.App
import com.plweegie.android.squash.R
import com.plweegie.android.squash.rest.GitHubService
import kotlinx.android.synthetic.main.activity_repo_readme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RepoReadmeActivity : AppCompatActivity() {

    companion object {

        private const val README_OWNER_EXTRA = "readme_owner"
        private const val README_NAME_EXTRA = "readme_name"

        @JvmStatic
        fun newIntent(context: Context, owner: String, name: String): Intent =
                Intent(context, RepoReadmeActivity::class.java).apply {
                    putExtra(README_OWNER_EXTRA, owner)
                    putExtra(README_NAME_EXTRA, name)
                }
    }

    @Inject
    lateinit var apiService: GitHubService

    private var readmeOwner: String? = null
    private var readmeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).netComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_readme)

        readmeOwner = intent.getStringExtra(README_OWNER_EXTRA)
        readmeName = intent.getStringExtra(README_NAME_EXTRA)
        updateUI()
    }

    private fun updateUI() {
        lifecycleScope.launchWhenResumed {
            readmeOwner?.let {
                val readme = apiService.getReadme(it, readmeName!!)
                val data = Base64.decode(readme.content, Base64.DEFAULT)
                withContext(Dispatchers.Main) {
                    readme_content_tv?.text = String(data, Charsets.UTF_8)
                }
            }
        }
    }
}