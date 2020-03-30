package tech.davidburns.activitytracker.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session

/**
 * Abstract class for database communication. Retrieving
 * information from the database may take time. Should be implemented
 * in an asynchronous manner TODO
 * On init, Database is forced to initialize itself.
 * @author Charles Jenkins
 * @author David Burns
 */
abstract class Database {
    init {
        this.initializeDatabase()
    }

    /**
     * Must be called before any other methods are called.
     * Ensures that the database is ready to be written to / read from
     */
    protected abstract fun initializeDatabase()

    abstract fun setUserInfo()

    /**
     * User will most likely not have very many activities, but take caution when retrieving all.
     * @return a list of all activities
     */
    abstract fun getActivities(): List<Activity>

    /**
     * User may have a lot of sessions, so take caution when retrieving all.
     * @param activityName that contains sessions
     * @return a list of all sessions belonging to an [Activity], will be empty if no sessions
     */
    abstract fun getSessionsFromActivity(activityName: String): List<Session>

    /**
     * Add an activity to the local activity cache and to the database
     * @param activity is the activity to add to the database
     */
    abstract fun addActivity(activity: Activity)

    /**
     * Add a session pertaining to an activity to the database
     * @param session is the session to add to the database
     * @param activityName is the name of the activity to which the session belongs
     */
    abstract fun addSession(session: Session, activityName: String)
}