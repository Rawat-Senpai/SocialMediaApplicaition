package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentAllUserListBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SerchUserListFragment : Fragment() {
  

    private var _binding:FragmentAllUserListBinding ? = null

    private val userViewModel by viewModels<FirebaseViewModel>()
    private  val binding get() = _binding!!

    private lateinit var adapter: UserListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        _binding= FragmentAllUserListBinding.inflate(layoutInflater,container,false)


        return binding.root

        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_all_user_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(::onActionClicked,Constants.ADD_USER_ACTION)
        binding.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter

        bindObservers()

        bindView()

    }

    private fun bindView() {

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text in the EditText is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text in the EditText is changed

                val searchText = s.toString()

                if(s.toString()!=""){
                    userViewModel.searchUsers(searchText)
                }


            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text in the EditText has been changed
            }
        }

        binding.apply {
            searchEdt.addTextChangedListener(textWatcher)
        }

    }



    private fun bindObservers() {
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.searchedUser.collect{it->
                    when(it){
                        is NetworkResult.Error -> {}
                        is NetworkResult.Loading -> {}
                        is NetworkResult.Success -> {
                            Log.d("checkingSearchResponse",it.data.toString())
                            Log.d("checkingSearchResponse",it.data?.size.toString())
                            adapter.submitList(it.data)
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun onActionClicked(user: User, action:String){
        val bundle = Bundle()
        bundle.putString("user", Gson().toJson(user))
        findNavController().navigate(R.id.action_searchUserListFragment_to_chatFragment,bundle)
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
