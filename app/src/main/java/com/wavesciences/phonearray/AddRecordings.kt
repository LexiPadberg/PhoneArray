package com.wavesciences.phonearray

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
<<<<<<< Updated upstream
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
=======
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioManager.AudioPlaybackCallback
import android.media.AudioPlaybackConfiguration
>>>>>>> Stashed changes
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.wavesciences.phonearray.databinding.ActivityAddRecordingsBinding
<<<<<<< Updated upstream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


=======
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

import java.io.FileOutputStream
import java.io.IOException
>>>>>>> Stashed changes


class AddRecordings : ComponentActivity(), Timer.onTimerTickListener {
    private var binding: ActivityAddRecordingsBinding? = null
    private var recorder1: AudioRecord? = null
    private var recorder2: AudioRecord? = null
    private var isRecording = false
    private lateinit var  recordName: String

    private var recordingThread1: Thread? = null
    private var recordingThread2: Thread? = null

<<<<<<< Updated upstream
    private lateinit var timer: Timer
    private lateinit var realTime : TextView


    private lateinit var adapter: AdapterRecyclerView


=======
    //private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var audioTrack: AudioTrack
>>>>>>> Stashed changes


    private val pcmBufferSize: Int
        get() {
            val pcmBufSize = AudioRecord.getMinBufferSize(
                SAMPLING_RATE_IN_HZ,
                CHANNEL_CONFIG,
                AudioFormat.ENCODING_PCM_16BIT
            ) + 8191
            return pcmBufSize - pcmBufSize % 8192
        }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

<<<<<<< Updated upstream
        timer = Timer(this)

        realTime = binding!!.timer


=======
        mediaPlayer1 = MediaPlayer()
>>>>>>> Stashed changes

        binding!!.startRecordingBtn.setOnClickListener {
            Toast.makeText(this, "starting recording", Toast.LENGTH_SHORT).show()
            startRecording()


        }
        binding!!.stopRecordingBtn.setOnClickListener{
            stopRecording()
            val recordName = binding?.recordingName?.text.toString()
            val folderName =recordName
            makeDirectory(recordName)


            val fileName1 = getExternalFilesDir(null).toString() + "/${recordName}_CH001.pcm"
            val fileName2 = getExternalFilesDir(null).toString() + "/${recordName}_CH002.pcm"
            var wavFile1 = getExternalFilesDir(null).toString() + "/${recordName}_CH001.wav"
            var wavFile2 = getExternalFilesDir(null).toString() + "/${recordName}_CH002.wav"

            val time:String = binding!!.timer.text.toString()


            val filePathName1 = File(fileName1)
            val filePathName2 = File(fileName2)
            val wavPathFile1 =  File(wavFile1)
            val wavPathFile2 = File(wavFile2)
            rawToWave(filePathName1, wavPathFile1)
            rawToWave(filePathName2, wavPathFile2)
            val directory = File(getExternalFilesDir(null), folderName)
            wavPathFile1.renameTo(File(directory,wavPathFile1.name))
            wavPathFile2.renameTo(File(directory,wavPathFile2.name))
            filePathName1.delete()
            filePathName2.delete()

            val intent = Intent(applicationContext,
                ManageRecordings::class.java)
            intent.putExtra("recpath1", wavPathFile1.absolutePath)
            intent.putExtra("recpath2", wavPathFile2.absolutePath)
            intent.putExtra("time",time)
            startActivity(intent)


            val toast = Toast.makeText(this, "$recordName saved to your recordings.", Toast.LENGTH_SHORT)
            toast.show()

        }


        binding!!.homeBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

<<<<<<< Updated upstream
    }
    @Throws(IOException::class)
    fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(rawFile))
            input.read(rawData)
        } finally {
            input?.close()
        }
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(waveFile))
            // WAVE header
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + rawData.size) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, 44100) // sample rate
            writeInt(output, SAMPLING_RATE_IN_HZ * 2) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawData.size) // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            val shorts = ShortArray(rawData.size / 2)
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()[shorts]
            val bytes = ByteBuffer.allocate(shorts.size * 2)
            for (s in shorts) {
                bytes.putShort(s)
            }
            output.write(fullyReadFileToBytes(rawFile))
        } finally {
            output?.close()
        }
    }

    @Throws(IOException::class)
    fun fullyReadFileToBytes(f: File): ByteArray {
        val size = f.length().toInt()
        val bytes = ByteArray(size)
        val tmpBuff = ByteArray(size)
        val fis = FileInputStream(f)
        try {
            var read = fis.read(bytes, 0, size)
            if (read < size) {
                var remain = size - read
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain)
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
                    remain -= read
                }
            }
        } catch (e: IOException) {
            throw e
        } finally {
            fis.close()
=======
        binding!!.buttonTestRecordings.setOnClickListener{

               val recordName = binding?.recordingName?.text.toString()
              // playAudio(getExternalFilesDir(null).toString() + "/" + recordName + "_record1.1.pcm")

>>>>>>> Stashed changes
        }
        return bytes
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }


    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }


    private fun writeString(output: DataOutputStream, value: String) {
        for (i in 0 until value.length) {
            output.write(value[i].code)
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
                        os.close()
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


        val fileName1 = getExternalFilesDir(null).toString() + "/"+ recordName +"_CH001.pcm"
        val fileName2 = getExternalFilesDir(null).toString() + "/" + recordName + "_CH002.pcm"
        recordingThread1 = threadRecordings("recording1", recorder1!!, fileName1)
        recordingThread2 = threadRecordings("recording2", recorder2!!, fileName2)

        recordingThread1!!.start()
        recordingThread2!!.start()


        timer.start()

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


        timer.stop()



    }
//    private fun playAudio(recordingFilePath: String?) {
//        try {
//            if (!recordingFilePath.isNullOrEmpty()) {
//                GlobalScope.launch(Dispatchers.IO) {
//                    val audioAttributes = AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .build()
//
//                    val audioFormat = AudioFormat.Builder()
//                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                        .setSampleRate(SAMPLING_RATE_IN_HZ)
//                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
//                        .build()
//
//                    val bufferSizeInBytes = AudioTrack.getMinBufferSize(
//                        SAMPLING_RATE_IN_HZ,
//                        AudioFormat.CHANNEL_OUT_MONO,
//                        AudioFormat.ENCODING_PCM_16BIT
//                    )
//
//                    audioTrack = AudioTrack(
//                        audioAttributes,
//                        audioFormat,
//                        bufferSizeInBytes,
//                        AudioTrack.MODE_STREAM,
//                        AudioManager.AUDIO_SESSION_ID_GENERATE
//                    )
//
//                    audioTrack.play()
//
//                    val file = File(recordingFilePath)
//                    val fileInputStream = FileInputStream(file)
//                    val buffer = ByteArray(bufferSizeInBytes)
//
//                    var bytesRead: Int
//                    while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
//                        withContext(Dispatchers.Main) {
//                            audioTrack.write(buffer, 0, bytesRead)
//                        }
//                    }
//
//                    fileInputStream.close()
//                }
//            } else {
//                Log.e(TAG, "Recording file path is null or empty")
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error playing recording", e)
//        }
    // }
//    private fun playAudio(recordingFilePath: String?){
//        //mediaPlayer1 = MediaPlayer()
//        try{
//
//            if(!recordingFilePath.isNullOrEmpty()) {
//                mediaPlayer1.reset()
//                mediaPlayer1.setDataSource(recordingFilePath)
//                mediaPlayer1.prepare()
//                mediaPlayer1.start()
//                Log.d(TAG, "playing recording")
//            }else {
//                Log.e(TAG, "Recording file path is null or empty")
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, " Error playing recording", e)
//        }
//    }



    companion object {
        val TAG = AddRecordings::class.java.simpleName
        const val SAMPLING_RATE_IN_HZ = 44100
<<<<<<< Updated upstream
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
=======
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
>>>>>>> Stashed changes
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
    fun calculateAmplitude(buffer: ShortArray, bufferSize: Int): Float {
        var maxAmplitude = 0f

        for (i in 0 until bufferSize) {
            val amplitude = Math.abs(buffer[i].toFloat())
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude
            }
        }

        return maxAmplitude
    }
    override fun onTimerTick(duration: String) {
        realTime.text = duration
    //    waveformView.addAmplitude(calculateAmplitude().toFloat())
    }
    private fun makeDirectory(folderName: String) {
        val folder = File(getExternalFilesDir(null), folderName)
        if (!folder.exists()) {
            val created = folder.mkdirs()
            if (!created) {
                Log.e(TAG, "Failed to create folder: $folderName")
                return
            }
        }

        Log.d(TAG, "New folder created: ${folder.absolutePath} ")
    }



}


