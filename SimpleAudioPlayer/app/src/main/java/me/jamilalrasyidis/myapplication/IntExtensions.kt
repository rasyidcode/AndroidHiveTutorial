package me.jamilalrasyidis.myapplication

/**
 * Created by Jamil on 1/22/2020.
 */

fun Int.progressToTimer(totalDuration: Int): Int {
    return ((this / 100) * (totalDuration / 1000)) * 1000
}

fun Int.toProperTimer(): String {
    return if (this < 10) {
        "0$this"
    } else {
        "$this"
    }
}