package com.wavesciences.phonearray

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
//import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.Date
import java.util.Locale


class ManageRecordings : ComponentActivity(),  AdapterRecyclerView.OnItemClickListener {
    private lateinit var binding: ActivityManageRecordingsBinding

    //private lateinit var mediaPlayer1: MediaPlayer
    //  private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var recordingListAdapter: AdapterRecyclerView
    private var recordingFilePaths = mutableListOf<String>()
    private lateinit var audioTrack: AudioTrack
    private lateinit var searchView: SearchView
    private lateinit var path: File

    //private val recordingsList = mutableListOf<File>()
    private var isRecordingPaused = false

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
        //recordingFilePaths = listOfNotNull(rec1, rec2) as MutableList<String>

        //Log.d(TAG, "recordingFilePaths: $recordingFilePaths")

        val duration = intent.getStringExtra("time")

        recordingListAdapter = AdapterRecyclerView(recordingFilePaths, this)
        binding.recordingsHolder.adapter = recordingListAdapter
        binding.recordingsHolder.layoutManager = LinearLayoutManager(this@ManageRecordings)
        binding.backButton.visibility = View.INVISIBLE
        binding.shareBtn.visibility = View.INVISIBLE
        binding.playRecordingBtn.visibility = View.INVISIBLE
        binding.deleteRecordingBtn.visibility = View.INVISIBLE
        binding.pauseRecordingBtn.visibility = View.INVISIBLE

//        if (duration != null) {
//            if (rec1 != null) {
//                recordingListAdapter.updateDuration(rec1, duration)
//            }
//        }
//        if (duration != null) {
//            if (rec2 != null) {
//                recordingListAdapter.updateDuration(rec2, duration)
//            }
//        }

        searchView = binding.searchBar

        //add files from directory

        val directoryPath = "/storage/emulated/0/Android/data/com.wavesciences.phonearray/files"
        path = File(directoryPath)


        File(directoryPath).walkTopDown().forEach {
            if (it.isDirectory) {
                recordingFilePaths.add(it.absolutePath)
                val creationDate = Date(it.lastModified())
               // binding.playRecordingBtn.isEnabled = false
            }
        }


        recordingListAdapter.notifyDataSetChanged()

        recordingListAdapter.setOnItemClickListener { position ->
            // Get the selected recording file path
            val selectedRecordingFilePath = recordingListAdapter.recordingFilePaths[position]

            // Call the modified loadRecordingFiles function with the selected recording file path
            loadRecordingFiles(selectedRecordingFilePath)


        }




        binding.shareBtn.setOnClickListener {
            val selectedRecording =
                recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)

            Log.d(TAG, "selected ; $selectedRecording")
            if (selectedRecording != null) {
                shareRecording(selectedRecording)
            } else {
                Toast.makeText(this, "select a recording to share", Toast.LENGTH_SHORT).show()
            }
            // Share the selectedRecording from recyclerView
        }

        binding.playRecordingBtn.setOnClickListener{
            val selectedRecording =
                recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)

           Log.d(TAG, "selected ; $selectedRecording")
            if (selectedRecording != null) {
                playRecording(selectedRecording)
            } else {
                Toast.makeText(this, "select a recording to play", Toast.LENGTH_SHORT).show()

            }
            // Play selectedRecording from recyclerView
        }

//        binding.pauseRecordingBtn.setOnClickListener {
//            val selectedRecording =
//                recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
//            if (selectedRecording != null) {
//                pauseRecording()
//            } else {
//                Toast.makeText(this, "play a recording in order to pause", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
        binding.backButton.setOnClickListener {
            getFileDirectory()
            binding.backButton.visibility = View.INVISIBLE
            binding.shareBtn.visibility = View.INVISIBLE
            binding.playRecordingBtn.visibility = View.INVISIBLE
            binding.deleteRecordingBtn.visibility = View.INVISIBLE
            binding.pauseRecordingBtn.visibility = View.INVISIBLE
        }
        
        binding.deleteRecordingBtn.setOnClickListener {
            val selectedRecording =
                recordingListAdapter.recordingFilePaths.getOrNull(recordingListAdapter.selectedPosition)
            if (selectedRecording != null) {
                deleteRecording(selectedRecording)
            } else {
                Toast.makeText(this, "select a recording to delete", Toast.LENGTH_SHORT).show()
            }
            // Delete selectedRecording from recyclerView
        }

        binding.homeBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }


        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

    }


    private fun pauseRecording() {
        isRecordingPaused = !isRecordingPaused

        if (isRecordingPaused) {
            audioTrack.pause()
            Toast.makeText(this, "Recording paused", Toast.LENGTH_SHORT).show()
        } else {
            audioTrack.play()
            Toast.makeText(this, "Recording resumed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<String>()
            for (i in recordingFilePaths) {
                if (getFileNameFromPath(i).lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No recordings found", Toast.LENGTH_LONG).show()

            } else {
                recordingListAdapter.setFilteredList(filteredList)
            }
        }
    }

    private fun getFileNameFromPath(filePath: String): String {
        val file = File(filePath)
        return file.name
    }


    private fun playRecording(recordingFilePath: String) {
        try {
            if (recordingFilePath.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
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


                    val file = File(recordingFilePath)
                    val fileInputStream = FileInputStream(file)
                    val buffer = ByteArray(pcmBufferSize)

                    audioTrack.play()
                    Log.d(TAG, "playing recording $recordingFilePath")

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
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val file = File(recordingFilePath)
        Log.d(TAG, "file path  = $recordingFilePath")
        //val uri = FileProvider.getUriForFile(this, "com.wavesciences.phonearray.fileprovider", file)


        if (file.exists()) {
            intentShareFile.type = "audio/*"
            val uri =
                FileProvider.getUriForFile(this, "com.wavesciences.phonearray.fileprovider", file)
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File")
            intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intentShareFile)
        } else {
            Toast.makeText(this, "Failed to share recording", Toast.LENGTH_SHORT).show()
        }

    }


    private fun deleteRecording(recordingFilePath: String) {
        val file = File(recordingFilePath)
        val deleted = file.delete()
        if (deleted) {
            recordingListAdapter.recordingFilePaths =
                recordingListAdapter.recordingFilePaths.filterNot { it == recordingFilePath }
            recordingListAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show()
        }

    }

    fun loadRecordingFiles(directoryPath: String) {
        binding.backButton.visibility = View.VISIBLE
        binding.shareBtn.visibility = View.VISIBLE
        binding.playRecordingBtn.visibility = View.VISIBLE
        binding.deleteRecordingBtn.visibility = View.VISIBLE
        binding.pauseRecordingBtn.visibility = View.VISIBLE
        //val directoryPath = "/storage/emulated/0/Android/data/com.wavesciences.phonearray/files/3"
        val directory = File(directoryPath)
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            recordingFilePaths.clear()

            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        recordingFilePaths.add(file.path)
                    }
                }
            }

            recordingListAdapter.notifyDataSetChanged()
        }
    }

    fun getFileDirectory(){
        val directoryPath = "/storage/emulated/0/Android/data/com.wavesciences.phonearray/files"
        val directory = File(directoryPath)
        if (directory.exists() && directory.isDirectory) {
          val files = directory.listFiles()
            recordingFilePaths.clear()

            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        recordingFilePaths.add(file.path)
                    }
                }
            }

            recordingListAdapter.notifyDataSetChanged()
        }
    }






    companion object {
        private const val TAG = "ManageRecordings"
        const val SAMPLING_RATE_IN_HZ = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    override fun onItemClick(recordingFilePath: String) {
        Log.d(TAG, "Clicked on file: $recordingFilePath")

        // Example: Open the file using an intent
        val file = File(recordingFilePath)
        val uri = FileProvider.getUriForFile(this, "com.example.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "audio/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    override fun onDirectoryClick(directoryPath: String) {
        loadRecordingFiles(directoryPath)
    }
}

