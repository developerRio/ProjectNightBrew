package com.originalstocks.brew.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.originalstocks.brew.R
import com.originalstocks.brew.ui.LockScreenActivity

private const val TAG = "PowerConnectionReceiver"

class PowerConnectionReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "101"
        const val CHANNEL_NAME = "Always-On Display"
        const val CHANNEL_DESCRIPTION =
            "This channel responsible for showing Always-On Display type notifications while charging."
    }

    override fun onReceive(context: Context, intent: Intent) {

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB

        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        if (batteryStatus != null) {
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat()
        }

        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardLocked) {
            //it is locked
            Log.e(TAG, "keyguardManager LOCKED")
            if (isCharging) {
                // showing the always on display
                showingHighPriorityNotification(context)
            } else {
                LockScreenActivity().finishThisActivity()
            }
        } else {
            //it is not locked
            Log.i(TAG, "keyguardManager UNLOCKED")
        }
        //Log.i(TAG, "onReceive: isCharging : $isCharging usbCharge: $usbCharge acCharge = $acCharge")
    }

    private fun showingFullScreenActivityIntent(context: Context): Intent {
        val screenIntent = Intent(context, LockScreenActivity::class.java)
        screenIntent.setClassName(
            context.packageName,
            "com.originalstocks.brew.ui.LockScreenActivity"
        )
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return screenIntent
    }

    private fun showingHighPriorityNotification(context: Context) {
        var notificationManager: NotificationManager? = null
        val mBuilder: NotificationCompat.Builder
        //Notification builder
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                151,
                showingFullScreenActivityIntent(context),
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                151,
                showingFullScreenActivityIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel =
                notificationManager.getNotificationChannel(CHANNEL_ID)
            if (mChannel == null) {
                mChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    importance
                )
                mChannel.description = CHANNEL_DESCRIPTION
                mChannel.enableVibration(false)
                mChannel.lightColor = Color.GREEN
                notificationManager.createNotificationChannel(mChannel)
            }
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            mBuilder.setSmallIcon(R.drawable.ic_baseline_battery_charging_full_24)
                .setContentTitle("Brew")
                .setContentText("Some Time")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(null)
                .setSilent(true)
                .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                .setVibrate(longArrayOf(100, 100, 100))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)

        } else {
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            mBuilder.setSmallIcon(R.drawable.ic_baseline_battery_charging_full_24)
                .setContentTitle("Brew")
                .setContentText("Some Time")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(null)
                .setSilent(true)
                .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                .setVibrate(longArrayOf(100, 100, 100))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)
        }
        notificationManager.notify(0, mBuilder.build())
    }


}