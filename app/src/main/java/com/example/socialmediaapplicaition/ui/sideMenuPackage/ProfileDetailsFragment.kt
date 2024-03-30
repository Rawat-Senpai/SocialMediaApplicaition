package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.health.connect.datatypes.AppInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R

import com.example.socialmediaapplicaition.databinding.FragmentProfileDetailsBinding
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
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

        binding.apply {
            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {

                }

                override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                    Glide.with(personImageSquare).load(tokenManager.getProfile()).placeholder(R.drawable.ic_default_person).circleCrop().into(personImageSquare)
                }

                override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {

                    if(currentId == R.id.start){
                        Glide.with(personImageSquare).load(tokenManager.getProfile()).placeholder(R.drawable.ic_default_person).into(personImageSquare)
                    }else if (currentId == R.id.end){
                        Glide.with(personImageSquare).load(tokenManager.getProfile()).placeholder(R.drawable.ic_default_person).circleCrop().into(personImageSquare)
                    }


                }

                override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
            })


        }

//        Glide.with(binding.personImageSquare).load(tokenManager.getProfile().toString()).into(binding.personImageSquare)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}