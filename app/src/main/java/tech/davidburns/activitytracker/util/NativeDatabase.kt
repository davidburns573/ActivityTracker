package tech.davidburns.activitytracker.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import tech.davidburns.activitytracker.*
import tech.davidburns.activitytracker.interfaces.Database
import java.time.ZoneId

class NativeDatabase(context: Context) : Database(context) {
    private lateinit var database: SQLiteDatabase

    override fun initializeDatabase(context: Context) {
        database = UserBaseHelper(context).writableDatabase
    }

    override fun setUserInfo() {
        TODO("Not yet implemented")
    }

    override fun getActivities(): MutableList<Activity> {
        val cursor: Cursor = database.query(
            UserSchema.ActivityTable.NAME,
            null, null,
            null, null,
            null, null
        )
        val activities: MutableList<Activity> = mutableListOf()
        ActivityCursorWrapper(cursor).use {
            it.moveToFirst()
            while (!(it.isAfterLast)) {
                User.activities.add(it.getActivity())
                it.moveToNext()
            }
        }
        return activities
    }

    override fun getSessionsFromActivity(activityName: String): MutableList<Session> {
        val query: String = "SELECT * FROM " + UserSchema.SessionTable.NAME +
                " WHERE " + UserSchema.SessionTable.Cols.NAME + "=" + "?"
        val strArray: Array<String> = arrayOf(activityName)
        val cursor: Cursor = database.rawQuery(query, strArray)

        val sessions: MutableList<Session> = mutableListOf()
        SessionCursorWrapper(cursor).use {
            it.moveToFirst()
            while (!(it.isAfterLast)) {
                sessions.add(it.getSession())
                it.moveToNext()
            }
        }
        return sessions
    }

    override fun addActivity(activity: Activity) {
        val values: ContentValues = getActivityContentValues(activity)
        database.insert(UserSchema.ActivityTable.NAME, null, values)
    }

    override fun addSession(session: Session, activityName: String) {
        val values = getSessionContentValues(session)
        database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    companion object {
        fun getActivityContentValues(activity: Activity): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.ActivityTable.Cols.ACTIVITYNAME, activity.name)
            return values
        }

        fun getSessionContentValues(session: Session): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.SessionTable.Cols.NAME, session.name)
            values.put(UserSchema.SessionTable.Cols.LENGTH, session.length.toMillis())
            val zoneId = ZoneId.systemDefault()
            values.put(UserSchema.SessionTable.Cols.DAY, session.day.toEpochDay())
            values.put(
                UserSchema.SessionTable.Cols.START, session.start?.atZone(zoneId)?.toEpochSecond()
            )
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

class SessionCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getSession(): Session {
        val name: String = getString(getColumnIndex(UserSchema.SessionTable.Cols.NAME))
        val length: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.LENGTH))
        val day: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.DAY))
        val start: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.START))
        return Session(name, length, day, start)
    }
}


class UserSchema {
    object ActivityTable {
        const val NAME: String = "activities"

        object Cols {
            const val ACTIVITYNAME = "activityname"
        }
    }

    object SessionTable {
        const val NAME: String = "sessions"

        object Cols {
            const val NAME = "name"
            const val LENGTH = "length"
            const val DAY = "day"
            const val START = "start"
        }
    }

}

private const val VERSION: Int = 1
private const val DATABASE_NAME = "UserBase.db"

class UserBaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table " + UserSchema.ActivityTable.NAME
                    + "(" + " _id integer primary key autoincrement, "
                    + UserSchema.ActivityTable.Cols.ACTIVITYNAME + ")"
        )
        db?.execSQL(
            "create table " + UserSchema.SessionTable.NAME
                    + "(" + " _id integer primary key autoincrement, "
                    + UserSchema.SessionTable.Cols.NAME + ", "
                    + UserSchema.SessionTable.Cols.LENGTH + ", "
                    + UserSchema.SessionTable.Cols.DAY + ", "
                    + UserSchema.SessionTable.Cols.START + ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}