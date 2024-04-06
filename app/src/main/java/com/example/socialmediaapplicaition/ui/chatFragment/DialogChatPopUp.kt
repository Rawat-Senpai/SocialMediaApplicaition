package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.DialogChatPopupBinding
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.utils.Constants

class DialogChatPopUp(
    private val currentChat: ChatMessageModel,
    private val onChatActionClicked: (ChatMessageModel, String) -> Unit,
) : DialogFragment() {

    private var _binding: DialogChatPopupBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChatPopupBinding.inflate(inflater, container, false)

        binding.apply {

            thumbsUp.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_THUMBS_UP
                )
                dismiss()
            }
            thumbsDown.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_THUMBS_DOWN
                )
                dismiss()
            }
            laughing.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_LAUGHING
                )
                dismiss()
            }
            surprised.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_SURPRISED
                )
                dismiss()
            }
            sed.setOnClickListener { onChatActionClicked(currentChat, Constants.ACTION_SED)
                dismiss()
            }

            replyOnChat.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_REPLY
                )
                dismiss()
            }
            delteChat.setOnClickListener {
                onChatActionClicked(
                    currentChat,
                    Constants.ACTION_DELETE
                )
                dismiss()
            }

        }
        return binding.root

    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.75).toInt() // 3/4 of screen width
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(R.color.white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}