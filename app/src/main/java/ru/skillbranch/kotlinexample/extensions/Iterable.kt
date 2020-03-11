package ru.skillbranch.kotlinexample.extensions

/* Kotlin idiomatic variant */
fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> =
    run {
        toMutableList().apply {
            listIterator(size).apply {
                while (hasPrevious()) {
                    val element = previous()
                    if (predicate(element)) {
                        remove()
                        break
                    } else {
                        remove()
                    }
                }
            }
        }.toList()
    }

/* Usual variant */
//fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
//    val mutableList = this.toMutableList()
//    val iterator = mutableList.listIterator(mutableList.size)
//    while (iterator.hasPrevious()) {
//        val element = iterator.previous()
//        if (predicate(element)) {
//            iterator.remove()
//            break
//        } else {
//            iterator.remove()
//        }
//    }
//    return mutableList.toList()
//}