package tech.davidburns.activitytracker

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import java.time.Duration
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
//
//    @Test
//    fun addingToList() {
//        val list: MutableList<Int> = mutableListOf()
//        list.add(3, 3)
//        list.add(1,1)
//        list.add(2,2)
//        Log.i("TESTINGTEST", list.toString())
//    }

//    @Test
//    fun aliasing() {
//        val activity = Activity("Study")
//        activity.addSession(Duration.ofMinutes(30))
//        assertEquals(activity.sessions, activity.statistics.sessions)
//    }


//    @Test
//    fun sessionDatabase() {
//        val activity = Activity("David")
//        activity.addSession(Duration.ofMillis(30))
//        activity.addSession(Duration.ofMillis(30))
//        activity.sessions = mutableListOf()
//        activity.setSessionsFromDB()
//        for (session in activity.sessions) {
//            println(session.length.toMillis())
//        }
//    }

//    fun timeLastWeek(sessions: MutableList<Session>): Array<Duration> {
//        val lastSevenDays: Array<Duration> = Array(7) { Duration.ZERO }
//        val now: LocalDate = LocalDate.now()
//
//        sessions.forEach {
//            when(Duration.between(it.start, now).toDays().toInt()) {
//                0 -> lastSevenDays[6] = lastSevenDays[6].plus(it.length)
//                1 -> lastSevenDays[5] = lastSevenDays[5].plus(it.length)
//                2 -> lastSevenDays[4] = lastSevenDays[4].plus(it.length)
//                3 -> lastSevenDays[3] = lastSevenDays[3].plus(it.length)
//                4 -> lastSevenDays[2] = lastSevenDays[2].plus(it.length)
//                5 -> lastSevenDays[1] = lastSevenDays[1].plus(it.length)
//                6 -> lastSevenDays[0] = lastSevenDays[0].plus(it.length)
//            }
//        }
//
//        return lastSevenDays
//    }
//
//    fun timeLastWeekShort(sessions: MutableList<Session>): Array<Duration> {
//        val lastSevenDays: Array<Duration> = Array(7) { Duration.ZERO }
//        val now: LocalDate = LocalDate.now()
//
//        sessions.forEach {
//            val duration = Duration.between(it.start, now).toDays().toInt()
//            lastSevenDays[6 - duration] += it.length
//        }
//
//        return lastSevenDays
//    }
//    @Test
//    fun sessionDatabase() {
//        val activity = Activity("David")
//        activity.addSession(Duration.ofMillis(30))
//        activity.addSession(Duration.ofMillis(30))
//        activity.sessions = mutableListOf()
//        activity.setSessionsFromDB()
//        for (session in activity.sessions) {
//            println(session.length.toMillis())
//        }
//    }
}
