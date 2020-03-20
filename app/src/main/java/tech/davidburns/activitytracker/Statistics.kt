package tech.davidburns.activitytracker

import java.time.Duration

class Statistics(val sessions: MutableList<Session>) {

    /**
     * @return [Duration] of all sessions of an [Activity]
     */
    fun totalTimeEver() : Duration {
        var total : Duration = Duration.ZERO
        sessions.forEach {
            total = total.plus(it.length)
        }
        return total
    }

//    fun averageTimePerDay() : Duration {
//
//    }
}