package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatHistoryListFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel by viewModels<FirebaseViewModel>()
    private var _binding: FragmentChatHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserChatHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChatHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UserChatHistoryAdapter(::onActionClicked, tokenManager.getId().toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter
        viewModel.getAllPreviousChat(tokenManager.getId().toString())

        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.apply {

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text in the EditText is changed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text in the EditText is changed

                    val searchText = s.toString()
                    Log.d("checking123",searchText)
                    if (s.toString() != "") {
                        viewModel.searchContacts(searchText, tokenManager.getId().toString())
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
            searchEdt.addTextChangedListener(textWatcher)
        }
    }

    private fun bindObserver() {


        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.getAllChatHistory.collect { it ->
                    binding.progressBar.isVisible = false
                    when (it) {
                        is NetworkResult.Error -> {
                            Log.d("checkingHistory", it.toString())
                        }

                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                            Log.d("checkingHistory", it.toString())
                        }

                        is NetworkResult.Success -> {
                            Log.d("checkingHistory", it.data.toString())
                            adapter.submitList(it.data)
                        }

                        null -> {

                        }
                    }
                }
            }
            
            launch {

                viewModel.searchContact.collect { it ->
                    binding.progressBar.isVisible = false
                    when (it) {
                        is NetworkResult.Error -> {
                            Log.d("checkingHistory", it.toString())
                        }

                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                            Log.d("checkingHistory", it.toString())
                        }

                        is NetworkResult.Success -> {
                            Log.d("checkContactSearch", it.data.toString())
                            adapter.submitList(it.data)
                        }

                        null -> {

                        }
                    }
                }

            }


        }
    }

    private fun onActionClicked(user: ChatRoomModel, action: String) {
        val bundle = Bundle()
        bundle.putString("previousChat", Gson().toJson(user))
        findNavController().navigate(R.id.action_chatHistoryFragment_to_chatFragment, bundle)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}