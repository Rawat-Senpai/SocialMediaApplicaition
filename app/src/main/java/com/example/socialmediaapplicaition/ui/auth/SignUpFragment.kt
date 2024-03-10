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
import com.example.socialmediaapplicaition.databinding.FragmentSignUpBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.Utils
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.utils.TokenManager
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {



    private var _binding:FragmentSignUpBinding?= null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel> ()

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var commonImageUri: Uri? = null
    private  var imageUrl :String = "";




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

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

                commonImageUri?.let { uri ->
                    viewModel.uploadImageToFireStore(uri)
                    binding.imageView.setImageURI(uri)
                }
            } else {
                Toast.makeText(requireContext(),"photo capture failed",Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d("Checking", "selectedPic")

            if(uri!= null){
                commonImageUri = uri
                viewModel.uploadImageToFireStore(commonImageUri!!)
                binding.imageView.setImageURI(commonImageUri!!)
            }

        }

        binding.apply {
            btnSignUp.setOnClickListener(){
                val name = txtUsername.text.toString()
                val email= txtEmail.text.toString()
                val password = txtPassword.text.toString()
                viewModel.signup(name,email,password)
            }

            imageView.setOnClickListener{
                chooseImage(requireActivity())

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
                        findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
                    }
                    else -> {

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uploadPhotoResult.collect{

                when(it){
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {
                        Log.d("response",it.data.toString())
                        imageUrl= it.data.toString()
                    }
                    null -> {

                    }
                }
            }
        }

    }

    private fun addUserToDatabase(networkResult: NetworkResult.Success<FirebaseUser>) {
        Log.d("TAGSignUp","insideAddUser")
        val name = binding.txtUsername.text.toString()
        val requestModel = User(name,networkResult.data?.uid.toString(),imageUrl)
        viewModel.addUserToDatabase(requestModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=  null
    }

    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        )
        val builder = AlertDialog.Builder(context)

        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {
//

                if (Utils.checkPermission(requireActivity(), Manifest.permission.CAMERA)) {
                    takeImageFromCameraUri()
                } else {
                    Utils.requestPermission(requireActivity(), Manifest.permission.CAMERA,Utils.CAMERA_PERMISSION_CODE)
                }

            } else if (optionsMenu[i] == "Choose from Gallery") {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    imageChooser()
                } else {
                    if (Utils.checkPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        imageChooser()
                    } else {
                        Utils.requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, Utils.STORAGE_PERMISSION_CODE)
                    }
                }

            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    private fun takeImageFromCameraUri() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis())
        commonImageUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        cameraLauncher.launch(commonImageUri)

    }

    private fun imageChooser() {
        galleryLauncher.launch("image/*")
    }


}