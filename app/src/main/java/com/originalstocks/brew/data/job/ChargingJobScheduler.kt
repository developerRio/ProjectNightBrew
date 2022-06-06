package com.originalstocks.brew.data.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.work.Configuration
import com.originalstocks.brew.utils.PowerConnectionReceiver
import com.originalstocks.brew.utils.showToast

private const val TAG = "ChargingJobScheduler"
class ChargingJobScheduler: JobService() {

    init {
        val builder = Configuration.Builder()
        builder.setJobSchedulerJobIdRange(0, 1000)
    }

    private var chargingReceiver: PowerConnectionReceiver? = null

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.e(TAG, "onStartJob: called")
        chargingReceiver = PowerConnectionReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(chargingReceiver, intentFilter)
        showToast(this, "Dock Mode ON")
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.e(TAG, "onStopJob: called")
        showToast(this, "Dock Mode OFF")
        unregisterReceiver(chargingReceiver)
        return true
    }
}