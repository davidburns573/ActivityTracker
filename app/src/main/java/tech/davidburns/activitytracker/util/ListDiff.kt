package tech.davidburns.activitytracker.util

enum class ListDiffEnum {
    MOVED_TO, DELETED_AT
}

typealias ListDiffMap<T> = MutableMap<T, ListDiff.Result>

open class ListDiff<T>(private val list: MutableList<T>) {
    protected val map: ListDiffMap<T> = hashMapOf()

    fun itemMoved(from: Int, to: Int) {
        if (map[list[to]]?.initialIndex == to) {
            map.remove(list[to])
        } else {
            map[list[to]] = Result(ListDiffEnum.MOVED_TO, to, map[list[to]]?.initialIndex ?: from)
        }
    }

    fun itemDeleted(item: T, index: Int) {
        map[item] = Result(
            ListDiffEnum.DELETED_AT,
            index,
            map[item]?.initialIndex ?: index
        )

        for (i in index until list.size) {
            itemMoved(i + 1, i)
        }
    }

    class Result(val state: ListDiffEnum, val index: Int, val initialIndex: Int)
}