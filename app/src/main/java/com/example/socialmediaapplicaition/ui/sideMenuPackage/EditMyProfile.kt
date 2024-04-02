package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.databinding.FragmentEditMyProfileBinding
import com.example.socialmediaapplicaition.models.User
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMyProfile : Fragment() {


    private var _binding :FragmentEditMyProfileBinding ?= null
    val binding get() = _binding!!
    var user: User?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditMyProfileBinding.inflate(layoutInflater,container,false)
        return binding.root

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setInitialState()
        bindingView()
        bindObserver()

    }

    private fun setInitialState() {

        val myUserProfile = arguments?.getString("userProfile")
        if(myUserProfile!= null){
            user = Gson().fromJson<User>(myUserProfile,User::class.java)
            user?.let {

                binding.apply {

                    Glide.with(userImage).load(user?.profile).into(userImage)
                    UserName.setText(user?.name)
                    status.setText(user?.status)
                    gmailText.text =user?.userEmail


                }
            }

        }

    }

    private fun bindObserver() {

    }

    private fun bindingView() {

    }

}