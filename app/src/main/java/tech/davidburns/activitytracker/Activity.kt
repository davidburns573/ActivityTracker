package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)

    fun addSession(length: Duration) {
        sessions.add(Session(length))
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        sessions.add(Session(start, end))
    }
}