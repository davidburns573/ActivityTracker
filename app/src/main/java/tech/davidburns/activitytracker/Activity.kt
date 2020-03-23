package tech.davidburns.activitytracker

import android.content.ContentValues
import android.database.Cursor
import android.database.CursorWrapper
import android.view.View
import kotlinx.android.synthetic.main.activity.view.*
import tech.davidburns.activitytracker.util.UserSchema
import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)
    var view: View? = null

    fun addSession(length: Duration) {
        val session: Session = Session(length, name)
        sessions.add(session)
        val values: ContentValues = Session.getContentValues(session)
        User.database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        val session: Session = Session(start, end, name)
        sessions.add(session)
        val values: ContentValues = Session.getContentValues(session)
        User.database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    fun initView(view: View) {
        this.view = view
        view.setOnClickListener {

        }
        view.btnStart.setOnClickListener {

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

class  ActivityCursorWrapper(cursor: Cursor): CursorWrapper(cursor) {
    fun getActivity(): Activity {
        val name: String = getString(getColumnIndex(UserSchema.ActivityTable.Cols.ACTIVITYNAME));
        return Activity(name)
    }
}