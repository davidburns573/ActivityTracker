package tech.davidburns.activitytracker.util

const val DELETED = -1

enum class ListDiffEnum {
    MOVED_TO, DELETED_AT
}

typealias ListDiffMap<T> = MutableMap<T, ListDiff.Result>

open class ListDiff<T>(private val list: MutableList<T>) {
    protected val map: ListDiffMap<T> = mutableMapOf()

    fun itemMoved(to: Int) {
        val original = map[list[to]]
        if (original != null) {
            if (original.initialIndex == to) {
                map.remove(list[to])
            } else {
                map[list[to]] = Result(ListDiffEnum.MOVED_TO, to, original.initialIndex)
            }
        }
    }

    fun itemDeleted(item: T) {
        map[item] = Result(ListDiffEnum.DELETED_AT, DELETED, DELETED)
    }

    data class Result(val state: ListDiffEnum, val index: Int, val initialIndex: Int)
}