package com.wavesciences.phonearray

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wavesciences.phonearray.databinding.ActivityAddRecordingsBinding

import java.io.File


class AddRecordings : ComponentActivity() {
    private lateinit var binding: ActivityAddRecordingsBinding
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioFilePath: String? = null
    private var isRecording = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        audioSetup()

        binding.homeBtn.setOnClickListener {
            finish()
        }
    }

    private fun hasMicrophone(): Boolean {
        val pmanager = this.packageManager
        return pmanager.hasSystemFeature(
            PackageManager.FEATURE_MICROPHONE)
    }

    private fun audioSetup() {
        if (!hasMicrophone()) {
            binding.stopRecordingBtn.isEnabled = false
            //binding.playBtn.isEnabled = false
            binding.startRecordingBtn.isEnabled = false
        } else {
            //binding.playBtn.isEnabled = false
            binding.stopRecordingBtn.isEnabled = false
        }

        val audioFile = File(this.filesDir, "myaudio.3gp")
        audioFilePath = audioFile.absolutePath
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun recordAudio(view: View) {
        val recordingName = binding.recordingName.text.toString()
        isRecording = true
        binding.stopRecordingBtn.isEnabled = true
        binding.startRecordingBtn.isEnabled = false
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            //will need to change to separate microphones
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            val recordingFile = File(this.filesDir, "$recordingName.mp4")
            audioFilePath = recordingFile.absolutePath
            mediaRecorder?.setOutputFile(audioFilePath)

            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun stopAudio(view: View) {
        binding.stopRecordingBtn.isEnabled = false
        binding.startRecordingBtn.isEnabled = true
        if (isRecording) {
            binding.startRecordingBtn.isEnabled = false
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }
}