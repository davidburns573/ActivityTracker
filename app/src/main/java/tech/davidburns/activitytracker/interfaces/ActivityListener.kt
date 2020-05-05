package tech.davidburns.activitytracker.interfaces

interface ActivityListener {
    fun itemChanged(index: Int)
    fun itemRemoved(index: Int)
    fun itemAdded(index: Int)
    fun itemRangeAdded(start: Int, itemCount: Int)
}