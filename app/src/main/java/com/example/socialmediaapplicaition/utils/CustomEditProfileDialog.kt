package com.example.socialmediaapplicaition.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import com.example.socialmediaapplicaition.R

class CustomEditProfileDialog (context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set dialog content
        setContentView(R.layout.custom_dialog_layout)

        // Set dialog window attributes
        val windowParams = window?.attributes
        windowParams?.gravity = Gravity.BOTTOM // Display the dialog at the bottom
        windowParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        windowParams?.height = WindowManager.LayoutParams.WRAP_CONTENT

        // Adjust the window to be above the keyboard
        windowParams?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

        window?.attributes = windowParams
        val editText = findViewById<EditText>(R.id.editDataDetails)
        editText.requestFocus()


    }
}