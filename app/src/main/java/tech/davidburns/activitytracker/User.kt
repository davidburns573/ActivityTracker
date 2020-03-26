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

    var implementedDatabase: Database? = null

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