package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentContactBinding
import com.example.socialmediaapplicaition.databinding.FragmentProfileDetailsBinding
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import javax.inject.Inject

class ProfileDetailsFragment : Fragment() {



    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()

    private  var _binding: FragmentProfileDetailsBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentProfileDetailsBinding.inflate(layoutInflater,container,false)

        return binding.root

//        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingView()
        bindObserver()
    }

    private fun bindObserver() {

    }

    private fun bindingView() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}