package tech.davidburns.activitytracker

import android.content.Context
import tech.davidburns.activitytracker.interfaces.ActivityListener
import tech.davidburns.activitytracker.interfaces.Database

object User {
    var name: String = "UNNAMED"
    lateinit var currentActivity: Activity
    lateinit var applicationContext: Context

    private lateinit var database: Database

    val activities: MutableList<Activity>
        get() = database.activities

    /**
     * Sets the desired database for the current user.
     * Must be set upon app initialization.
     * Can be changed at any time. TODO
     * @param database to store content in.
     */
    fun setDatabase(database: Database) {
        this.database = database
    }

    /**
     * Create activity with given name and add to the database.
     * @param name of the [Activity] to create
     */
    fun addActivity(name: String) =
        addActivity(Activity(name))

    /**
     * Create activity with given name and add to the database.
     * @param activity to add
     */
    fun addActivity(activity: Activity) {
        database.addActivity(activity)
    }

    /**
     * Notify the database that the order of an activity at a specific index has changed.
     * @param index at which order was changed
     */
    fun orderUpdated(index: Int) {
        database.orderUpdated(index)
    }

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

    /**
     * Add a listener who will be notified whenever activities list is changed
     * @param listener to be notified
     */
    fun addActivityListener(listener: ActivityListener) {
        database.listeners.add(listener)
    }

    /**
     * Remove a listener
     * @param listener to be removed
     */
    fun removeActivityListener(listener: ActivityListener) {
        database.listeners.remove(listener)
    }
}
