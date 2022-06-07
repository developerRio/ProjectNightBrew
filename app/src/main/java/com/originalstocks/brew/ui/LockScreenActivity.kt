package com.originalstocks.brew.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.liveData
import com.originalstocks.brew.R
import com.originalstocks.brew.databinding.ActivityLockScreenBinding
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "LockScreenActivity"

class LockScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        /** setting views */

        // animation
        binding.lottieAnimationView.setAnimation(R.raw.rim_three)

        val currentTime = liveData {
            while (true) {
                emit(Calendar.getInstance().time)
                kotlinx.coroutines.delay(1000)
            }
        }

        currentTime.observe(this) {time ->
            val formatDate: String = SimpleDateFormat("EEEE dd MMM", Locale.ENGLISH).format(time)
            val formatTime: String = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(time)
            binding.timeTextView.text = formatTime
            binding.dateTextView.text = formatDate
            if (getBatteryLevels(this).toString().contains("100.0")) {
                // fully charged
                binding.chargingStatusTextView.text = "Charged"
            } else {
                // charging
                binding.chargingStatusTextView.text = "Charging ${getBatteryLevels(this)}"
            }
        }
    }

    private fun getBatteryLevels(context: Context): Float {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 0

        return level * 100 / scale.toFloat()
    }

    fun finishThisActivity() {
        finish()
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

}