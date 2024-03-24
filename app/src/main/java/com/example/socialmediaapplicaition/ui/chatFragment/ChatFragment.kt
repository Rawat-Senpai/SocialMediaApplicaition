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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapplicaition.databinding.FragmentChatBinding
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.postPackage.PostListAdapter
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager


    private lateinit var adapter: PostListAdapter


    private val viewModel by viewModels<FirebaseViewModel>()

    private var _binding: FragmentChatBinding? = null
    val binding get() = _binding!!

    // Receiver Data
    private var user: User? = null
    private var previousUser:ChatRoomModel?= null

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

        binding.apply {}

        bindObserver()
        bindViews()

    }

    // set initial state
    private fun getInitialState() {
        val userChat = arguments?.getString("user")
        Log.d("chekcingShobhit1", userChat.toString())

        val previousChat = arguments?.getString("previousChat")

        if (userChat != null) {
            user = Gson().fromJson<User>(userChat, User::class.java)
            user?.let {
                frontPersonId = user!!.id.toString()
                frontPersonImage = user!!.profile.toString()
                frontPersonName = user!!.name.toString()
                frontPersonUserModel = User(frontPersonName, frontPersonId, frontPersonImage)
            }
        }else if (previousChat !=null){
            previousUser = Gson().fromJson<ChatRoomModel>(previousChat,ChatRoomModel::class.java)

            for(data in previousUser!!.userList){

                if(myUserId!= data.id){
                    frontPersonId = data.id
                    frontPersonImage= data.profile
                    frontPersonName = data.name
                    frontPersonUserModel = User(frontPersonName, frontPersonId, frontPersonImage)
                }

            }

        }

        myUserId = tokenManager.getId().toString()

        myUserModel = User(
            tokenManager.getUserName().toString(),
            tokenManager.getId().toString(),
            tokenManager.getProfile().toString()
        )

        chatRoomId = Utils.getChatroomId(tokenManager.getId().toString(),frontPersonId)

        viewModel.getAllMessages(chatRoomId)
    }

    private fun bindViews() {

        binding.apply {

            send.setOnClickListener() {

                val chatRoomRequestModel = ChatRoomModel()
                chatRoomRequestModel.chatroomId = chatRoomId
                chatRoomRequestModel.lastMessage = messageText.text.trim().toString()
                chatRoomRequestModel.userList = arrayListOf(frontPersonUserModel!!, myUserModel!!)
                chatRoomRequestModel.lastMessageSenderId = myUserId
                chatRoomRequestModel.lastMessageTimestamp = System.currentTimeMillis()
                viewModel.addChatRoomToDatabase(chatRoomRequestModel)

            }

        }

    }

    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {



            viewModel.getAllChatChatMessages.collectLatest{

                when(it){
                    is NetworkResult.Error -> {
                        Log.d("ChatResponse",it.toString())
                    }
                    is NetworkResult.Loading -> {

                    }
                    is NetworkResult.Success -> {
                        Log.d("ChatResponse",it.data.toString())
                        Log.d("ChatResponse",it.data?.size.toString())
                    }
                    null -> {

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