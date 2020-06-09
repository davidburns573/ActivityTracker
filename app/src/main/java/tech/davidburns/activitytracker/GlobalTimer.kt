package tech.davidburns.activitytracker

import java.util.Timer
import kotlin.concurrent.timer

object GlobalTimer {
    var foreground: Boolean = false

    private val listeners = mutableListOf<GlobalTimerListener>()

    private var timer: Timer? = null

    private fun incrementTime() {
        listeners.forEach {
            it.incrementTime()
        }
    }

    fun subscribe(listener: GlobalTimerListener) {
        listeners.add(listener)
        if (timer == null) {
            timer = timer("timer", period = 1000, action = {
                incrementTime()
            })
        }
    }

    fun unsubscribe(listener: GlobalTimerListener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            timer?.cancel()
            timer = null
        }
    }
}

interface GlobalTimerListener {
    fun incrementTime()
}