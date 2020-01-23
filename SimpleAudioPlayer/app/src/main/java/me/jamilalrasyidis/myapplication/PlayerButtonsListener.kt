package me.jamilalrasyidis.myapplication

/**
 * Created by Jamil on 1/22/2020.
 */
interface PlayerButtonsListener {
    fun onShuffleClick()
    fun onRepeatClick()
    fun onSkipBackwardClick()
    fun onPreviousClick()
    fun onPlayPauseClick()
    fun onNextClick()
    fun onSkipForwardClick()
}