package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentSavedPostBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SavedPostFragment : Fragment() {

    @Inject
    lateinit var tokenManager:TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()
    private var _binding:FragmentSavedPostBinding ?= null
    private lateinit var adapter: UsersPostAdapter
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavedPostBinding.inflate(layoutInflater,container,false)
        return binding.root

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UsersPostAdapter(::onPostClicked)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter

        bindObservers()
        setInitialState()
    }

    private fun setInitialState(){
        val myProfileId = arguments?.getString("myUserId")

        if(myProfileId != null){
            viewModel.getUserSavedPost(myProfileId)
        }


    }


    private fun bindObservers() {


        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.userSavedPost.collect{it->

                    when(it){
                        is NetworkResult.Error -> {
                            Log.d("checkingData",it.message.toString())
                        }

                        is NetworkResult.Loading -> {

                        }

                        is NetworkResult.Success -> {
                         Log.d("savedPost_activity",it.data.toString())
                            Log.d("savedPost_activity",it.data?.size.toString())
                            adapter.submitList(it.data)
                        }

                        null -> {}
                    }

                }
            }




        }



    }
    private fun onPostClicked(postResponse: Post){
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
        findNavController().navigate(R.id.action_savedPostFragment_to_postDetailsFragment, bundle)
    }


}