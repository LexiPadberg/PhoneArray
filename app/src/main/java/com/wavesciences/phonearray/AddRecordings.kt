package com.wavesciences.phonearray

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity

import com.wavesciences.phonearray.databinding.ActivityAddRecordingsBinding

import java.io.FileOutputStream

class AddRecordings : ComponentActivity() {
    private var binding: ActivityAddRecordingsBinding? = null
    private var recorder1: AudioRecord? = null
    private var recorder2: AudioRecord? = null
    private var isRecording = false
    private val recordName = binding?.recordingName.toString()
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
        binding = ActivityAddRecordingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.startRecordingBtn.setOnClickListener {
           startRecording()
        }
        binding!!.stopRecordingBtn.setOnClickListener{
            stopRecording()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        recorder1 = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            pcmBufferSize
        )
        recorder2 = AudioRecord(
            MediaRecorder.AudioSource.CAMCORDER,
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            pcmBufferSize
        )
        recorder1?.startRecording()
        recorder2?.startRecording()
        isRecording = true


        //Start the recording threads.
        //TODO: Clean this up by encapsulating the thread method. Fine tune the buffer size.
        recordingThread1 = Thread {
            val data = ByteArray(pcmBufferSize)

            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 1")
            try {
                FileOutputStream(
                    getExternalFilesDir(null)
                        .toString() + "/"+ recordName +"_record1.1.pcm"
                ).use { os ->
                    var totalBytes: Long = 0
                    while (isRecording) {
                        val bytesRead = recorder1!!.read(data, 0, data.size)
                        if (bytesRead > 0) {
                            totalBytes += bytesRead.toLong()
                            os.write(data, 0, bytesRead)
                        }
                    }
                    os.flush()
                    Log.d(TAG,">>>>>>>>>>> Total Bytes 1: $totalBytes")

                }
            } catch (e: Exception) {
                Log.e(TAG, "[recordingThread1] Error saving recording", e)
            }
        }

        recordingThread2 = Thread {
            val data = ByteArray(pcmBufferSize)

            //val recordName = binding?.recordingName.toString()

            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 2")
            try {
                FileOutputStream(
                    getExternalFilesDir(null)
                        .toString() + "/" + recordName + "_record1.1.pcm"
                ).use { os ->
                    var totalBytes: Long = 0
                    while (isRecording) {
                        val bytesRead =recorder1!!.read(data, 0, data.size)
                        if (bytesRead > 0) {
                            totalBytes += bytesRead.toLong()
                            os.write(data, 0, bytesRead)
                        }
                    }
                    os.flush()
                    Log.d(TAG,">>>>>>>>>>> Total Bytes 2: $totalBytes")

                }
            } catch (e: Exception) {
                Log.e(TAG, "[recordingThread2] Error saving recording", e)
            }

        }

        Log.d(TAG, "[buttonTestRecording] Starting the Recording")
        recordingThread1!!.start()
        recordingThread2!!.start()
    }

    private fun stopRecording() {
        Log.d(TAG, "[buttonTestRecording] Stopping the Recording")
        isRecording = false
        recorder1!!.stop()
        recorder1!!.release()
        recorder2!!.stop()
        recorder2!!.release()
        recorder1 = null  //Release for GC
        recorder2 = null  //Release for GC
    }

    companion object {
        val TAG = AddRecordings::class.java.simpleName
        private const val SAMPLING_RATE_IN_HZ = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}