package vn.hexagon.vietnhat.base.utils


fun <T> MutableList<T>.removeAt(startIndex: Int, count: Int) {
    for (i in startIndex until startIndex + count) {
        if (startIndex >= this.size) {
            return
        }
        this.removeAt(startIndex)
    }
}


inline fun <T> List<T>.forEachDown(action: (T) -> Unit) {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        action.invoke(iterator.previous())
    }
}

inline fun <T> List<T>.forEachIndexedDown(action: (index: Int, T) -> Unit) {
    val iterator = this.listIterator(size)
    var index = size - 1
    while (iterator.hasPrevious()) {
        action.invoke(index--, iterator.previous())
    }
}

inline fun <T> MutableList<T>.removeBy(predicate: (T) -> Boolean) {
    val listIterator = listIterator(size)
    while (listIterator.hasPrevious()) {
        val element = listIterator.previous()
        if (predicate(element)) {
            remove(element)
        }
    }
}