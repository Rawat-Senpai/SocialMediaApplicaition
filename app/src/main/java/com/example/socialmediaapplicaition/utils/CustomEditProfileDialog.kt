package com.example.socialmediaapplicaition.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.example.socialmediaapplicaition.R

class CustomEditProfileDialog (context: Context, private val onActionClicked:(String) -> Unit,
                               private val updateType:String , private val textValue :String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set dialog content
        setContentView(R.layout.layout_custom_dialog)
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
        val cancelBtn = findViewById<TextView>(R.id.cancleBtn)
        val saveBtn = findViewById<TextView>(R.id.saveBtn)

        val headingText = findViewById<TextView>(R.id.headingText)

        editText.setText(textValue)

        if(updateType == Constants.NAME){
            headingText.text = "Enter your name"
        }else if (updateType == Constants.STATUS){
            headingText.text = "Write something about yourself"
        }

        cancelBtn.setOnClickListener(){
            dismiss()
        }

        saveBtn.setOnClickListener(){
            onActionClicked(editText.text.toString())
            dismiss()
        }

        setCanceledOnTouchOutside(false)


    }
}