package com.wavesciences.phonearray


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wavesciences.phonearray.databinding.ItemRowsBinding
import java.io.File



class AdapterRecyclerView(var recordingFilePaths: List<String>)
    : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    private lateinit var binding: ItemRowsBinding
    var selectedPosition = RecyclerView.NO_POSITION
   // lateinit var title: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRowsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

       // title = binding.recordingNameRecycler.toString()
    }

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

            binding.recordingNameRecycler.isChecked = selectedPosition == position
            binding.recordingNameRecycler.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }


    }
    fun getFileNameFromPath(filePath: String): String {
        val file = File(filePath)
        return file.name
    }
}