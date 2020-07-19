package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String, val id: Int) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)

    fun addSession(length: Duration) {
        val session = Session(length, name)
        sessions.add(session)
        User.addSession(session, name)
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        val session = Session(start, end, name)
        sessions.add(session)
        User.addSession(session, name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Activity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}