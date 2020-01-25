package com.rasyidcode.simpleflashlight

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.rasyidcode.simpleflashlight.databinding.ActivityMainBinding
import java.lang.RuntimeException

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    private var camera: Camera? = null
    private var isFlashOn: Boolean = false
    private var hasFlash: Boolean = false
    private var params: Camera.Parameters? = null
    private var mediaPlayer: MediaPlayer? = null

    private val mainActivityListener by lazy {
        object : MainActivityInterface {
            override fun flashListener() {
                if (isFlashOn) turnOffFlash()
                else turnOnFlash()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkFlash()

        binding.flashOnOff.setOnCheckedChangeListener { _, _ -> mainActivityListener.flashListener() }
    }

    private fun checkFlash() {
        hasFlash = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (!hasFlash) {
            val alert = AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Sorry, your device doesn't support flash light!")
                .setPositiveButton("OK") { _, _ ->
                    finish()
                }
            alert.show()
            return
        }
    }

    private fun getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open()
                camera?.let {
                    params = it.parameters
                }
            } catch (e: RuntimeException) {
                Log.e("MainActivity","Camera Error. Failed to Open. Error: ${e.message}")
            }
        }
    }

    private fun turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return
            }

            playSound()

            camera?.let {
                params = it.parameters
                params?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                it.parameters = params
                it.startPreview()
            }

            isFlashOn = true

            toggleFlash()
        }
    }

    private fun turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return
            }

            playSound()

            camera?.let {
                params = it.parameters
                params?.flashMode = Camera.Parameters.FLASH_MODE_OFF
                it.parameters = params
                it.stopPreview()
            }

            isFlashOn = false

            toggleFlash()
        }
    }

    private fun playSound() {
        mediaPlayer = if (isFlashOn) {
            MediaPlayer.create(this, R.raw.turn_on)
        } else {
            MediaPlayer.create(this, R.raw.turn_off)
        }
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        mediaPlayer?.start()
    }

    private fun toggleFlash() {
        if (!isFlashOn) {
            binding.textStatus.text = resources.getString(R.string.text_status_off)
            binding.flashOnOff.isChecked = false
        } else {
            binding.textStatus.text = resources.getString(R.string.text_status_on)
            binding.flashOnOff.isChecked = true
        }
    }

    override fun onPause() {
        super.onPause()
        turnOffFlash()
    }

    override fun onResume() {
        super.onResume()
        if (hasFlash) turnOnFlash()
    }

    override fun onStart() {
        super.onStart()
        getCamera()
    }

    override fun onStop() {
        super.onStop()

        if (camera != null) {
            camera?.release()
            camera = null
        }
    }
}

interface MainActivityInterface {
    fun flashListener()
}
