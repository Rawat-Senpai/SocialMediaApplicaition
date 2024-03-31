package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentChatBinding
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
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

    // my unique id
    private var myUserId:String = ""


    // adapter
    private lateinit var adapter: ChatAdapter

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


        adapter = ChatAdapter(::onActionClicked,tokenManager.getId().toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter
        // Ensure adapter itemCount is not 0 before scrolling
        if (adapter.itemCount > 0) {
            binding.recyclerView.smoothScrollToPosition(adapter.itemCount-1)
        }



        getInitialState()

        binding.apply {}

        bindObserver()
        bindViews()

    }

    // set initial state
    private fun getInitialState() {

        myUserId = tokenManager.getId().toString()

        // when user is coming from search
        val userChat = arguments?.getString("user")


        // men user is coming from history
        val previousChat = arguments?.getString("previousChat")

        Log.d("chekcingShobhit1", userChat.toString())

        if (userChat != null) {
            user = Gson().fromJson<User>(userChat, User::class.java)
            user?.let {
                binding.personName.text = user!!.name
                frontPersonId = user!!.id.toString()
                frontPersonImage = user!!.profile.toString()
                frontPersonName = user!!.name.toString()
                frontPersonUserModel = User(frontPersonName, frontPersonId, frontPersonImage)
            }

        }else if (previousChat !=null){

            previousUser = Gson().fromJson<ChatRoomModel>(previousChat,ChatRoomModel::class.java)
            for(data in previousUser!!.userList){
                if(myUserId != data.id){
                    frontPersonId = data.id
                    frontPersonImage= data.profile
                    frontPersonName = data.name
                    Glide.with(requireContext()).load(data.profile).placeholder(R.drawable.ic_default_person).into(binding.personImage)
                    binding.personName.text = data.name
                    frontPersonUserModel = User(frontPersonName, frontPersonId, frontPersonImage)
                }

            }

        }



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

            backBtn.setOnClickListener(){
                findNavController().popBackStack()
            }

            send.setOnClickListener() {

                val chatRoomRequestModel = ChatRoomModel()
                chatRoomRequestModel.chatroomId = chatRoomId
                chatRoomRequestModel.lastMessage = messageText.text.trim().toString()
                chatRoomRequestModel.userList = arrayListOf(frontPersonUserModel!!, myUserModel!!)
                chatRoomRequestModel.lastMessageSenderId = myUserId
                chatRoomRequestModel.lastMessageTimestamp = System.currentTimeMillis()
                viewModel.addChatRoomToDatabase(chatRoomRequestModel)

                binding.recyclerView.smoothScrollToPosition(adapter.itemCount-1)
                binding.recyclerView.smoothScrollToPosition(adapter.itemCount-1)
            }

        }

    }

    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.getAllChatChatMessages.collect(){
                binding.progressBar.isVisible = it is NetworkResult.Loading
                when(it){

                    is NetworkResult.Error -> {
                        Log.d("ChatResponse",it.toString())
                    }

                    is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                    }

                    is NetworkResult.Success -> {
                        Log.d("ChatResponse",it.data.toString())
                        Log.d("ChatResponseSize",it.data?.size.toString())
                        Log.d("ChatResponseSize_",adapter.itemCount.toString())
                        adapter.submitList(it.data)
                        binding.recyclerView.smoothScrollToPosition(adapter.itemCount)
                    }
                    null -> {

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addChatRoomResultState.collect {

                binding.progressBar.isVisible = it is NetworkResult.Loading
                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }


                    is NetworkResult.Loading -> {
                            binding.progressBar.isVisible=true
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
                        binding.messageText.setText("")


                    }

                    else -> {

                    }
                }
            }
        }

    }


    private fun onActionClicked(chat:ChatMessageModel,action:String){
        val bundle = Bundle()
        bundle.putString("currentChat", Gson().toJson(chat))
        findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment,bundle)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}