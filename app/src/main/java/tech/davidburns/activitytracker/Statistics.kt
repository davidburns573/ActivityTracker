package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.ceil

/**
 * @constructor Creates an object to calculate a variety of stats about a list of [Session]s
 * @author David Burns
 * @author Charles Jenkins
 * @since 1.0
 */
class Statistics(private val sessions: List<Session>) {
    /**
     * @return [Duration] of all [Session]s of an [Activity]
     */
    fun totalTimeEver(): Duration {
        var total: Duration = Duration.ZERO
        sessions.forEach {
            total = total.plus(it.length)
        }
        return total
    }

    /**
     * @return Average [Duration] of all [Session]s of an [Activity] per day
     */
    fun averageTimePerDayEver(): Duration {
        val startDate: LocalDate = sessions[0].start.toLocalDate()
        val endDate: LocalDate = LocalDate.now()
        val numDays = ceil(Duration.between(startDate, endDate).toHours() / 24.0)
        val totalTime: Duration = totalTimeEver()
        return totalTime.dividedBy(numDays.toLong())
    }

    private fun getHashMapOfDaysAndTime(): HashMap<LocalDate, Duration> {
        val daysTotalTime: HashMap<LocalDate, Duration> = HashMap()
        sessions.forEach {
            if (daysTotalTime.containsKey(it.start.toLocalDate())) {
                daysTotalTime[it.start.toLocalDate()] = daysTotalTime[it.start.toLocalDate()]!!.plus(it.length)
            } else {
                daysTotalTime[it.start.toLocalDate()] = it.length
            }
        }
        return daysTotalTime
    }

    /**
     * Index 0 contains total time spent on Sunday... Index 6 contains total time spent on Saturday
     * @return [Array] of [Duration]s.
     */
    fun timeLastWeek(): Array<Duration> {
        val now: LocalDateTime = LocalDateTime.now()
        val lastSevenDays: Array<Duration> = Array(7) { Duration.ZERO }
        sessions.forEach {
            val duration = Duration.between(it.start, now).toDays().toInt()
            if (duration <= 6) {
                lastSevenDays[6 - duration] = lastSevenDays[6 - duration].plus(it.length)
            }
        }
        return lastSevenDays
    }
}