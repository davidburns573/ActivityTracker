package tech.davidburns.activitytracker

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_card.view.*
import java.util.*
import kotlin.properties.Delegates

class Timer(private val title: String, private val viewHolder: ActivityAdapter.ViewHolder) : GlobalTimerListener {
    var seconds: Int by Delegates.observable(0) { _, _, _ ->
        updateTime()
    }

    var isRunning: Boolean = false
    private val handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var service: TimerService
    var bound: Boolean = false
    private lateinit var intent: Intent

    override fun incrementTime() {
        seconds++
    }

    private fun updateTime() {
        val hours: Int = seconds / 3600
        val minutes: Int = seconds % 3600 / 60
        val secs: Int = seconds % 60

        val time: String = java.lang.String
            .format(
                Locale.getDefault(),
                "%d:%02d:%02d", hours,
                minutes, secs
            )

        viewHolder.changeTimerText(time)
    }

    fun runTimer() {
        isRunning = true
        User.mainActivity.startTimer(this)
//        runnable = Runnable {
//            val hours: Int = seconds / 3600
//            val minutes: Int = seconds % 3600 / 60
//            val secs: Int = seconds % 60
//
//            val time: String = java.lang.String
//                .format(
//                    Locale.getDefault(),
//                    "%d:%02d:%02d", hours,
//                    minutes, secs
//                )
//
//            viewHolder.itemView.timer.text = time
//
//            seconds++
//            handler.postDelayed(runnable, 1000)
//        }
//        handler.post(runnable)
    }

    fun endTimer() {
        User.mainActivity.stopService(intent)
//        handler.removeCallbacks(runnable)
        GlobalTimer.unsubscribe(this)
        reset()
    }

    fun pauseTimer() {
        GlobalTimer.unsubscribe(this)
//        handler.removeCallbacks(runnable)
//        isRunning = false
    }

    fun reset() {
        seconds = 0;
        viewHolder.changeTimerText("")
        isRunning = false
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TimerService.LocalBinder
            this@Timer.service = binder.getService()
            bound = true
            this@Timer.service.createNotification(title)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }
}