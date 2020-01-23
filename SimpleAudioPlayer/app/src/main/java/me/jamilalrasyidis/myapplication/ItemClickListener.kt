package me.jamilalrasyidis.myapplication

import android.view.View

/**
 * Created by Jamil on 1/22/2020.
 */
interface ItemClickListener {
    fun onClick(view: View, position: Int)
}