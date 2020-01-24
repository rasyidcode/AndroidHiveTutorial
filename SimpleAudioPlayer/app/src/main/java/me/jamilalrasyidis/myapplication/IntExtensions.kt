package me.jamilalrasyidis.myapplication

/**
 * Created by Jamil on 1/22/2020.
 */

fun Int.progressToTimer(totalMillis: Int): Int {
    val totalSecond = totalMillis / 1000
    val currentSecond = (this.toDouble() / 100) * totalSecond
    return (currentSecond * 1000).toInt()
}

fun Int.toProperTimer(): String {
    return if (this < 10) {
        "0$this"
    } else {
        "$this"
    }
}