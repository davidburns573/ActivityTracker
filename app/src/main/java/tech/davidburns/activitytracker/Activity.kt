package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)

    fun addSession(length: Duration) {
        val session = Session(length, name)
        sessions.add(session)
        User.database.addSession(session, name)
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        val session = Session(start, end, name)
        sessions.add(session)
        User.database.addSession(session, name)
    }
}