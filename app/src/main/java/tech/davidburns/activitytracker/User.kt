package tech.davidburns.activitytracker

class User(var username: String, var password: String) {
    var activities: MutableList<Activity> = mutableListOf()

    fun addActivity(name: String) {
        activities.add(Activity(name))
    }
}