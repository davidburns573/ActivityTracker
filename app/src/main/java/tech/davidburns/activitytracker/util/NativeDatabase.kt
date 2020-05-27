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
import java.time.Instant
import java.time.ZoneId

class NativeDatabase : Database() {
    private val database: SQLiteDatabase by lazy { UserBaseHelper(User.applicationContext).writableDatabase }

    init {
        val cursor: Cursor = database.query(
            UserSchema.ActivityTable.NAME,
            null, null,
            null, null,
            null, UserSchema.ActivityTable.Cols.ORDER
        )
        ActivityCursorWrapper(cursor).use {
            it.moveToFirst()
            while (!(it.isAfterLast)) {
                addInternalActivity(it.getActivity())
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
        addInternalActivity(activity)
    }

    private fun deleteActivity(activity: Activity) {
        val where = "${UserSchema.ActivityTable.Cols.ACTIVITY_NAME}=?"
        val whereArgs = arrayOf(activity.name)
        database.delete(UserSchema.ActivityTable.NAME, where, whereArgs)
    }

    override fun addSession(session: Session, activityName: String) {
        val values = getSessionContentValues(session)
        database.insert(UserSchema.SessionTable.NAME, null, values)
    }

    override fun orderUpdated(index: Int) {
        val values = ContentValues().apply {
            put(UserSchema.ActivityTable.Cols.ORDER, index)
        }
        val where = "${UserSchema.ActivityTable.Cols.ACTIVITY_NAME}=?"
        val whereArgs = arrayOf(User.activities[index].name)
        database.update(UserSchema.ActivityTable.NAME, values, where, whereArgs)
    }

    override fun executeListDiff(activityListDiffMap: ListDiffMap<Activity>) {
        for ((activity, result) in activityListDiffMap) {
            when (result.state) {
                ListDiffEnum.MOVED_TO -> orderUpdated(result.index)
                ListDiffEnum.DELETED_AT -> deleteActivity(activity)
            }
        }
    }

    companion object {
        //Must be called before this activity is added to the local list because
        //the order is defined as the size of the list, and not size - 1
        fun getActivityContentValues(activity: Activity): ContentValues {
            val values = ContentValues()
            values.put(UserSchema.ActivityTable.Cols.ACTIVITY_NAME, activity.name)
            values.put(UserSchema.ActivityTable.Cols.CREATED, Instant.now().epochSecond)
            values.put(UserSchema.ActivityTable.Cols.ORDER, User.activities.size)
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
        val name: String = getString(getColumnIndex(UserSchema.ActivityTable.Cols.ACTIVITY_NAME))
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
            const val CREATED: String = "created"
            const val ORDER: String = "activityorder" //Order is a reserved word
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
                    + UserSchema.ActivityTable.Cols.ACTIVITY_NAME + ", "
                    + UserSchema.ActivityTable.Cols.CREATED + ", "
                    + UserSchema.ActivityTable.Cols.ORDER + ")"
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