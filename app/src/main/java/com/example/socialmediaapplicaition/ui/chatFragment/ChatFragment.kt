package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private var offsetX = 0
    private var offsetY = 0
    private var parentWidth = 0
    private var parentHeight = 0

    private var _binding :FragmentChatBinding ?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater,container,false)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            parentLayout.post {
                parentWidth =parentLayout.width
                parentHeight =parentLayout.height
            }
            button.setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        offsetX = event.rawX.toInt() - view.x.toInt()
                        offsetY = event.rawY.toInt() - view.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = view.x // Keep the current X position
                        val newY = event.rawY - offsetY

                        // Ensure the draggable view stays within the bounds of the parent layout
                        if (newY >= 0 && newY + view.height <= parentHeight) {
                            view.y = newY
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick() // Perform click action
                    }
                }
                true
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }



}