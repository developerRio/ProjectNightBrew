package com.originalstocks.brew.utils

import android.app.job.JobScheduler
import android.content.Context
import android.widget.Toast


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun isJobSchedulerRunning(context: Context): Boolean {
    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    return jobScheduler.allPendingJobs.size > 0
}