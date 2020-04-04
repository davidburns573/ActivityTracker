package tech.davidburns.activitytracker.interfaces

import androidx.annotation.CallSuper
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
    private val listeners: MutableList<DatabaseListener> = mutableListOf()
    protected val _activities: MutableList<Activity> = mutableListOf()
    val activities: MutableList<Activity>
        get() = _activities

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
    @CallSuper
    open fun addActivity(activity: Activity) {
        _activities.add(activity)
        listeners.forEach { it.itemAdded(activities.size) }
    }

    fun addActivity(activityName: String) = addActivity(Activity(activityName))

    /**
     * Add a session pertaining to an activity to the database.
     * @param session is the session to add to the database
     * @param activityName is the name of the activity to which the session belongs
     */
    abstract fun addSession(session: Session, activityName: String)

    fun addListener(listener: DatabaseListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: DatabaseListener) {
        listeners.remove(listener)
    }
}

interface DatabaseListener {
    fun itemChanged(index: Int)
    fun itemRemoved(index: Int)
    fun itemAdded(index: Int)
    fun itemRangeAdded(start: Int, itemCount: Int)
}