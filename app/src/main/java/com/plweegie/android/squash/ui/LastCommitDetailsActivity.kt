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
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.plweegie.android.squash.App
import com.plweegie.android.squash.R
import com.plweegie.android.squash.data.Commit
import com.plweegie.android.squash.rest.GitHubService
import com.plweegie.android.squash.utils.DateUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.commit_view.*
import java.text.ParseException
import javax.inject.Inject

class LastCommitDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var service: GitHubService

    private var repoProps: Array<String> = arrayOf()
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).netComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.commit_view)

        repoProps = intent.getStringArrayExtra(EXTRA_REPO_PROPS) ?: arrayOf()

        if (savedInstanceState != null && savedInstanceState.containsKey(TEXT_VIEW_CONTENTS)) {
            commit_message_tv?.text = savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)!![0]
            commit_info_tv?.text = savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)!![1]
            commit_date_tv?.text = savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)!![2]
            return
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable?.isDisposed == false) {
            disposable?.dispose()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequenceArray(TEXT_VIEW_CONTENTS, arrayOf(
                commit_message_tv?.text,
                commit_info_tv?.text,
                commit_date_tv?.text))
    }

    private fun updateUI() {
        val call = service.getCommitsObservable(repoProps[0], repoProps[1], 10)

        disposable = call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Commit>>() {
                    override fun onNext(commits: List<Commit>) {
                        val commit = commits
                                .first { !it.commitBody.message.startsWith("Merge pull") }
                        commit_message_tv?.text = commit.commitBody.message
                                .split("\n").toTypedArray()[0]
                        commit_info_tv?.text = buildCommitInfo(commit)
                        commit_date_tv?.text = buildCommitDate(commit)
                    }

                    override fun onError(e: Throwable) {
                        Crashlytics.log(1, "LastCommitDetails", "Retrofit error")
                        Crashlytics.logException(e)
                    }

                    override fun onComplete() {}
                })
    }

    private fun buildCommitInfo(commit: Commit): CharSequence {
        val authorId = commit.commitBody.commitBodyAuthor.name
        val info = this.resources.getString(R.string.commit_info, authorId)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(info)
        }
    }

    private fun buildCommitDate(commit: Commit): CharSequence {
        val date = commit.commitBody.committer.date
        var formattedDate: String? = ""

        try {
            formattedDate = DateUtils.changeDateFormats(date)
        } catch (e: ParseException) {
            Crashlytics.log("Date parser error")
            Crashlytics.logException(e)
        }
        return resources.getString(R.string.commit_date, formattedDate)
    }

    companion object {
        private const val TEXT_VIEW_CONTENTS = "textViewContents"
        private const val EXTRA_REPO_PROPS = "repoPropsExtra"

        fun newIntent(packageContext: Context, repoProps: Array<String>?): Intent =
                Intent(packageContext, LastCommitDetailsActivity::class.java).apply {
                    putExtra(EXTRA_REPO_PROPS, repoProps)
                }
    }
}