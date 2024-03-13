package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.socialmediaapplicaition.databinding.FragmentChatBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.auth.AuthViewModel
import com.example.socialmediaapplicaition.ui.postPackage.PostViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()
    private val postViewModel by viewModels<PostViewModel>()

    private var _binding :FragmentChatBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapter: UserListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding= FragmentChatBinding.inflate(layoutInflater,container,false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(::onActionClicked)


        bindObserver()

        bindView()


    }

    private fun bindView() {
        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.allUsers.collect{it->
                    when(it){
                        is NetworkResult.Error -> {}
                        is NetworkResult.Loading -> {}
                        is NetworkResult.Success -> {
                            Log.d("checkignUserLIst",it.data.toString())
                            Log.d("checkignUserLIst",it.data?.size.toString())
                        }
                        null -> {}
                    }
                }
            }


        }
    }

    private fun bindObserver() {

    }

    private fun onActionClicked(user:User,action:String){
    }




}