package main.framework

/**
 *
 */
fun <T> List<T>.contains(predicate: (T) -> Boolean): Boolean {
    for (value in this) {
        if (predicate.invoke(value))
            return true
    }
    return false
}