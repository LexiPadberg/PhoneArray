package com.wavesciences.phonearray

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager.AudioPlaybackCallback
import android.media.AudioPlaybackConfiguration
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

import androidx.activity.ComponentActivity

import com.wavesciences.phonearray.databinding.ActivityAddRecordingsBinding
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding

import java.io.FileOutputStream

class AddRecordings : ComponentActivity() {
    private var binding: ActivityAddRecordingsBinding? = null
    private var recorder1: AudioRecord? = null
    private var recorder2: AudioRecord? = null
    private var isRecording = false
    private lateinit var  recordName: String

    private var recordingThread1: Thread? = null
    private var recordingThread2: Thread? = null
//    private lateinit var WaveFormView : WaveFormView
   // private lateinit var spinner: Spinner
    private var extensionName: String =""
    private lateinit var fileName1: String
    private lateinit var fileName2: String


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

        val extensions = resources.getStringArray(R.array.extensions)
        val spinner = binding!!.spinner

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, extensions)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                    Toast.makeText(this@AddRecordings,
                        getString(R.string.selected_item) + " " +
                                "" + extensions[position], Toast.LENGTH_SHORT).show()
               extensionName= extensions[position]
                Log.d(TAG, "chosen extension, $extensionName")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //set to default extension .wav
                extensionName = "WAV"
            }
        }

        //Log.d(TAG, "chosen extension, $extensionName")



        binding!!.startRecordingBtn.setOnClickListener {
           Toast.makeText(this, "starting recording", Toast.LENGTH_SHORT).show()
           startRecording()
        }
        binding!!.stopRecordingBtn.setOnClickListener{
            stopRecording()
            val recordName = binding?.recordingName?.text.toString()
//            when (extensionName) {
//
//               "PCM" -> {
//                    var fileName1 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.pcm"
//                    var fileName2 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record2.1.pcm"
//                }
//
//                "MP3" -> {
//                    var fileName1 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.mp3"
//                    var fileName2 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record2.1.mp3"
//
//                }
//                else -> {
//                    var fileName1 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.wav"
//                    var fileName2 =
//                        getExternalFilesDir(null).toString() + "/" + recordName + "_record2.1.wav"
//                }
//            }
            var fileName1 = getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.wav"
            var fileName2 = getExternalFilesDir(null).toString() + "/" + recordName + "_record2.1.wav"
            val intent = Intent(applicationContext,
                ManageRecordings::class.java)
            intent.putExtra("recpath1", fileName1)
            intent.putExtra("recpath2", fileName2)
            intent.putExtra("extension",extensionName)
            startActivity(intent)


            val toast = Toast.makeText(this, "$recordName saved to your recordings.", Toast.LENGTH_SHORT)
            toast.show()

        }

        binding!!.buttonTestRecordings.setOnClickListener{
           try {
                var mp = MediaPlayer()
                mp.setDataSource(getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.wav")
                mp.start()
                Log.d(TAG, "playing recording")
            }
           catch(e: Exception) {
                Log.e(TAG, " Error playing recording", e)
            }
        }

    }

    private fun threadRecordings(recordingName: String, recorder: AudioRecord?, fileName: String): Thread {
        return Thread {
            val data = ByteArray(pcmBufferSize)
            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD: $recordingName")
            try {
                recorder?.let { validRecorder ->
                    FileOutputStream(fileName).use { os ->
                        var totalBytes: Long = 0
                        while (isRecording) {
                            val bytesRead = validRecorder.read(data, 0, data.size)
                            if (bytesRead > 0) {
                                totalBytes += bytesRead.toLong()
                                os.write(data, 0, bytesRead)
                            }
                        }
                        os.flush()
                        Log.d(TAG, ">>>>>>>>>>> Total Bytes for $recordingName: $totalBytes")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "[$recordingName] Error saving recording", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val recordName = binding?.recordingName?.text.toString()

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
        val dir = "${externalCacheDir?.absolutePath}/"



        val fileName1 = getExternalFilesDir(null).toString() + "/"+ recordName +"_record1.1.wav"
        val fileName2 = getExternalFilesDir(null).toString() + "/" + recordName + "_record2.1.wav"


        recordingThread1 = threadRecordings("recording1", recorder1!!, fileName1)
        recordingThread2 = threadRecordings("recording2", recorder2!!, fileName2)

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
        const val SAMPLING_RATE_IN_HZ = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}


