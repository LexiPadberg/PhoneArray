package com.wavesciences.phonearray

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding
import java.io.IOException


class ManageRecordings: ComponentActivity() {
    private lateinit var binding: ActivityManageRecordingsBinding
    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var recordingListAdapter: AdapterRecyclerView
    private var recordingFilePaths: List<String> = emptyList()


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
        mediaPlayer2 = MediaPlayer()
        recordingFilePaths = listOfNotNull(rec1,rec2)

        //Log.d(TAG, "recordingFilePaths: $recordingFilePaths")

        println(recordingFilePaths.joinToString())
        recordingListAdapter = AdapterRecyclerView(recordingFilePaths)
        binding.recordingsHolder.adapter = recordingListAdapter
        binding.recordingsHolder.layoutManager = LinearLayoutManager(this@ManageRecordings)



//        try {
//            mediaPlayer1.setDataSource(rec1)
//            mediaPlayer2.setDataSource(rec2)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        // Prepare media players
//        mediaPlayer1.prepareAsync()
//        mediaPlayer2.prepareAsync()
//
//
//
//        mediaPlayer1.setOnPreparedListener {
//            mediaPlayer1.start()
//        }
//        mediaPlayer2.setOnPreparedListener {
//            mediaPlayer2.start()
//        }
//

        binding.homeBtn.setOnClickListener {
            finish()
        }

    }
    companion object {
        private const val TAG = "ManageRecordings"
    }
}

