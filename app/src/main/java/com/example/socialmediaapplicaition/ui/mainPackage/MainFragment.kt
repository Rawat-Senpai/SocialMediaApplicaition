package com.example.socialmediaapplicaition.ui.mainPackage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentMainBinding
import com.example.socialmediaapplicaition.ui.auth.AuthViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlin.math.log10


@AndroidEntryPoint
class MainFragment : Fragment() {


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<AuthViewModel>()

    private val postViewModel by viewModels<MainViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_main, container, false)

        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObserver()

        bindViews()




        binding.apply {
            menuImg.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }



            Log.d("response", viewModel.currentUser?.displayName.toString())
            Log.d("response", viewModel.currentUser?.uid.toString())

        }

    }

    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.allPosts.collect {
                binding.progressBar.isVisible = it is NetworkResult.Loading
                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is NetworkResult.Success -> {
                        Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                        Log.d("checkingPostResponse",it.data.toString())
                    }

                    else -> {

                    }
                }
            }
        }


    }

    private fun bindViews() {

        binding.apply {

            userName.text = viewModel.currentUser?.displayName.toString()
            Log.d("checkingResponse",viewModel.currentUser?.uid.toString())
            viewModel.getUserFullDetails(viewModel.currentUser?.uid.toString())

            addPost.setOnClickListener(){
                findNavController().navigate(R.id.action_mainFragment_to_createPostFragmnet)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null

    }


}