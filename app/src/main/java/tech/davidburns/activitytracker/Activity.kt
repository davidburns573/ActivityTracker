package tech.davidburns.activitytracker

import android.content.ContentValues
import android.database.Cursor
import android.database.CursorWrapper
import android.view.View
import tech.davidburns.activitytracker.util.UserSchema
import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    val sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)
    var view: View? = null

    fun addSession(length: Duration) {
        val session = Session(length, name)
        sessions.add(session)
        val values = Session.getContentValues(session)
        User.database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        val session = Session(start, end, name)
        sessions.add(session)
        val values = Session.getContentValues(session)
        User.database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    fun querySessions():
            SessionCursorWrapper {
        val query: String = "SELECT * FROM " + UserSchema.SessionTable.NAME +
                " WHERE " + UserSchema.SessionTable.Cols.NAME + "=" + "?"

        val strArray: Array<String> = arrayOf(name)
        val cursor: Cursor = User.database.rawQuery(query, strArray)

//        val cursor: Cursor = User.database.query(
//            UserSchema.SessionTable.NAME,
//            null,
//            whereClause,
//            whereArgs,
//            null,
//            null,
//            null
//        )

        return SessionCursorWrapper(cursor)
    }

    fun setSessionsFromDB() {
        sessions.clear()

        val cursor: SessionCursorWrapper = querySessions()

        try {
            cursor.moveToFirst()
            while (!(cursor.isAfterLast)) {
                sessions.add(cursor.getSession())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
    }

    companion object {
        fun getContentValues(activity: Activity): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.ActivityTable.Cols.ACTIVITYNAME, activity.name)
            return values
        }
    }
}

class ActivityCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getActivity(): Activity {
        val name: String =
            getString(getColumnIndex(UserSchema.ActivityTable.Cols.ACTIVITYNAME));
        return Activity(name)
    }
}