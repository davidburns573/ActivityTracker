package tech.davidburns.activitytracker

import java.time.*

class Session {
    val length: Duration
    val start: LocalDateTime?
    val day: LocalDate
    val name: String

    constructor(length: Duration, name: String) {
        this.length = length
        day = LocalDate.now()
        start = null
        this.name = name
    }

    constructor(start: LocalDateTime, end: LocalDateTime, name: String) {
        length = Duration.between(start, end)
        this.start = start
        day = start.toLocalDate()
        this.name = name
    }

    constructor(name: String, length: Duration, day: LocalDate, start: LocalDateTime) {
        this.name = name
        this.length = length
        this.day = day
        this.start = start
    }

    /**
     * Chained constructor converts primitive [Long]s to date types
     * @param name is the name of the [Activity] this [Session] belongs to
     * @param length is the milliseconds of the session
     * @param day is the [Long] of the [LocalDate] epochDay
     * @param start is the [Long] of the [LocalDateTime] of the beginning of the session
     */
    constructor(name: String, length: Long, day: Long, start: Long) : this(
        name,
        Duration.ofMillis(length),
        LocalDate.ofEpochDay(day),
        LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.ofTotalSeconds(0))
    )

}