package me.jamilalrasyidis.myapplication

import android.os.Environment
import java.io.File
import java.lang.Exception

/**
 * Created by Jamil on 1/22/2020.
 */
object SongManager {
    @Suppress("DEPRECATION")
    val rootPath: String by lazy { Environment.getExternalStorageDirectory().absolutePath }

    fun getSongFromStorage(path: String): ArrayList<HashMap<String, String>>? {
        try {
            val home = File(path)
            val songList = ArrayList<HashMap<String, String>>()
            home.listFiles()?.let {
                if (it.isNotEmpty()) {
                    for (file: File in it) {
                        if (file.isDirectory) {
                            if (getSongFromStorage(file.absolutePath) != null) {
                                getSongFromStorage(file.absolutePath)?.let { it1 ->
                                    songList.addAll(it1)
                                }
                            } else {
                                break
                            }
                        } else if (file.name.endsWith(".mp3") || file.name.endsWith(".MP3")) {
                            val song = HashMap<String, String>()
                            song["songTitle"] = file.name.substring(0, (file.name.length - 4))
                            song["songPath"] = file.path
                            songList.add(song)
                        }
                    }
                }
            }

            return songList
        } catch (e: Exception) {
            return null
        }
    }

}