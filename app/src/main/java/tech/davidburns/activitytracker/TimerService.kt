package tech.davidburns.activitytracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val SUMMARY_ID = 0
private const val GROUP_NAME = "timerGroup"
private const val CHANNEL_NAME = "timerChannel"
private const val FOREGROUND_ID = 2

class TimerService : Service(), GlobalTimerListener {
    // Binder given to clients
    private val binder = LocalBinder()
    private var seconds = 0
    private var id = 1

    fun createNotification(title: String) {
        //Opens this app on notification click
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification =
            NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_delete_black_24dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setGroup(GROUP_NAME)
                .build()

        if (GlobalTimer.foreground) {
            with(NotificationManagerCompat.from(User.mainActivity)) {
                notify(id, notification)
            }
            id += 100
        } else {
            val summary = NotificationCompat.Builder(User.mainActivity, CHANNEL_NAME)
                .setSmallIcon(R.drawable.ic_delete_black_24dp)
                .setGroupSummary(true)
                .setGroup(GROUP_NAME)
                .build()

            with(NotificationManagerCompat.from(User.mainActivity)) {
                notify(SUMMARY_ID, summary)
            }
            startForeground(FOREGROUND_ID, notification)
            GlobalTimer.foreground = true
        }
    }

    override fun onCreate() {
        //Create Notification Channel
        val channel = NotificationChannel(
            CHANNEL_NAME,
            getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
        )
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("$id", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        GlobalTimer.subscribe(this)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onDestroy() {
        GlobalTimer.unsubscribe(this)
        super.onDestroy()
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun incrementTime() {
        seconds++
    }
}