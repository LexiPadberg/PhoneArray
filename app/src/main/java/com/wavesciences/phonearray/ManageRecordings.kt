package com.wavesciences.phonearray

import android.R.id.shareText
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream


class ManageRecordings: ComponentActivity() {
    private lateinit var binding: ActivityManageRecordingsBinding
   // private lateinit var mediaPlayer1: MediaPlayer
  //  private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var recordingListAdapter: AdapterRecyclerView
    private var recordingFilePaths: List<String> = emptyList()
    private lateinit var audioTrack: AudioTrack

    private val pcmBufferSize: Int
        get() {
            val pcmBufSize = AudioRecord.getMinBufferSize(
                AddRecordings.SAMPLING_RATE_IN_HZ,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            ) + 8191
            return pcmBufSize - pcmBufSize % 8192
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_recordings)
        binding = ActivityManageRecordingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rec1 = intent.getStringExtra("recpath1")
        val rec2 = intent.getStringExtra("recpath2")
        Log.d(TAG, "rec1: $rec1")
        Log.d(TAG, "rec2: $rec2")

        //mediaPlayer1 = MediaPlayer()
        //mediaPlayer2 = MediaPlayer()
        recordingFilePaths = listOfNotNull(rec1,rec2)

        //Log.d(TAG, "recordingFilePaths: $recordingFilePaths")

        println(recordingFilePaths.joinToString())
        recordingListAdapter = AdapterRecyclerView(recordingFilePaths)
        binding.recordingsHolder.adapter = recordingListAdapter
        binding.recordingsHolder.layoutManager = LinearLayoutManager(this@ManageRecordings)


        binding.shareBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            if (selectedRecording != null) {
                shareRecording(selectedRecording)
            }
            // Share the selectedRecording from recyclerView
        }

        binding.playRecordingBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            if (selectedRecording != null) {
                playRecording(selectedRecording)
            }
            // Play selectedRecording from recyclerView
        }

        binding.deleteRecordingBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            if (selectedRecording != null) {
                deleteRecording(selectedRecording)
            }
            // Delete selectedRecording from recyclerView
        }

        binding.homeBtn.setOnClickListener {
            finish()
        }

    }
    private fun playRecording(recordingFilePath: String){
        try {
            if (!recordingFilePath.isEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()

                    val audioFormat = AudioFormat.Builder()
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(SAMPLING_RATE_IN_HZ)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()


                    audioTrack = AudioTrack(
                        audioAttributes,
                        audioFormat,
                        pcmBufferSize,
                        AudioTrack.MODE_STREAM,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                    )

                    audioTrack.play()

                    val file = File(recordingFilePath)
                    val fileInputStream = FileInputStream(file)
                    val buffer = ByteArray(pcmBufferSize)

                    var bytesRead: Int
                    while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                        withContext(Dispatchers.Main) {
                            audioTrack.write(buffer, 0, bytesRead)
                        }
                    }

                    fileInputStream.close()
                }
            } else {
                Log.e(TAG, "Recording file path is null or empty")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing recording", e)
        }
    }

    private fun shareRecording(recordingFilePath: String) {

        //TODO: actually implement something to share, currently just has dummy text

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    private fun deleteRecording(recordingFilePath: String){
        //TODO: figure out how to delete the selected recording
        val file = File(recordingFilePath)
        val deleted = file.delete()
        if (deleted) {
            recordingListAdapter.recordingFilePaths = recordingListAdapter.recordingFilePaths.filterNot { it == recordingFilePath }
            recordingListAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val TAG = "ManageRecordings"
        const val SAMPLING_RATE_IN_HZ = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}

