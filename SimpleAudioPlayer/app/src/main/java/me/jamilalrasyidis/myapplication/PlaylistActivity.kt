package me.jamilalrasyidis.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import me.jamilalrasyidis.myapplication.databinding.ActivityPlaylistBinding
import java.io.File

/**
 * Created by Jamil on 1/22/2020.
 */
class PlaylistActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityPlaylistBinding>(
            this,
            R.layout.activity_playlist
        )
    }

    private val adapter by lazy { PlaylistAdapter() }

    private val songList by lazy {
        SongManager.getSongFromStorage(SongManager.rootPath)
    }

    private val itemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@PlaylistActivity, PlayerActivity::class.java).apply {
                    putExtra("songIndex", position)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        songList?.let {
            adapter.songList = it
        }
        adapter.itemClickListener = itemClickListener
        binding.listSong.layoutManager = LinearLayoutManager(this)
        binding.listSong.adapter = adapter
    }

}