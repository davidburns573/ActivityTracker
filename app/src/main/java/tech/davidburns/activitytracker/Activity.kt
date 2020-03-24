package tech.davidburns.activitytracker

import android.content.ContentValues
import android.database.Cursor
import android.database.CursorWrapper
import android.view.View
import tech.davidburns.activitytracker.util.ActivitySchema
import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)
    var view: View? = null

    fun addSession(length: Duration) {
        sessions.add(Session(length))
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        sessions.add(Session(start, end))
    }

    companion object {
        fun getContentValues(activity: Activity): ContentValues {
            val values = ContentValues()
            values.put(ActivitySchema.ActivityTable.Cols.ACTIVITYNAME, activity.name)
            return values
        }
    }
}

class  ActivityCursorWrapper(cursor: Cursor): CursorWrapper(cursor) {
    fun getActivity(): Activity {
        val name: String = getString(getColumnIndex(ActivitySchema.ActivityTable.Cols.ACTIVITYNAME));
        return Activity(name)
    }
}