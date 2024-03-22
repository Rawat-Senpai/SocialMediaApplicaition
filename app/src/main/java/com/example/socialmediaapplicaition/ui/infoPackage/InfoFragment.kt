package com.example.socialmediaapplicaition.ui.infoPackage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {

    private var _binding:FragmentInfoBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding= FragmentInfoBinding.inflate(layoutInflater,container,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Glide.with(requireContext())
//            .load(R.drawable.animation_gif)
//            .into(binding.progressBar);

        val x =10;
        val y =20



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}