package tech.davidburns.activitytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.util.ActivityBaseHelper
import tech.davidburns.activitytracker.util.ActivitySchema

//class User(var name: String) {
//    constructor(firebaseUser: FirebaseUser) : this(
//        firebaseUser.displayName ?: "UNNAMED"
//    )
//
//    var activities: MutableList<Activity> = mutableListOf()
//
//    fun addActivity(name: String) {
//        activities.add(Activity(name))
//    }
//}

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
        database.insert(ActivitySchema.ActivityTable.NAME, null, values)
    }

    fun initDatabase(context: Context) {
        database = ActivityBaseHelper(context).writableDatabase
    }

    fun queryActivities(whereClause: String?, whereArgs: Array<String>?): ActivityCursorWrapper {
        val cursor: Cursor = database.query(
            ActivitySchema.ActivityTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null)
        return ActivityCursorWrapper(cursor)
    }

    fun setActivitiesFromDB() {
        activities = mutableListOf()

        var cursor: ActivityCursorWrapper = queryActivities(null, null)

        try {
            cursor.moveToFirst()
            while (!(cursor.isAfterLast)) {
                activities.add(cursor.getActivity())
                cursor.moveToNext()
            }
        } finally {
            cursor?.close()
        }
    }
}