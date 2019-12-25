package com.plweegie.android.squash.services

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import com.plweegie.android.squash.App
import com.plweegie.android.squash.R
import com.plweegie.android.squash.data.Commit
import com.plweegie.android.squash.data.RepoRepository
import com.plweegie.android.squash.rest.GitHubService
import com.plweegie.android.squash.ui.LastCommitDetailsActivity
import com.plweegie.android.squash.utils.DateUtils
import com.plweegie.android.squash.utils.QueryPreferences
import java.text.ParseException
import java.util.*
import javax.inject.Inject

class CommitPollWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @Inject
    lateinit var service: GitHubService

    @Inject
    lateinit var queryPrefs: QueryPreferences

    @Inject
    lateinit var dataRepository: RepoRepository

    private val commits: MutableList<Commit>

    companion object {
        private const val CHANNEL_ID = "com.plweegie.android.squash"
        const val ACTION_SHOW_NOTIFICATION = "com.plweegie.android.squash.SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = "com.plweegie.android.squash.PRIVATE"
        const val REQUEST_CODE = "request_code"
        const val NOTIFICATION = "notification"
    }

    init {
        (applicationContext as App).netComponent.inject(this)
        commits = ArrayList()
        createNotificationChannel()
    }

    override suspend fun doWork(): Result {
        val repos = dataRepository.getFavoritesAsync()
        val authToken = queryPrefs.storedAccessToken

        return try {
            repos.forEach { repo ->
                val filteredMerges = service.getCommits(repo.owner.login, repo.name, 1, authToken)
                        .filter { !it.commitBody.message.startsWith("Merge pull") }
                commits.add(filteredMerges[0])
            }
            processCommits(commits)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun processCommits(commits: List<Commit>) {
        val lastDate = queryPrefs.lastResultDate
        var newLastDate = 0L

        Collections.sort(commits, QueryPreferences.CommitCreatedComparator())

        try {
            newLastDate = if (commits.isEmpty()) lastDate else
                DateUtils.convertToTimestamp(commits[0].commitBody.committer.date)
        } catch (e: ParseException) {
            Crashlytics.log("Date parser error")
            Crashlytics.logException(e)
        }

        if (newLastDate > lastDate) {
            val updatedCommit = commits[0]
            val splitCommit = updatedCommit.htmlUrl.split("/")
            val commitRepo = splitCommit[4]
            val commitOwner = splitCommit[3]

            val commitDetailsIntent = LastCommitDetailsActivity.newIntent(context,
                    arrayOf(commitOwner, commitRepo))
            val commitPendingIntent = PendingIntent.getActivity(context,
                    0, commitDetailsIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setTicker(context.getString(R.string.new_commit_headline))
                    .setContentTitle(context.getString(R.string.new_commit_headline))
                    .setContentText(commitRepo)
                    .setContentIntent(commitPendingIntent)
                    .setSmallIcon(R.drawable.ic_info_24dp)
                    .setAutoCancel(true)
                    .build()

            showBackgroundNotif(0, notification)
            queryPrefs.lastResultDate = newLastDate
        }
    }

    private fun showBackgroundNotif(requestCode: Int, notif: Notification) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notif)
        }

        context.sendOrderedBroadcast(intent, PERMISSION_PRIVATE, null, null,
                Activity.RESULT_OK, null, null)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Squash"
            val descriptionText = "Recent commit notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
