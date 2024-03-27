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
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentContactBinding
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.ui.chatFragment.UserChatHistoryAdapter
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()

    private  var _binding :FragmentContactBinding ?= null

    private lateinit var adapter: UserChatHistoryAdapter
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentContactBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
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
    private fun onActionClicked(user: ChatRoomModel, action:String){
        val bundle = Bundle()
        bundle.putString("previousChat", Gson().toJson(user))
        findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment,bundle)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}