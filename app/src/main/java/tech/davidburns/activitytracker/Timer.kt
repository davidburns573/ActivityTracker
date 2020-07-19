package tech.davidburns.activitytracker

import java.util.*
import kotlin.concurrent.timer

class Timer(
    private val timerService: TimerService,
    private val title: String
) {
    private val timer = timer("timer", period = 1000, action = { updateTime() })
    private var paused = false
    private val myId = id++
    private var listeners: MutableList<((String) -> Unit)> = mutableListOf()

    var bound = true
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
            TimerManager.createNotification(title, formattedTime, timerService, myId)
            listeners.forEach { it(formattedTime) }
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
        TimerManager.dismissNotification(myId)
//        viewHolder.clearTimer()
    }

    fun addListener(listener: (formattedTime: String) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (formattedTime: String) -> Unit) {
        listeners.remove(listener)
    }

    private companion object {
        private var id: Int = 2
    }
}