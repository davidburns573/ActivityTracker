package tech.davidburns.activitytracker

import java.time.*

class Session(
    val name: String,
    val length: Duration,
    val start: LocalDateTime
) {
    constructor(length: Duration, name: String) : this(name, length, LocalDateTime.now())

    constructor(start: LocalDateTime, end: LocalDateTime, name: String) : this(
        name,
        Duration.between(start, end),
        start
    )

    /**
     * Chained constructor converts primitive [Long]s to date types
     * @param name is the name of the [Activity] this [Session] belongs to
     * @param length is the milliseconds of the session
     * @param start is the [Long] of the [LocalDateTime] of the beginning of the session
     */
    constructor(name: String, length: Long, start: Long) : this(
        name,
        Duration.ofMillis(length),
        LocalDateTime.ofEpochSecond(start, 0,
            ZoneId.systemDefault().rules.getOffset(Instant.now()))
    )
}