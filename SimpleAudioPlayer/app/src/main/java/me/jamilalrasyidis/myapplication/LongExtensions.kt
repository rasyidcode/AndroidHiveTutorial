package me.jamilalrasyidis.myapplication

/**
 * Created by Jamil on 1/22/2020.
 */

fun Long.timerToProgress(totalDuration: Long) : Int {
    val currentSeconds = this / 1000
    val totalSeconds = totalDuration / 1000
    val percentage = (currentSeconds.toDouble() / totalSeconds) * 100
    return percentage.toInt()
}

fun Long.milliSecondsToTimer(): String {
    val hours = (this / Constant.HOUR).toInt()
    val minutes = ((this % Constant.HOUR) / Constant.MINUTE).toInt()
    val seconds = ((this % Constant.HOUR) % Constant.MINUTE / Constant.SECOND).toInt()

    return if (hours > 0) {
        "${hours.toProperTimer()}:${minutes.toProperTimer()}:${seconds.toProperTimer()}"
    } else {
        "${minutes.toProperTimer()}:${seconds.toProperTimer()}"
    }
}