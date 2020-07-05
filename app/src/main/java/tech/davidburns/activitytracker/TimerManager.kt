package tech.davidburns.activitytracker

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_card.view.*

private const val SUMMARY_ID = 1
private const val GROUP_NAME = "timerGroup"
private const val CHANNEL_NAME = "timerChannel"
private var foreground = false

object TimerManager {
    val mapOfTimers = mutableMapOf<Activity, Timer>()
    private val connections: MutableList<ServiceConnection> = mutableListOf()
    private lateinit var timerService: TimerService
    private var foregroundId: Int = -1

    /**
     * Start a service to run a timer
     * @param viewHolder to display time
     */
    fun initializeTimer(activity: Activity, title: String, callback: (Timer) -> Unit) {
        if (::timerService.isInitialized) {
            mapOfTimers[activity] = timerService.startTimer(title).apply(callback)
        } else {
            val serviceIntent = Intent(User.mainActivity, TimerService::class.java)

            /** Defines callbacks for service binding, passed to bindService()  */
            val connection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    // We've bound to LocalService, cast the IBinder and get LocalService instance
                    val binder = service as TimerService.LocalBinder
                    timerService = binder.getService()
                    mapOfTimers[activity] = timerService.startTimer(title).apply(callback)
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

    fun createNotification(title: String, content: String, timerService: TimerService, id: Int) {
        //Opens this app on notification click
        val pendingIntent: PendingIntent =
            Intent(timerService, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(timerService, 0, notificationIntent, 0)
            }

        val notification = NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_delete_black_24dp)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setGroup(GROUP_NAME)
            .setOnlyAlertOnce(true)
            .setContentText(content)

        if (foreground) {
            with(NotificationManagerCompat.from(User.mainActivity)) {
                notify(id, notification.build())
            }
        } else {
            val summaryNotification = NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
                .setGroup(GROUP_NAME)
                .setSmallIcon(R.drawable.ic_delete_black_24dp)
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