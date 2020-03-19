package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDate
import kotlin.math.ceil

class Statistics(val sessions: MutableList<Session>) {

    /**
     * @return [Duration] of all sessions of an [Activity]
     */
    fun totalTimeEver(): Duration {
        var total: Duration = Duration.ZERO
        sessions.forEach {
            total = total.plus(it.length)
        }
        return total
    }

    fun averageTimePerDayEver(): Duration {
        val startDate: LocalDate = sessions[0].day
        val endDate: LocalDate = LocalDate.now()
        val numDays = ceil(Duration.between(startDate, endDate).toHours() / 24.0)
        val totalTime: Duration = totalTimeEver()
        return totalTime.dividedBy(numDays.toLong())
    }

    private fun getHashMapOfDaysAndTime(): HashMap<LocalDate, Duration> {
        val daysTotalTime: HashMap<LocalDate, Duration> = HashMap()
        sessions.forEach {
            if (daysTotalTime.containsKey(it.day)) {
                daysTotalTime[it.day] = daysTotalTime[it.day]!!.plus(it.length)
            } else {
                daysTotalTime[it.day] = it.length
            }
        }
        return daysTotalTime
    }

    fun timeLastWeek(): Array<Duration> {
        val lastSevenDays: Array<Duration> = Array(7) { Duration.ZERO }
        val now: LocalDate = LocalDate.now()
        
        sessions.forEach {
            when(Duration.between(it.start, now).toDays().toInt()) {
                0 -> lastSevenDays[6] = lastSevenDays[6].plus(it.length)
                1 -> lastSevenDays[5] = lastSevenDays[5].plus(it.length)
                2 -> lastSevenDays[4] = lastSevenDays[4].plus(it.length)
                3 -> lastSevenDays[3] = lastSevenDays[3].plus(it.length)
                4 -> lastSevenDays[2] = lastSevenDays[2].plus(it.length)
                5 -> lastSevenDays[1] = lastSevenDays[1].plus(it.length)
                6 -> lastSevenDays[0] = lastSevenDays[0].plus(it.length)
            }
        }

        return lastSevenDays
    }
}