package tech.davidburns.activitytracker

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class Session(
    val name: String,
    val length: Duration,
    val day: LocalDate,
    val start: LocalDateTime?
) {
    constructor(length: Duration, name: String) : this(name, length, LocalDate.now(), null)

    constructor(start: LocalDateTime, end: LocalDateTime, name: String) : this(
        name,
        Duration.between(start, end),
        start.toLocalDate(),
        start
    )

    /**
     * Chained constructor converts primitive [Long]s to date types
     * @param name is the name of the [Activity] this [Session] belongs to
     * @param length is the milliseconds of the session
     * @param day is the [Long] of the [LocalDate] epochDay
     * @param start is the [Long] of the [LocalDateTime] of the beginning of the session
     */
    constructor(name: String, length: Long, day: Long, start: Long?) : this(
        name,
        Duration.ofMillis(length),
        LocalDate.ofEpochDay(day),
        start?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.ofTotalSeconds(0)) }
    )
}