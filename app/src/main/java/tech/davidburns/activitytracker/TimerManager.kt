package tech.davidburns.activitytracker

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val SUMMARY_ID = 1
private const val GROUP_NAME = "timerGroup"
private const val CHANNEL_NAME = "timerChannel"
private var foreground = false

object TimerManager {
    val mapOfTimers = mutableMapOf<Int, Timer>()
    private val connections: MutableList<ServiceConnection> = mutableListOf()
    private lateinit var timerService: TimerService
    private var foregroundId: Int = -1

    /**
     * Start a service to run a timer.
     * Uses callback to return created timer.
     */
    fun initializeTimer(activityId: Int, title: String, callback: (Timer) -> Unit) {
        if (::timerService.isInitialized) {
            mapOfTimers[activityId] = timerService.startTimer(title, activityId).apply(callback)
        } else {
            val serviceIntent = Intent(User.mainActivity, TimerService::class.java)

            /** Defines callbacks for service binding, passed to bindService()  */
            val connection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    // We've bound to LocalService, cast the IBinder and get LocalService instance
                    val binder = service as TimerService.LocalBinder
                    timerService = binder.getService()
                    mapOfTimers[activityId] =
                        timerService.startTimer(title, activityId).apply(callback)
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    //Don't care?
                }
            }

            User.mainActivity.startService(serviceIntent)
            // Bind to LocalService
            Intent(User.mainActivity, TimerService::class.java).also { bindingIntent ->
                User.mainActivity.bindService(bindingIntent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun unbindServices() {
        connections.forEach {
            User.mainActivity.unbindService(it)
        }
    }

    fun rebindServices() {
        connections.forEach {
            Intent(User.mainActivity, TimerService::class.java).also { intent ->
                User.mainActivity.bindService(intent, it, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun createNotification(
        title: String,
        content: String,
        timerService: TimerService,
        id: Int,
        activityId: Int
    ) {
        val context = User.applicationContext
        //Opens this app on notification click
        val pendingIntent =
            Intent(
                context,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS).let { notificationIntent ->
                PendingIntent.getActivity(context, 0, notificationIntent, 0)
            }

        val stopIntent =
            Intent(context, LaunchAppBroadcast::class.java).putExtra(ACTIVITY_ID, activityId)

        val notification = NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
            .setGroup(GROUP_NAME)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentText(content)
            .addAction(
                R.drawable.ic_baseline_stop_24,
                User.applicationContext.getString(R.string.stop),
                PendingIntent.getBroadcast(
                    context,
                    1,
                    stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        if (foreground) {
            with(NotificationManagerCompat.from(User.mainActivity)) {
                notify(id, notification.build())
            }
        } else {
            val summaryNotification = NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
                .setGroup(GROUP_NAME)
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentIntent(pendingIntent)
                .setGroupSummary(true)
                .build()
            with(NotificationManagerCompat.from(User.mainActivity)) {
                notify(SUMMARY_ID, summaryNotification)
            }
            timerService.startForeground(id, notification.build())
            foregroundId = id
            foreground = true
        }
    }

    fun dismissNotification(id: Int) {
        if (id == foregroundId) {
            timerService.stopForeground(true)
            foreground = false
        }
        with(NotificationManagerCompat.from(User.mainActivity)) {
            cancel(id)
            cancel(SUMMARY_ID)
        }
    }
}