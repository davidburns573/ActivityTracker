package tech.davidburns.activitytracker.interfaces

import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session

/**
 * Abstract class for database communication.
 * Retrieving information from the database may take time.
 * Should be implemented in an asynchronous manner TODO.
 * On init, Database is forced to initialize itself.
 * @author Charles Jenkins
 * @author David Burns
 */
abstract class Database {
    val activities: MutableList<Activity> = mutableListOf()

    var listeners: MutableList<ActivityListener> = mutableListOf()

    /**
     * User may have a lot of sessions, so take caution when retrieving all.
     * @param activityName that contains sessions
     * @return a list of all sessions belonging to an [Activity], will be empty if no sessions
     */
    abstract fun getSessionsFromActivity(activityName: String): MutableList<Session>

    /**
     * Add an activity to the local activity cache and to the database.
     * @param activity is the activity to add to the database
     */
    abstract fun addActivity(activity: Activity)

    /**
     * Delete an activity at index.
     * @param index of the activity to delete
     */
    abstract fun deleteActivityAt(index: Int)

    protected fun addInternalActivity(activity: Activity) {
        activities.add(activity)
        listeners.forEach { it.itemAdded(activities.size - 1) }
    }

    protected fun deleteInternalActivity(activityOrder: Int) {
        activities.removeAt(activityOrder)
        listeners.forEach { it.itemRemoved(activityOrder) }

    }

    /**
     * Add a session pertaining to an activity to the database.
     * @param session is the session to add to the database
     * @param activityName is the name of the activity to which the session belongs
     */
    abstract fun addSession(session: Session, activityName: String)

    /**
     * Notify the database that the order of an activity at a specific index has changed.
     * Should only be called after activities list has been updated to the desired order.
     * @param index at which order was changed.
    */
    abstract fun orderUpdated(index: Int)
}