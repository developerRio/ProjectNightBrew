package com.originalstocks.brew.ui

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.originalstocks.brew.data.job.ChargingJobScheduler
import com.originalstocks.brew.databinding.ActivityMainBinding
import com.originalstocks.brew.utils.isJobSchedulerRunning

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /* By Job Scheduler */
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo.Builder(
            12,
            ComponentName(this@MainActivity, ChargingJobScheduler::class.java)
        ).build()

        binding.startServiceButton.setOnClickListener {
            jobScheduler.schedule(jobInfo)
        }

        binding.stopServiceButton.setOnClickListener {
            if (isJobSchedulerRunning(this)) {
                jobScheduler.cancel(jobInfo.id)
            } else {
                Log.e(TAG, "onCreate: no jobs active right now")
            }
        }
    }


}