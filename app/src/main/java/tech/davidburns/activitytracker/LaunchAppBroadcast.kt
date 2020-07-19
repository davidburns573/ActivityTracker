package tech.davidburns.activitytracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LaunchAppBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.extras?.getInt(ACTIVITY_ID)?.let {
            TimerManager.mapOfTimers[it]?.apply {
                inLimbo = true
                pause()
            }

            Intent(
                context,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).also { launchActivity ->
                context.startActivity(launchActivity)
            }
        }
    }
}