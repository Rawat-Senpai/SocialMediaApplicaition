package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentChatHistoryBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.auth.AuthViewModel
import com.example.socialmediaapplicaition.ui.postPackage.PostViewModel
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatHistoryListFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()
    private val postViewModel by viewModels<PostViewModel>()

    private var _binding :FragmentChatHistoryBinding?= null
    private val binding get() = _binding!!
    private lateinit var adapter: UserListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding= FragmentChatHistoryBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(::onActionClicked,Constants.CALLING_ACTIONS)
        binding.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter

        bindObserver()

        bindView()


    }

    private fun bindView() {
        binding.apply {
            textView.setOnClickListener(){
                findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment)
            }
        }
    }

    private fun bindObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.allUsers.collect{it->
                when(it){
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {
                        Log.d("checkignUserLIst",it.data.toString())
                        Log.d("checkignUserLIst",it.data?.size.toString())
                        adapter.submitList(it.data)
                    }
                    null -> {}
                }
            }
        }
    }

    private fun onActionClicked(user:User,action:String){

        findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment)

    }




}