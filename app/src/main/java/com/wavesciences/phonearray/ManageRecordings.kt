package com.wavesciences.phonearray

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding

import java.io.File
import java.io.FileInputStream


class ManageRecordings: ComponentActivity() {
    private lateinit var binding: ActivityManageRecordingsBinding
    private lateinit var mediaPlayer1: MediaPlayer
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

        mediaPlayer1 = MediaPlayer()
        //mediaPlayer2 = MediaPlayer()
        recordingFilePaths = listOfNotNull(rec1,rec2)

        //Log.d(TAG, "recordingFilePaths: $recordingFilePaths")

        println(recordingFilePaths.joinToString())
        recordingListAdapter = AdapterRecyclerView(recordingFilePaths)
        binding.recordingsHolder.adapter = recordingListAdapter
        binding.recordingsHolder.layoutManager = LinearLayoutManager(this@ManageRecordings)


        binding.shareBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            //get the recording selected by the user
            if (selectedRecording != null) {
                shareRecording(selectedRecording)
            }
            // Share the selectedRecording from recyclerView
        }

        binding.playRecordingBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            //get the recording selected by the user
            if (selectedRecording != null) {
                playRecording(selectedRecording)
            }
            // Play selectedRecording from recyclerView
        }

        binding.deleteRecordingBtn.setOnClickListener {
            val selectedRecording = recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            //get the recording selected by the user
            if (selectedRecording != null) {
                deleteRecording(selectedRecording)
            }
            // Delete selectedRecording from recyclerView
        }

        binding.homeBtn.setOnClickListener {
            finish()
        }

    }
    private fun playRecording(recordingFilePath: String?) {
        try {
            if (!recordingFilePath.isNullOrEmpty()) {
                mediaPlayer1.reset()
                mediaPlayer1.setDataSource(recordingFilePath)
                mediaPlayer1.prepare()
                mediaPlayer1.start()
                Log.d(TAG, "playing recording")
            } else {
                Log.e(TAG, "Recording file path is null or empty")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing recording", e)
        }
    }
    private fun shareRecording(recordingFilePath: String) {
        val file = File(recordingFilePath)
        val uri = FileProvider.getUriForFile(this, "com.wavesciences.phonearray.fileprovider", file)

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "audio/wav"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun deleteRecording(recordingFilePath: String){
        val file = File(recordingFilePath)
        val deleted = file.delete()
        if (deleted) {
            recordingListAdapter.recordingFilePaths = recordingListAdapter.recordingFilePaths.filterNot { it == recordingFilePath }
            recordingListAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val TAG = "ManageRecordings"
        const val SAMPLING_RATE_IN_HZ = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}

