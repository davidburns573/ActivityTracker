package tech.davidburns.activitytracker

import java.util.*
import kotlin.concurrent.timer

class Timer(private val onTimeUpdated: () -> Unit, private val onTimerStopped: () -> Unit) {
    private val timer = timer("timer", period = 1000, action = { updateTime() })
    private var  paused = false
    var seconds = 0
    val formattedTime: String
        get() {
            val hours: Int = seconds / 3600
            val minutes: Int = seconds % 3600 / 60
            val secs: Int = seconds % 60

            return String
                .format(
                    Locale.getDefault(),
                    "%d:%02d:%02d", hours,
                    minutes, secs
                )
        }

    private fun updateTime() {
        if (!paused) {
            seconds++
            onTimeUpdated()
        }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun stop() {
        timer.cancel()
        onTimerStopped()
    }
}