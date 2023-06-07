package com.wavesciences.phonearray

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.RecyclerView
import com.wavesciences.phonearray.databinding.ItemRowsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


class AdapterRecyclerView(var recordingFilePaths: List<String>)
    : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    private lateinit var binding: ItemRowsBinding
    var selectedPosition = RecyclerView.NO_POSITION

  // private var duration: String = ""
   private var durationMap: MutableMap<String, String> = mutableMapOf()


    fun updateDuration(filePath: String, newDuration: String) {
        durationMap[filePath] = newDuration
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRowsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

       // title = binding.recordingNameRecycler.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordingFilePath = recordingFilePaths[position]
        holder.bind(recordingFilePath)
        //Log.d("AdapterRecyclerView", "Position: $position, File path: $recordingFilePath")

    }

    override fun getItemCount() = recordingFilePaths.size

    fun setFilteredList(recordingFilePaths:  List<String>){
        this.recordingFilePaths = recordingFilePaths
        notifyDataSetChanged()
    }
    inner class ViewHolder(private val binding: ItemRowsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recordingFilePath: String) {
            val fileName = getFileNameFromPath(recordingFilePath)
            binding.recordingNameRecycler.text = fileName

            //display each files time and date created
            val file = File(recordingFilePath)
            val creationDate = Date(file.lastModified())
            val dateFormat = SimpleDateFormat("MM-dd-yyyy, hh:mm a", Locale.getDefault())
            val creationDateStr = dateFormat.format(creationDate)
            binding.time.text = creationDateStr

           val duration = durationMap[recordingFilePath]
            binding.duration.text = duration




            binding.recordingNameRecycler.isChecked = selectedPosition == position
            binding.recordingNameRecycler.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }

        }
        private fun formatDuration(duration: Int): String {
            val totalSeconds = duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

    }
    fun getFileNameFromPath(filePath: String): String {
        val file = File(filePath)
        return file.name
    }

//    fun updateDuration(newDuration: String) {
//        duration = newDuration
//        notifyDataSetChanged()
//    }

}