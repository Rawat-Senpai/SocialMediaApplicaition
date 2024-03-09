package com.example.socialmediaapplicaition.utils

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment



object Utils{

    const val CAMERA_PERMISSION_CODE = 100
    const val STORAGE_PERMISSION_CODE = 101
    fun pickImageFromGallery(activity: Activity, fragment: Fragment, onImagePicked: (Uri) -> Unit) {
        val launcher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    onImagePicked(it)
                }
            }
        }

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }


    fun checkPermission(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }


}



