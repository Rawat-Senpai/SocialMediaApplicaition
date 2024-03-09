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
import com.bumptech.glide.Glide
import com.permissionx.guolindev.PermissionX

@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private var _binding:FragmentSignUpBinding?= null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel> ()



    private var CommonImageUri: Uri? = null
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101
    private var cameraPermession = false
    private var SELECT_PICTURE = 200
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

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


                checkPermission(
                    Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_CODE
                )

                if (cameraPermession) {
                    chooseImage(requireActivity())
                }


//                if (!Utils.isStoragePermissionGranted(requireActivity())) {
//                    Utils.requestStoragePermission(requireActivity())
//                } else {
//                    uploadImageFromGallery()
//                    // Storage permission already granted
//                    // Proceed with your logic here
//                }
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


    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
        } else {
            cameraPermession = true
        }
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
                takeImageFromCameraUri()
            } else if (optionsMenu[i] == "Choose from Gallery") {
                imageChooser()
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }


    private fun takeImageFromCameraUri() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
        values.put(
            MediaStore.Images.Media.DESCRIPTION,
            "Photo taken on " + System.currentTimeMillis()
        )
        CommonImageUri = requireActivity().application.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CommonImageUri)
        startActivityForResult(intent, 0)
    }

    fun imageChooser() {
        verifyStoragePermissions(requireActivity())
        // create an instance of the
        // intent of the type image
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(intent, "Open Gallery"), SELECT_PICTURE)
        //   CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(FormSix.this);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("checking",requestCode.toString()+"  "+resultCode.toString())

        if (resultCode == AppCompatActivity.RESULT_OK) {

            if (requestCode == 0) {
                Log.d("Checking","camera")
//                binding.profilePic.setImageURI(CommonImageUri)
//                uploadPhotoFunction(CommonImageUri)
//                viewModel.uploadImageToFireStore(CommonImageUri!!)
//                binding.imageView.setImageURI(CommonImageUri!!)
                try {

                } catch (e: Exception) {
                    Log.d("crash_check", e.toString())
                }
            } else if (requestCode == SELECT_PICTURE) {
                Log.d("Checking","selectedPic")
                CommonImageUri = data!!.data

                viewModel.uploadImageToFireStore(CommonImageUri!!)
                binding.imageView.setImageURI(CommonImageUri!!)
              

            }
        }

    }



    private fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (activity != null) {
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        } else {
            Log.d("camera permission", "else statement 1026")
        }
    }





}