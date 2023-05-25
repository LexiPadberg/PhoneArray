//First fragment before I edited
package com.wavesciences.phonearray

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.wavesciences.phonearray.databinding.FragmentFirstBinding
import java.io.FileOutputStream

class FirstFragment : ComponentActivity() {
    private var binding: FragmentFirstBinding? = null
    var recorder1: AudioRecord? = null
    var recorder2: AudioRecord? = null
    var isRecording = false
    private var recordingThread1: Thread? = null
    private var recordingThread2: Thread? = null
    private val pcmBufferSize: Int
        get() {
            val pcmBufSize = AudioRecord.getMinBufferSize(
                SAMPLING_RATE_IN_HZ,
                CHANNEL_CONFIG,
                AudioFormat.ENCODING_PCM_16BIT
            ) + 8191
            return pcmBufSize - pcmBufSize % 8192
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFirstBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.buttonTestRecording.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
            isRecording = !isRecording
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        recorder1 = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AudioFormat.ENCODING_PCM_16BIT,
            pcmBufferSize
        )
        recorder2 = AudioRecord(
            MediaRecorder.AudioSource.CAMCORDER,
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AudioFormat.ENCODING_PCM_16BIT,
            pcmBufferSize
        )
        recorder1?.startRecording()
        recorder2?.startRecording()
        isRecording = true

        recordingThread1 = Thread {
            val data = ByteArray(pcmBufferSize)
            val outputStream1 = FileOutputStream(getExternalFilesDir(null).toString() + "/record1.1.pcm")
            try {
                while (isRecording) {
                    val bytesRead = recorder1?.read(data, 0, data.size) ?: 0
                    if (bytesRead > 0) {
                        outputStream1.write(data, 0, bytesRead)
                    }
                }
                outputStream1.flush()
            } catch (e: Exception) {
                Log.e(TAG, "[recordingThread1] Error saving recording", e)
            } finally {
                outputStream1.close()
            }
        }

        recordingThread2 = Thread {
            val data = ByteArray(pcmBufferSize)
            val outputStream2 = FileOutputStream(getExternalFilesDir(null).toString() + "/record2.1.pcm")
            try {
                while (isRecording) {
                    val bytesRead = recorder2?.read(data, 0, data.size) ?: 0
                    if (bytesRead > 0) {
                        outputStream2.write(data, 0, bytesRead)
                    }
                }
                outputStream2.flush()
            } catch (e: Exception) {
                Log.e(TAG, "[recordingThread2] Error saving recording", e)
            } finally {
                outputStream2.close()
            }
        }

        recordingThread1?.start()
        recordingThread2?.start()
    }

    private fun stopRecording() {
        isRecording = false
        recorder1?.stop()
        recorder1?.release()
        recorder2?.stop()
        recorder2?.release()
        recorder1 = null
        recorder2 = null
    }

    companion object {
        const val TAG = "MainActivity"
        private const val SAMPLING_RATE_IN_HZ = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    }
}