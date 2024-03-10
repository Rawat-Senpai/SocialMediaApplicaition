package com.example.socialmediaapplicaition.ui.mainPackage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
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
import kotlinx.coroutines.launch


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


        postViewModel.getAllUser()

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
            viewModel.userData.collect{
                when(it){
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {}
                    null -> {}
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