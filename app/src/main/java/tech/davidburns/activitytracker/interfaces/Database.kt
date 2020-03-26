package tech.davidburns.activitytracker.interfaces

import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session

/**
 * Interface for database communication. All functions are marked with suspend, as retrieving
 * information from the database may take time and cause blocking otherwise. Should be implemented
 * in an asynchronous manner.
 * @author Charles Jenkins
 * @author David Burns
 */
interface Database {
    /**
     * Must be called before any other methods are called.
     * Ensures that the database is ready to be written to / read from
     */
    suspend fun initializeDatabase()

    /**
     * User will most likely not have very many activities, but take caution when retrieving all.
     * @return a list of all activities
     */
    suspend fun getActivities(): List<Activity>

    /**
     * User may have a lot of sessions, so take caution when retrieving all.
     * @param activity that contains sessions
     * @return a list of all sessions belonging to an [Activity], will be empty if no sessions
     */
    suspend fun getSessionsFromActivity(activity: Activity): List<Session>
}