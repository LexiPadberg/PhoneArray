package com.wavesciences.phonearray

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.wavesciences.phonearray.databinding.LoadingPageBinding

class LoadingPage  : ComponentActivity() {
    private lateinit var binding: LoadingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueToAppBtn.setOnClickListener {
            checkPermissions()
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkPermissions()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_denied_title)
                    .setMessage(R.string.permission_denied_msg)
                    .setNegativeButton(R.string.ok)
                    { dialog, _ -> dialog.cancel()
                    }
                    .show()
            }
        }

    private fun checkPermissions() {
        val recordAudioPermission = Manifest.permission.RECORD_AUDIO
        val writeExternalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val recordAudioPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            recordAudioPermission
        ) == PackageManager.PERMISSION_GRANTED

        val writeExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            writeExternalStoragePermission
        ) == PackageManager.PERMISSION_GRANTED

        if (recordAudioPermissionGranted && writeExternalStoragePermissionGranted) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else {
            val permissionsToRequest = mutableListOf<String>()

            if (!recordAudioPermissionGranted) {
                permissionsToRequest.add(recordAudioPermission)
            }

            if (!writeExternalStoragePermissionGranted) {
                permissionsToRequest.add(writeExternalStoragePermission)
            }

            if (permissionsToRequest.isNotEmpty()) {
                permissionsToRequest.forEach { permission ->
                    requestPermissionLauncher.launch(permission)
                }
            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
