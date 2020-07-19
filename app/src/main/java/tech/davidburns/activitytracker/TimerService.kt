package tech.davidburns.activitytracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_card.view.*

private const val CHANNEL_NAME = "timerChannel"
private var id = 2

class TimerService : Service(), GlobalTimerListener {
    // Binder given to clients
    private val binder = LocalBinder()
    private var seconds = 0

    override fun onCreate() {
        //Create Notification Channel
        val channel = NotificationChannel(
            CHANNEL_NAME,
            getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lightColor = Color.BLUE
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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
        // If we get killed, after returning from here, restart
        return START_STICKY
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

    fun startTimer(title: String, activity: Activity): Timer {
        return Timer(this,title, activity)
    }
}