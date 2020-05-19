package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
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

        if (name != other.name) return false
        if (sessions != other.sessions) return false
        if (statistics != other.statistics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + sessions.hashCode()
        result = 31 * result + statistics.hashCode()
        return result
    }
}