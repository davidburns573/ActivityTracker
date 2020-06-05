package tech.davidburns.activitytracker.util

import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.User

class ActivityListDiff : ListDiff<Activity>(User.activities) {
    fun commitToDatabase() {
        User.executeListDiff(map)
    }
}