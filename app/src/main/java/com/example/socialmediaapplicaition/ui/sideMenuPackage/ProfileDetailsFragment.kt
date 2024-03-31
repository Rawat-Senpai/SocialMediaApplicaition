package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R

import com.example.socialmediaapplicaition.databinding.FragmentProfileDetailsBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailsFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()
    private  var _binding: FragmentProfileDetailsBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapter: UsersPostAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UsersPostAdapter(::onPostClicked)
        binding.postRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.postRecyclerView.adapter = adapter

        setInitialState()
        bindingView()
        bindObserver()
    }

    private fun setInitialState() {

        val myProfileId = arguments?.getString("myProfileId")
        val otherUserId = arguments?.getString("otherProfileId")

        if(myProfileId != null){
            binding.apply {
                Log.d("checkingUserId",myProfileId)
                personName.text = tokenManager.getUserName()
                userStatus.text = tokenManager.getStatus()
                Glide.with(personImageSquare).load(tokenManager.getProfile()).placeholder(R.drawable.ic_default_person).into(personImageSquare)
                onlineStatus.text = "Online"
                viewModel.filterPostUsingId(myProfileId)
            }


        }else if (otherUserId != null){
            binding.apply {
                Log.d("checkingUserId",otherUserId)
                viewModel.getUserProfileData(otherUserId)
                viewModel.filterPostUsingId(otherUserId)
            }
        }
    }

    private fun onPostClicked(postResponse: Post){
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
        findNavController().navigate(R.id.action_mainFragment_to_postDetailsFragment, bundle)
    }


    private fun bindObserver() {


        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.userProfileData.collect{it->
                    binding.progressBar.isVisible = it is NetworkResult.Loading
                    when(it){
                       is NetworkResult.Error -> {

                       }

                       is NetworkResult.Loading -> {
                           binding.progressBar.isVisible = true
                       }

                       is NetworkResult.Success -> {
                           binding.apply {

                               personName.text = it.data?.name
                               userStatus.text = it.data?.status
                               onlineStatus.text = it.data?.status
                               Glide.with(personImageSquare).load(it.data?.profile).placeholder(R.drawable.ic_default_person).into(personImageSquare)

                           }

                       }

                       null -> {}
                   }

                }
            }


            launch {
                viewModel.userSpecificPost.collect { it ->
                    Log.d("checkingShobhitprofile", it?.data.toString())
                    when (it) {
                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                        is NetworkResult.Success -> {
                            Log.d("checkingDataPost", it.data.toString())
                            adapter.submitList(it.data)
                        }
                        is NetworkResult.Error -> {
                            Log.d("error", it.message.toString())
                            Log.d("checkingShobhitprofile", it?.message.toString())
                        }
                        else -> {}
                    }
                }
            }


        }

    }

    private fun bindingView() {

        binding.apply {
            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {}

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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}