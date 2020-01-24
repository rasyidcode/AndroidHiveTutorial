package me.jamilalrasyidis.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.jamilalrasyidis.myapplication.databinding.ActivityPlayerBinding
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.random.Random

/**
 * Created by Jamil on 1/22/2020.
 */
class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener,
    SeekBar.OnSeekBarChangeListener {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityPlayerBinding>(
            this,
            R.layout.activity_player
        )
    }

    private val mediaPlayer by lazy { MediaPlayer() }

    private lateinit var songList: ArrayList<HashMap<String, String>>

    private var currentSongIndex = 0
    private var seekForwardTime = 5000
    private var seekBackwardTime = 5000
    private var isShuffle = false
    private var isRepeat = false

    private var handler = Handler()

    private val updateTimeTask by lazy {
        object : Runnable {
            override fun run() {
                val totalDuration = mediaPlayer.duration.toLong()
                val currentDuration = mediaPlayer.currentPosition.toLong()

                binding.textTotalDuration.text = totalDuration.milliSecondsToTimer()
                binding.textCurrentDuration.text = currentDuration.milliSecondsToTimer()

                val progress = currentDuration.timerToProgress(totalDuration)
                binding.progressBarSong.progress = progress

                handler.postDelayed(this, 100)
            }

        }
    }

    private val playerButtonsListener by lazy {
        object : PlayerButtonsListener {
            override fun onShuffleClick() {
                if (isShuffle) {
                    isShuffle = false
                    binding.buttonShuffle.isChecked = false

                    Toast.makeText(this@PlayerActivity, "Shuffle is OFF", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    turnOffRepeat()
                    isShuffle = true
                    binding.buttonShuffle.isChecked = true

                    Toast.makeText(this@PlayerActivity, "Shuffle is ON", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onRepeatClick() {
                if (isRepeat) {
                    isRepeat = false
                    binding.buttonRepeat.isChecked = false

                    Toast.makeText(this@PlayerActivity, "Repeat is OFF", Toast.LENGTH_SHORT).show()
                } else {
                    turnOffShuffle()
                    isRepeat = true
                    binding.buttonRepeat.isChecked = true

                    Toast.makeText(this@PlayerActivity, "Repeat is ON", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSkipBackwardClick() {
                val currentPosition = mediaPlayer.currentPosition
                if (currentPosition - seekBackwardTime >= 0) {
                    mediaPlayer.seekTo(currentPosition - seekForwardTime)
                } else {
                    mediaPlayer.seekTo(0)
                }
            }

            override fun onPreviousClick() {
                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1)
                    currentSongIndex -= 1
                } else {
                    playSong((songList.size).minus(1))
                    currentSongIndex = (songList.size).minus(1)
                }
            }

            override fun onPlayPauseClick() {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    binding.buttonPlayPause.isChecked = true
                } else {
                    mediaPlayer.start()
                    binding.buttonPlayPause.isChecked = false
                }
            }

            override fun onNextClick() {
                if (currentSongIndex < (songList.size).minus(1)) {
                    playSong(currentSongIndex + 1)
                    currentSongIndex += 1
                } else {
                    playSong(0)
                    currentSongIndex = 0
                }
            }

            override fun onSkipForwardClick() {
                val currentPosition = mediaPlayer.currentPosition
                if (currentPosition + seekForwardTime <= mediaPlayer.duration) {
                    mediaPlayer.seekTo(currentPosition + seekForwardTime)
                } else {
                    mediaPlayer.seekTo(mediaPlayer.duration)
                }
            }
        }

    }

    @Suppress("DEPRECATION")
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Fetch your local audio file...")
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAudioData()

        binding.progressBarSong.setOnSeekBarChangeListener(this)
        mediaPlayer.setOnCompletionListener(this)

        binding.buttonPlaylist.setOnClickListener {
            startActivityForResult(Intent(this, PlaylistActivity::class.java), 100)
        }

        binding.buttonShuffle.setOnCheckedChangeListener { _, _ -> playerButtonsListener.onShuffleClick() }
        binding.buttonRepeat.setOnCheckedChangeListener { _, _ -> playerButtonsListener.onRepeatClick() }
        binding.buttonSkipBackward.setOnClickListener { playerButtonsListener.onSkipBackwardClick() }
        binding.buttonPrevious.setOnClickListener { playerButtonsListener.onPreviousClick() }
        binding.buttonPlayPause.setOnCheckedChangeListener { _, _ -> playerButtonsListener.onPlayPauseClick() }
        binding.buttonNext.setOnClickListener { playerButtonsListener.onNextClick() }
        binding.buttonSkipForward.setOnClickListener { playerButtonsListener.onSkipForwardClick() }
    }

    private fun setupAudioData() {
        progressDialog.show()
        Thread {
            SongManager.getSongFromStorage(SongManager.rootPath)?.let {
                songList = it
            }

            runOnUiThread {
                progressDialog.dismiss()
            }
        }.start()
    }

    private fun turnOffShuffle() {
        isShuffle = false
        binding.buttonShuffle.setOnCheckedChangeListener(null)
        binding.buttonShuffle.isChecked = false
        binding.buttonShuffle.setOnCheckedChangeListener { _, _ -> playerButtonsListener.onShuffleClick() }
    }

    private fun turnOffRepeat() {
        isRepeat = false
        binding.buttonRepeat.setOnCheckedChangeListener(null)
        binding.buttonRepeat.isChecked = false
        binding.buttonRepeat.setOnCheckedChangeListener { _, _ -> playerButtonsListener.onRepeatClick() }
    }

    private fun playSong(currentSongIndex: Int) {
        try {
            val songTitle = songList[currentSongIndex]["songTitle"] ?: ""
            val songPath = songList[currentSongIndex]["songPath"]
            mediaPlayer.reset()
            mediaPlayer.setDataSource(songPath)
            mediaPlayer.prepare()
            mediaPlayer.start()

            binding.textSongTitle.text = songTitle
            songPath?.let { setupSongThumbnail(it) }
            binding.progressBarSong.progress = 0
            binding.progressBarSong.max = 100

            updateProgressBar()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                currentSongIndex = data?.extras?.getInt("songIndex")!!
                playSong(currentSongIndex)
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (isRepeat) {
            playSong(currentSongIndex)
        } else if (isShuffle) {
            currentSongIndex = Random.nextInt((songList.size - 1) - 0 + 1) + 0
            playSong(currentSongIndex)
        } else {
            if (currentSongIndex < (songList.size - 1)) {
                playSong(currentSongIndex + 1)
                currentSongIndex += 1
            } else {
                playSong(0)
                currentSongIndex = 0
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        handler.removeCallbacks(updateTimeTask)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        handler.removeCallbacks(updateTimeTask)
        val totalDuration = mediaPlayer.duration
        val currentPosition = seekBar?.progress?.progressToTimer(totalDuration)
        if (currentPosition != null) {
            mediaPlayer.seekTo(currentPosition)
        }

        updateProgressBar()
    }

    private fun setupSongThumbnail(songPath: String) {
        val mmr = MediaMetadataRetriever().apply {
            setDataSource(songPath)
        }

        val data: ByteArray? = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.imageSongThumb.background = ColorDrawable(Color.TRANSPARENT)
            } else {
                @Suppress("DEPRECATION")
                binding.imageSongThumb.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            binding.imageSongThumb.setImageBitmap(bitmap)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                @Suppress("DEPRECATION")
                binding.imageSongThumb.background =
                    resources.getDrawable(R.drawable.song_no_thumbnail)
            } else {
                @Suppress("DEPRECATION")
                binding.imageSongThumb.setBackgroundDrawable(
                    resources.getDrawable(
                        R.drawable.song_no_thumbnail,
                        null
                    )
                )
            }
        }
    }
}