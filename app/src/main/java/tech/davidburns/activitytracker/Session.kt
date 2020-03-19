package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class Session {
    val length: Duration
    val start: LocalDateTime?
    val end: LocalDateTime?
    val day: LocalDate

    constructor(length: Duration) {
        this.length = length
        day = LocalDate.now()
        start = null
        end = null
    }

    constructor(start: LocalDateTime, end: LocalDateTime) {
        length = Duration.between(start, end)
        this.start = start
        this.end = end
        day = start.toLocalDate()
    }
}