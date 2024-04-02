package com.example.socialmediaapplicaition

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmediaapplicaition.databinding.FragmentEditMyProfileBinding

class EditMyProfile : Fragment() {


    private var _binding :FragmentEditMyProfileBinding ?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentEditMyProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_edit_my_profile, container, false)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}