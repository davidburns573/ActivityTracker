package tech.davidburns.activitytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.util.UserBaseHelper
import tech.davidburns.activitytracker.util.UserSchema

object User {
    var authenticate: FirebaseUser? = null
    var name: String = "UNNAMED"

    var firebaseUser: FirebaseUser? = null

    var activities: MutableList<Activity> = mutableListOf()

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

    fun setActivitiesFromDB() {
        activities.clear()

        val cursor: ActivityCursorWrapper = queryActivities(null, null)
        try {
            cursor.moveToFirst()
            while (!(cursor.isAfterLast)) {
                activities.add(cursor.getActivity())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
    }
}