package tech.davidburns.activitytracker.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Database
import java.time.ZoneId

class NativeDatabase : Database() {
    private val database: SQLiteDatabase by lazy { UserBaseHelper(User.applicationContext).writableDatabase }

    init {
        val cursor: Cursor = database.query(
            UserSchema.ActivityTable.NAME,
            null, null,
            null, null,
            null, null
        )
        ActivityCursorWrapper(cursor).use {
            it.moveToFirst()
            while (!(it.isAfterLast)) {
                User.addActivity(it.getActivity(), false)
                it.moveToNext()
            }
        }
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

    /*
    https://stackoverflow.com/questions/2758415/swap-values-for-two-rows-in-the-same-table-in-sql-server
    WITH map AS (
        SELECT *
        FROM (VALUES
            (1, 2),  -- Here's an example of swapping two rows:
            (2, 1),  -- 1 <- 2,  2 <- 1

            (3, 4),  -- Here's an example of rotating three rows:
            (4, 5),  -- 3 <- 4,  4 <- 5,  5 <- 3
            (5, 3),

            (6, 7)   -- Here's an example of just copying one row to another: 3 <- 5
        ) AS a (destID, srcID)
    )
    UPDATE destination
    SET
        ColumnA = source.ColumnA,
        ColumnB = source.ColumnB,
        ColumnC = source.ColumnC
    FROM
        SomeTable AS destination
        JOIN map ON map.destID = destination.ID
        JOIN SomeTable AS source ON source.ID = map.srcID
     */
    override fun orderUpdated() {
        TODO("Not yet implemented")
    }

    companion object {
        fun getActivityContentValues(activity: Activity): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.ActivityTable.Cols.ACTIVITY_NAME, activity.name)
            return values
        }

        fun getSessionContentValues(session: Session): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.SessionTable.Cols.NAME, session.name)
            values.put(UserSchema.SessionTable.Cols.LENGTH, session.length.toMillis())
            val zoneId = ZoneId.systemDefault()
            values.put(
                UserSchema.SessionTable.Cols.START, session.start.atZone(zoneId)?.toEpochSecond()
            )
            return values
        }
    }
}

class ActivityCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getActivity(): Activity {
        val name: String =
            getString(getColumnIndex(UserSchema.ActivityTable.Cols.ACTIVITY_NAME))
        return Activity(name)
    }
}

class SessionCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getSession(): Session {
        val name: String = getString(getColumnIndex(UserSchema.SessionTable.Cols.NAME))
        val length: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.LENGTH))
        val start: Long = getLong(getColumnIndex(UserSchema.SessionTable.Cols.START))
        return Session(name, length, start)
    }
}


class UserSchema {
    object ActivityTable {
        const val NAME: String = "activities"

        object Cols {
            const val ACTIVITY_NAME = "activityname"
        }
    }

    object SessionTable {
        const val NAME: String = "sessions"

        object Cols {
            const val NAME = "name"
            const val LENGTH = "length"
            const val START = "start"
        }
    }

}

private const val VERSION: Int = 1
private const val DATABASE_NAME = "UserBase.db"

class UserBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table " + UserSchema.ActivityTable.NAME
                    + "(" + " _id integer primary key autoincrement, "
                    + UserSchema.ActivityTable.Cols.ACTIVITY_NAME + ")"
        )
        db?.execSQL(
            "create table " + UserSchema.SessionTable.NAME
                    + "(" + " _id integer primary key autoincrement, "
                    + UserSchema.SessionTable.Cols.NAME + ", "
                    + UserSchema.SessionTable.Cols.LENGTH + ", "
                    + UserSchema.SessionTable.Cols.START + ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}