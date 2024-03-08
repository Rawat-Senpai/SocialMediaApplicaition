package com.example.socialmediaapplicaition.ui.auth

import android.content.pm.PackageManager
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
import com.example.socialmediaapplicaition.databinding.FragmentSignUpBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.Utils
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.permissionx.guolindev.PermissionX

@AndroidEntryPoint
class SignUpFragment : Fragment() {




    private var _binding:FragmentSignUpBinding?= null

    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel> ()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        _binding= FragmentSignUpBinding.inflate(layoutInflater,container,false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObserver()

        binding.apply {
            btnSignUp.setOnClickListener(){
                val name = txtUsername.text.toString()
                val email= txtEmail.text.toString()
                val password = txtPassword.text.toString()
                viewModel.signup(name,email,password)
            }

            imageView.setOnClickListener{

                if (!Utils.isStoragePermissionGranted(requireActivity())) {
                    Utils.requestStoragePermission(requireActivity())
                } else {
                    // Storage permission already granted
                    // Proceed with your logic here
                }
            }
        }

    }



    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            // First collect viewModel.signupFlow


            viewModel.signupFlow.collect { it ->
                binding.progressBar.isVisible = it is NetworkResult.Loading
                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is NetworkResult.Success -> {
                        Log.d("TAGSignUp", "Success")
                        Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                        addUserToDatabase(it)
                    }

                    else -> {

                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addUserResultState.collect { it ->
                binding.progressBar.isVisible = it is NetworkResult.Loading

                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAGSignUp", it.message.toString())
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()

                    }
                    is NetworkResult.Success -> {
                        Log.d("TAGSignUp", "Success")
                        Log.d("TAGSignUp", it.data.toString())
                    }
                    else -> {

                    }
                }
            }
        }

    }


    private fun addUserToDatabase(networkResult: NetworkResult.Success<FirebaseUser>) {
        Log.d("TAGSignUp","insideAddUser")
        val name = binding.txtUsername.text.toString()
        val requestModel = User(name,networkResult.data?.uid.toString(),"")
        viewModel.addUserToDatabase(requestModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=  null
    }


    private fun uploadImageFromGallery() {
        Utils.pickImageFromGallery(requireActivity(), this) { uri ->
            // Handle the selected image URI here
            Log.d("SelectedImageURI", uri.toString())
            viewModel.uploadImageToFireStore(uri)
            binding.imageView.setImageURI(uri)
        }
    }




  







}