package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentChatHistoryBinding
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.viewModels.AuthViewModel
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatHistoryListFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager

    private val viewModel by viewModels<FirebaseViewModel>()

    private var _binding :FragmentChatHistoryBinding?= null
    private val binding get() = _binding!!
    private lateinit var adapter: UserChatHistoryAdapter
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

        adapter = UserChatHistoryAdapter(::onActionClicked,tokenManager.getId().toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter
        viewModel.getAllPreviousChat(tokenManager.getId().toString())
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
            viewModel.getAllChatHistory.collect{it->
                binding.progressBar.isVisible=false
                when(it){
                    is NetworkResult.Error -> {
                        Log.d("checkingHistory",it.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible=true
                        Log.d("checkingHistory",it.toString())
                    }
                    is NetworkResult.Success -> {
                        Log.d("checkingHistory",it.data.toString())
                        adapter.submitList(it.data)
                    }
                    null ->{

                    }
                }
            }
        }
    }

    private fun onActionClicked(user:ChatRoomModel,action:String){

        findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment)

    }




}