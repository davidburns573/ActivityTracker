package tech.davidburns.activitytracker

import android.os.Handler
import android.widget.TextView
import java.util.*

class Timer(private var textView: TextView) {
    var seconds: Int = 0
    var isRunning: Boolean = false
    private val handler: Handler = Handler()
    private lateinit var runnable: Runnable
    fun runTimer() {
        isRunning = true
        runnable = Runnable {
            val hours: Int = seconds / 3600
            val minutes: Int = seconds % 3600 / 60
            val secs: Int = seconds % 60

            val time: String = java.lang.String
                .format(
                    Locale.getDefault(),
                    "%d:%02d:%02d", hours,
                    minutes, secs)

            textView.text = time

            seconds++
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }

    fun endTimer() {
        handler.removeCallbacks(runnable)
        reset()
    }

    fun pauseTimer() {
        handler.removeCallbacks(runnable)
        isRunning = false
    }

    fun reset() {
        seconds = 0;
        textView.text = ""
        isRunning = false
    }
}