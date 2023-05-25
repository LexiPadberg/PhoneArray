package com.wavesciences.phonearray


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.wavesciences.phonearray.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addRecordingsBtn.setOnClickListener{
            val intent = Intent(applicationContext, AddRecordings::class.java)
            startActivity(intent)
        }
        binding.manageRecordingsBtn.setOnClickListener {
            val intent = Intent(applicationContext, ManageRecordings::class.java)
            //legIntent.putExtra("workout_type", manage.text.toString())
            startActivity(intent)
        }
        binding.settings.setOnClickListener {
            val intent = Intent(applicationContext, Settings::class.java)
            //legIntent.putExtra("workout_type", manage.text.toString())
            startActivity(intent)
        }
    }
}