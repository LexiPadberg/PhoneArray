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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.wavesciences.phonearray.databinding.FragmentFirstBinding
import java.io.FileOutputStream

class FirstFragment : Fragment() {
    private var binding: FragmentFirstBinding? = null
    var recorder1: AudioRecord? = null
    var recorder2: AudioRecord? = null
    var isRecording = false
    private var recordingThread1: Thread? = null
    private var recordingThread2: Thread? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private val pcmBufferSize: Int
        get() {
            val pcmBufSize = AudioRecord.getMinBufferSize(
                SAMPLING_RATE_IN_HZ,
                CHANNEL_CONFIG,
                AudioFormat.ENCODING_PCM_16BIT
            ) + 8191
            return pcmBufSize - pcmBufSize % 8192
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonTestRecording.setOnClickListener(object : View.OnClickListener {
            @SuppressLint("MissingPermission")
            override fun onClick(v: View) {
                Log.d(TAG, "[buttonTestRecording] BINDING CLICKED")
                if (recorder1 == null || !isRecording) {
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
                    recorder1!!.startRecording()
                    recorder2!!.startRecording()
                    isRecording = true


                    //Start the recording threads.
                    //TODO: Clean this up by encapsulating the thread method. Fine tune the buffer size.
                    recordingThread1 = Thread(object : Runnable {
                        override fun run() {
                            val Data = ByteArray(pcmBufferSize)
                            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 1")
                            try {
                                FileOutputStream(
                                    context!!.getExternalFilesDir(null)
                                        .toString() + "/record1.1.pcm"
                                ).use { os ->
                                    var totalBytes: Long = 0
                                    while (isRecording) {
                                        val bytesRead =
                                            recorder1!!.read(Data, 0, Data.size)
                                        if (bytesRead > 0) {
                                            totalBytes += bytesRead.toLong()
                                            os.write(Data, 0, bytesRead)
                                        }
                                    }
                                    os.flush()
                                    Log.d(
                                        TAG,
                                        ">>>>>>>>>>> Total Bytes 1: $totalBytes"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "[recordingThread1] Error saving recording", e)
                            }
                        }
                    })
                    recordingThread2 = Thread(object : Runnable {
                        override fun run() {
                            val Data = ByteArray(pcmBufferSize)
                            var totalBytes: Long = 0
                            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 2")
                            try {
                                FileOutputStream(
                                    context!!.getExternalFilesDir(null)
                                        .toString() + "/record2.1.pcm"
                                ).use { os ->
                                    while (isRecording) {
                                        val bytesRead =
                                            recorder2!!.read(Data, 0, Data.size)
                                        if (bytesRead > 0) {
                                            totalBytes += bytesRead.toLong()
                                            os.write(Data, 0, bytesRead)
                                        }
                                    }
                                    Log.d(
                                        TAG,
                                        ">>>>>>>>>>> Total Bytes 2: $totalBytes"
                                    )
                                    os.flush()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "[recordingThread1] Error saving recording", e)
                            }
                        }
                    })
                    Log.d(TAG, "[buttonTestRecording] Starting the Recording")
                    recordingThread1!!.start()
                    recordingThread2!!.start()
                } else {
                    Log.d(TAG, "[buttonTestRecording] Stopping the Recording")
                    isRecording = false
                    recorder1!!.stop()
                    recorder1!!.release()
                    recorder2!!.stop()
                    recorder2!!.release()
                    recorder1 = null //Release for GC
                    recorder2 = null //Release for GC
                }
            }
        })
        binding!!.buttonFirst.setOnClickListener {
            NavHostFragment.findNavController(this@FirstFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        val TAG = FirstFragment::class.java.simpleName
        private const val SAMPLING_RATE_IN_HZ = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}