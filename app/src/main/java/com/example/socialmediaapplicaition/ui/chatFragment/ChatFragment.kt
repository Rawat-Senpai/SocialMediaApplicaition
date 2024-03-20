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
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.postPackage.PostListAdapter
import com.example.socialmediaapplicaition.ui.postPackage.PostViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager


    private lateinit var adapter: PostListAdapter


    private val viewModel by viewModels<PostViewModel>()

    private var _binding: FragmentChatBinding? = null
    val binding get() = _binding!!

    // Receiver Data
    private var user: User? = null

    private var frontPersonId: String = ""
    private var frontPersonImage: String = ""
    private var frontPersonName: String = ""
    private var frontPersonUserModel: User? = null
    private var myUserModel: User? = null


    // chat room id
    private var chatRoomId:String =""

    private var myUserId:String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getInitialState()



        adapter = PostListAdapter(::onPostClicked, ::onPostLiked, tokenManager.getId().toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerView.adapter = adapter
        binding.apply {
        }

        viewModel.getAllPost()
        bindObserver()
        bindViews()

    }

    // set initial state
    private fun getInitialState() {
        val jsonNote = arguments?.getString("user")

        Log.d("chekcingShobhit1", jsonNote.toString())

        if (jsonNote != null) {
            user = Gson().fromJson<User>(jsonNote, User::class.java)
            user?.let {
                frontPersonId = user!!.id.toString()
                frontPersonImage = user!!.profile.toString()
                frontPersonName = user!!.name.toString()
                frontPersonUserModel = User(frontPersonName, frontPersonId, frontPersonImage)

            }
        } else {

        }

        myUserId = tokenManager.getId().toString()

        myUserModel = User(
            tokenManager.getUserName().toString(),
            tokenManager.getId().toString(),
            tokenManager.getProfile().toString()
        )

        chatRoomId = Utils.getChatroomId(tokenManager.getId().toString(),frontPersonId)
    }

    private fun bindViews() {

        binding.apply {

            send.setOnClickListener() {

                val chatRoomRequestModel = ChatRoomModel()

                chatRoomRequestModel.chatroomId = chatRoomId
                chatRoomRequestModel.lastMessage = messageText.text.trim().toString()
                chatRoomRequestModel.userIds = listOf(frontPersonUserModel!!, myUserModel!!)
                chatRoomRequestModel.lastMessageSenderId = myUserId
                chatRoomRequestModel.lastMessageTimestamp = System.currentTimeMillis()

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
                        Log.d("checkingPostResponse1", it.data.toString())
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
                        val chatMessageModel = ChatMessageModel()
                        chatMessageModel.message = binding.messageText.text.toString()
                        chatMessageModel.removedByMe="0"
                        chatMessageModel.removedByThem="0"
                        chatMessageModel.reply=""
                        chatMessageModel.senderId = tokenManager.getId().toString()
                        chatMessageModel.timeStamp = System.currentTimeMillis()

                        viewModel.addChatMessageToDatabase(chatMessageModel,chatRoomId)

                    }

                    else -> {

                    }
                }
            }
        }

    }

    private fun onPostClicked(postResponse: Post) {
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
//        findNavController().navigate(R.id.action_mainFragment_to_postDetailsFragment, bundle)
    }

    private fun onPostLiked(post: Post) {
        viewModel.addLikeToPost(post, tokenManager.getId().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}