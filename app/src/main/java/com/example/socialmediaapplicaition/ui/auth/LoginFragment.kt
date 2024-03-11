package com.example.socialmediaapplicaition.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentLoginBinding
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()
    private  var _binding : FragmentLoginBinding?= null
    private val  binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObserver()
        bindViews()

    }

    private fun bindViews(){

        binding.apply{

            btnLogin.setOnClickListener(){
                val email = txtEmail.text.toString()
                val password = txtPassword.text.toString()

                viewModel.login(email,password)
            }


            btnSignUp.setOnClickListener(){
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }



        }

    }


    private fun bindObserver() {

        lifecycleScope.launch {
            viewModel.loginFlow.collect { it ->
                binding.progressBar.isVisible = it is NetworkResult.Loading

                when(it){

                    is NetworkResult.Error -> {
                        Log.d("TAG",it.message.toString())

                        Toast.makeText(requireContext(),"error",Toast.LENGTH_SHORT).show()

                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible=true
                    }
                    is NetworkResult.Success -> {
                        Log.d("TAG","Success")
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    }

                    else -> {

                    }
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}