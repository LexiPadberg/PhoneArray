package com.wavesciences.phonearray

import android.content.res.ColorStateList
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< Updated upstream
import android.widget.Toast
import androidx.core.content.ContextCompat
=======
import android.widget.RadioButton
>>>>>>> Stashed changes
import androidx.recyclerview.widget.RecyclerView
import com.wavesciences.phonearray.AddRecordings.Companion.TAG
import com.wavesciences.phonearray.databinding.ItemRowsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class AdapterRecyclerView(var recordingFilePaths: List<String>, private val manageRecordings: ManageRecordings) :
    RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    //private lateinit var manageRecordings: ManageRecordings
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(recordingFilePath: String)
        fun onDirectoryClick(directoryPath: String)
    }

<<<<<<< Updated upstream


=======
class AdapterRecyclerView( val recordingFilePaths: List<String>)
    : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>(){

    private lateinit var binding: ItemRowsBinding
    var checkedRadioButtonId = RecyclerView.NO_POSITION
>>>>>>> Stashed changes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordingFilePath = recordingFilePaths[position]
        holder.bind(recordingFilePath)



        val context = holder.itemView.context


        holder.itemView.setOnClickListener {
            val file = File(recordingFilePath)
            if (file.isDirectory) {
                Log.d(TAG, "Clicked on directory: ${file.absolutePath}")
                manageRecordings.loadRecordingFiles(file.absolutePath)
            } else {
                Log.d(TAG, "Clicked on file: ${file.absolutePath}")
            }

        }

    }

    override fun getItemCount() = recordingFilePaths.size

    fun setFilteredList(recordingFilePaths: List<String>) {
        this.recordingFilePaths = recordingFilePaths
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRowsBinding) :
        RecyclerView.ViewHolder(binding.root) {

<<<<<<< Updated upstream
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(position)
                }
            }
        }

        fun bind(recordingFilePath: String) {
            val fileName = getFileNameFromPath(recordingFilePath)
            binding.recordingNameRecycler.text = fileName
=======
        private val radioButtonName: RadioButton = binding.recordingNameRecycler
        fun bind(recordingFilePath: String) {
            val fileName = getFileNameFromPath(recordingFilePath)
            radioButtonName.text = fileName
            radioButtonName.isChecked = checkedRadioButtonId ==position

            //notify Recycler view that the data has changed
        }
>>>>>>> Stashed changes

            val file = File(recordingFilePath)



            if (file.isDirectory){
                binding.iconView.setImageResource(R.drawable.folder)
                binding.durationTextView.visibility = View.INVISIBLE
                binding.duration.visibility = View.INVISIBLE


            }else{
                binding.iconView.setImageResource(R.drawable.none)
                val creationDate = Date(file.lastModified())
                val dateFormat = SimpleDateFormat("MM-dd-yyyy, hh:mm a", Locale.getDefault())
                val creationDateStr = dateFormat.format(creationDate)
                binding.time.text = creationDateStr
                val duration = calculateWavDuration(file)
                binding.duration.text = duration
                binding.durationTextView.visibility = View.VISIBLE
                binding.duration.visibility = View.VISIBLE
            }

//            val creationDate = Date(file.lastModified())
//            val dateFormat = SimpleDateFormat("MM-dd-yyyy, hh:mm a", Locale.getDefault())
//            val creationDateStr = dateFormat.format(creationDate)
//            binding.time.text = creationDateStr

            // Handle folder and file clicks separately



            binding.recordingNameRecycler.isChecked = selectedPosition == position
            binding.recordingNameRecycler.isChecked = selectedPosition == position
            binding.recordingNameRecycler.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    var selectedPosition = RecyclerView.NO_POSITION

    private fun getFileNameFromPath(filePath: String): String {
        val file = File(filePath)
        return file.name
    }
    private fun calculateWavDuration(file: File): String {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(file.absolutePath)
        val durationString = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationInMillis = durationString?.toLongOrNull() ?: 0

        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % TimeUnit.MINUTES.toSeconds(1)
        val milliseconds = durationInMillis % 1000

        val durationFormatted = String.format(
            "%02d:%02d:%02d",
            minutes,
            seconds,
            milliseconds
        )
        return durationFormatted
    }

}

