package me.jamilalrasyidis.myapplication

import android.app.Activity
import android.app.ProgressDialog
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

    private lateinit var songList: ArrayList<HashMap<String, String>>

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

    @Suppress("DEPRECATION")
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Fetch your local audio file...")
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAudioList()
    }

    private fun setupAudioList() {
        progressDialog.show()
        Thread {
            SongManager.getSongFromStorage(SongManager.rootPath)?.let {
                songList = it
            }

            runOnUiThread {
                adapter.songList = songList
                adapter.itemClickListener = itemClickListener

                binding.listSong.layoutManager = LinearLayoutManager(this)
                binding.listSong.adapter = adapter

                progressDialog.dismiss()
            }
        }.start()
    }

}