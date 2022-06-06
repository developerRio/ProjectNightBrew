package com.originalstocks.brew.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.originalstocks.brew.ui.LockScreenActivity

private const val TAG = "PowerConnectionReceiver"
class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB

        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        if (batteryStatus != null) {
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat()
        }
        if (isCharging) {
            // show
            val screenIntent = Intent(context.applicationContext, LockScreenActivity::class.java)
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(screenIntent)
        } else {
            // hide
        }
        Log.i(TAG, "onReceive: isCharging : $isCharging usbCharge: $usbCharge acCharge = $acCharge")
    }
}