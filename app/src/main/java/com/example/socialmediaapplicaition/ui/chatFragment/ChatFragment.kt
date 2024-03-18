package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentChatBinding
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.ui.postPackage.PostListAdapter
import com.example.socialmediaapplicaition.ui.postPackage.PostViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager


    private lateinit var adapter: PostListAdapter


    private val viewModel by viewModels<PostViewModel> ()



    private var _binding :FragmentChatBinding ?= null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater,container,false)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = PostListAdapter(::onPostClicked,::onPostLiked,tokenManager.getId().toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)

        binding.recyclerView.adapter = adapter
        binding.apply {
        }

        viewModel.getAllPost()
        bindObserver()
        bindViews()

    }

    private fun bindViews() {

        binding.apply {

            send.setOnClickListener(){

                val chatRoomRequestModel =ChatRoomModel()

                chatRoomRequestModel.chatroomId           = "1_2"
                chatRoomRequestModel.lastMessage          = "chekcing"
                chatRoomRequestModel.userIds              = listOf("111", "222")
                chatRoomRequestModel.lastMessageSenderId  = "999"
                chatRoomRequestModel.lastMessageTimestamp = 213123

                viewModel.addChatRoomToDatabase(chatRoomRequestModel)




            }

        }



    }

    private fun bindObserver() {
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allPosts.collect {

                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }


                    is NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                        Log.d("checkingPostResponse1",it.data.toString())
                        adapter.submitList(it.data)
                    }

                    else -> {

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addChatRoomResultState.collect {

                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }


                    is NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                      val chatMessageModel =ChatMessageModel()
                        chatMessageModel.message=""
                        chatMessageModel.timeStamp=3124123

                        viewModel.addChatMessageToDatabase(chatMessageModel)

                    }

                    else -> {

                    }
                }
            }
        }

    }

    private fun onPostClicked(postResponse: Post){
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
        findNavController().navigate(R.id.action_mainFragment_to_postDetailsFragment, bundle)
    }

    private fun onPostLiked(post: Post){
        viewModel.addLikeToPost(post,tokenManager.getId().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }



}