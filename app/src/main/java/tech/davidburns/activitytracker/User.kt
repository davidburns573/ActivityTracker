package tech.davidburns.activitytracker

import android.content.Context
import tech.davidburns.activitytracker.interfaces.Database
import tech.davidburns.activitytracker.interfaces.DatabaseListener

object User {
    var name: String = "UNNAMED"
    lateinit var currentActivity: Activity
    lateinit var applicationContext: Context

    private lateinit var database: Database

    fun setDatabase(database: Database) {
        this.database = database
    }

    val activities: MutableList<Activity>
        get() = database.activities

    /**
     * Add specific activity object to database.
     * @param activity to store in the database
     */
    fun addActivity(activity: Activity) = database.addActivity(activity)

    /**
     * Create activity with given name and add to the database.
     * @param name of the [Activity] to create
     */
    fun addActivity(name: String) = addActivity(Activity(name))

    /**
     * Add given session to the database attached to the given activity.
     * @param session to add to database
     * @param activityName to attach the session to
     */
    fun addSession(session: Session, activityName: String) =
        database.addSession(session, activityName)

    /**
     * Retrieve all [Session] belonging to a specific activity.
     * @return list of [Session] belonging to the activity given
     */
    fun getSessionsFromActivity(activityName: String): MutableList<Session> =
        database.getSessionsFromActivity(activityName)

    /**
     * Retrieve all [Session] belonging to a specific activity.
     * @return list of [Session] belonging to the activity given
     */
    fun getSessionsFromCurrentActivity(): MutableList<Session> =
        database.getSessionsFromActivity(currentActivity.name)

    fun addActivityListener(listener: DatabaseListener) {
        database.addListener(listener)
    }

    fun removeActivityListener(listener: DatabaseListener) {
        database.removeListener(listener)
    }
}