package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentSavedPostBinding
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedPostFragment : Fragment() {

    @Inject
    lateinit var tokenManager:TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()
    private var _binding:FragmentSavedPostBinding ?= null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavedPostBinding.inflate(layoutInflater,container,false)

        return binding.root
//        return inflater.inflate(R.layout.fragment_saved_post, container, false)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        setInitialState()
    }

    private fun setInitialState(){
        val myProfileId = arguments?.getString("myUserId")

        if(myProfileId != null){

//            viewModel.getALl

        }


    }


    private fun bindObservers() {

    }


}