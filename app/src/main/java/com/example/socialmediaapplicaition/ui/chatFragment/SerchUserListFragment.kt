package com.example.socialmediaapplicaition.ui.chatFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialmediaapplicaition.databinding.FragmentAllUserListBinding


class SerchUserListFragment : Fragment() {
  

    private var _binding:FragmentAllUserListBinding ? = null
    private  val binding get() = _binding!!
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





    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null


    }


}
