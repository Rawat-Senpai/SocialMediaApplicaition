package com.example.socialmediaapplicaition.ui.createPost

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapplicaition.databinding.FragmentCreatePostFragmnetBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.auth.AuthViewModel
import com.example.socialmediaapplicaition.ui.postPackage.PostViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager


    private var _binding: FragmentCreatePostFragmnetBinding ?= null
    private val binding get() = _binding!!
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var commonImageUri: Uri? = null
    private  var imageUrl :String = "";

    private val viewModel by viewModels<AuthViewModel> ()

    private val postViewModel by viewModels<PostViewModel> ()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_post_fragmnet, container, false)

        _binding= FragmentCreatePostFragmnetBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObserver()


        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

                binding.tvAddPost.visibility=View.GONE
                binding.addImgBtn.visibility=View.GONE
                binding.imageView.visibility=View.VISIBLE

                commonImageUri?.let { uri ->
                    viewModel.uploadImageToFireStore(uri)
                    binding.imageView.setImageURI(uri)
                }
            } else {
                Toast.makeText(requireContext(),"photo capture failed", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d("Checking", "selectedPic")

            if(uri!= null){

                binding.tvAddPost.visibility=View.GONE
                binding.addImgBtn.visibility=View.GONE
                binding.imageView.visibility=View.VISIBLE
                commonImageUri = uri
                viewModel.uploadImageToFireStore(commonImageUri!!)
                binding.imageView.setImageURI(commonImageUri!!)
            }

        }

        Log.d("shobhitChecking",tokenManager.getProfile().toString())

        Log.d("shobhitChecking",tokenManager.getUserName().toString())
        Log.d("shobhitChecking",tokenManager.getId().toString())

        binding.apply {

            uploadPost.setOnClickListener{
              chooseImage(requireActivity())
            }



            btnSubmit.setOnClickListener(){
                if(commonImageUri== null){
                    Toast.makeText(context,"Please select a image first ",Toast.LENGTH_SHORT).show()

                }else if (txtDescription.text.trim().toString()==""){
                    Toast.makeText(context,"Please write some description",Toast.LENGTH_SHORT).show()

                }else {


                    val post =Post()
                    post.createdAt = System.currentTimeMillis()
                    post.createdBy = User(tokenManager.getUserName().toString(),tokenManager.getId().toString(),tokenManager.getProfile().toString())
                    post.text = txtDescription.text.toString()
                    post.imageUrl = imageUrl
                    postViewModel.addPostToDatabase(post)
                }


            }

        }


    }

    private fun bindObserver() {

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

        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.addPostResultState.collect {
                binding.progressBar.isVisible = it is NetworkResult.Loading

                when (it){
                    is NetworkResult.Error -> {
                        Log.d("error",it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        Log.d("error",it.message.toString())
                    }
                    is NetworkResult.Success -> {
                        findNavController().popBackStack()
                    }
                    null -> {

                    }
                }


            }
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

                if (Utils.checkPermission(requireActivity(), Manifest.permission.CAMERA)) {
                    takeImageFromCameraUri()
                } else {
                    Utils.requestPermission(requireActivity(), Manifest.permission.CAMERA, Utils.CAMERA_PERMISSION_CODE)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding= null

    }

}