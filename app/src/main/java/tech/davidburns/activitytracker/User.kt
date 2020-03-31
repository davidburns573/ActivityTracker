package tech.davidburns.activitytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.util.UserBaseHelper
import tech.davidburns.activitytracker.util.UserSchema
import tech.davidburns.activitytracker.interfaces.Database

object User {
    var authenticate: FirebaseUser? = null
    var name: String = "UNNAMED"

    var activities: MutableList<Activity> = mutableListOf()

    lateinit var implementedDatabase: Database

    lateinit var database: SQLiteDatabase

    fun addActivity(name: String) {
        val activity = Activity(name)
        activities.add(activity)
        val values: ContentValues = Activity.getContentValues(activity)
        database.insert(UserSchema.ActivityTable.NAME, null, values)
    }

    fun initDatabase(context: Context) {
        database = UserBaseHelper(context).writableDatabase
    }

    fun queryActivities(
        whereClause: String?,
        whereArgs: Array<String>?
    ): ActivityCursorWrapper {
        val cursor: Cursor = database.query(
            UserSchema.ActivityTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        )
        return ActivityCursorWrapper(cursor)
    }

    /**
     * Clears current activities and then retrieves activities from the database
     */
    fun setActivitiesFromDB() {
        activities.clear()

        queryActivities(null, null).use {cursor ->
            cursor.moveToFirst()
            while (!(cursor.isAfterLast)) {
                activities.add(cursor.getActivity())
                cursor.moveToNext()
            }
        }
    }
}