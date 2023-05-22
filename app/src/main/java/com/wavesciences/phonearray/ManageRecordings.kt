package com.wavesciences.phonearray

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.wavesciences.phonearray.databinding.ActivityManageRecordingsBinding


class ManageRecordings: ComponentActivity() {
    private lateinit var binding: ActivityManageRecordingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_recordings)
        binding = ActivityManageRecordingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBtn.setOnClickListener {
            finish()
        }

    }
}

