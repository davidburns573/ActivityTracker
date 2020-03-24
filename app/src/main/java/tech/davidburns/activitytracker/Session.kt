package tech.davidburns.activitytracker

import android.content.ContentValues
import android.database.Cursor
import android.database.CursorWrapper
import com.google.firebase.auth.UserProfileChangeRequest
import tech.davidburns.activitytracker.util.UserSchema
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
    //    fun createSessionData(): SessionData {
//        val zoneId = ZoneId.systemDefault()
//        val SessionData = SessionData(this.hashCode(), name, length.toMillis(), day.toEpochDay(),
//            start?.atZone(zoneId)?.toEpochSecond())
//        return SessionData;
//    }

    companion object {
        fun getContentValues(session: Session): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.SessionTable.Cols.NAME, session.name)
            values.put(UserSchema.SessionTable.Cols.LENGTH, session.length.toMillis())
            val zoneId = ZoneId.systemDefault()
            values.put(UserSchema.SessionTable.Cols.DAY, session.day.toEpochDay())
            values.put(UserSchema.SessionTable.Cols.START,
                session.start?.atZone(zoneId)?.toEpochSecond())
            return values
        }
    }
}

class  SessionCursorWrapper(cursor: Cursor): CursorWrapper(cursor) {
    fun getSession(): Session {
        val name: String = getString(getColumnIndex(UserSchema.SessionTable.Cols.NAME))
        val length: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.LENGTH))
        val day: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.DAY))
        val start: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.START))
        val zoneOffset = ZoneOffset.ofTotalSeconds(0)
        return Session(name, Duration.ofMillis(length), LocalDate.ofEpochDay(day),
            LocalDateTime.ofEpochSecond(start, 0, zoneOffset))
    }
}