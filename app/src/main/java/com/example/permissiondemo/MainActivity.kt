package com.example.permissiondemo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    //! single permission le ker dene wala
    private val cameraResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted for Camera", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied for Camera", Toast.LENGTH_SHORT).show()
            }
        }

    //! multiple permission le ker dene wala
    private val multipleResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION) {
                        Toast.makeText(
                            this,
                            "Permission Granted for Fine Location",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else if(permissionName == Manifest.permission.ACCESS_COARSE_LOCATION) {
                        Toast.makeText(
                            this,
                            "Permission Granted for Coarse Location",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "Permission Granted for Camera",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    when (permissionName) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            Toast.makeText(this, "Permission Denied for Fine Location", Toast.LENGTH_SHORT)
                                .show()
                        }
                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            Toast.makeText(this, "Permission Denied for Coarse Location", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> {
                            Toast.makeText(this, "Permission Denied for Camera", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }


    private lateinit var btnPermission: Button
    private lateinit var btnPermissions: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPermission = findViewById(R.id.btnPermission)
        btnPermissions = findViewById(R.id.btnPermissions)

        btnPermission.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showRationaleDialogue(
                    "Permission Demo Requires Camera Access",
                    "Camera cannot be used because Camera access is denied"
                )
            } else {
                cameraResultLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnPermissions.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialogue(
                    "Permission Demo Requires Camera and Location Permission.",
                    "Open settings to allow Camera and Location Permission to the app."
                )
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showRationaleDialogue(
                    "Permission Demo Requires Location Access",
                    "Open Settings to allow Location Access to the app"
                )
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialogue(
                    "Camera Access Permission required.",
                    "Open the settings to allow Camera Access to the app."
                )
            } else {
                multipleResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                    )
                )
            }
        }
    }

    private fun showRationaleDialogue(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNegativeButton("Cancel") { dialogue, _ ->
            dialogue.dismiss()
        }
        builder.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.create().show()
    }
}