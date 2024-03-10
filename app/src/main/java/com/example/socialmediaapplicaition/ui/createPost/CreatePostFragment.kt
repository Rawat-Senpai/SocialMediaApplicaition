package com.example.socialmediaapplicaition.ui.createPost

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentCreatePostFragmnetBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.auth.AuthViewModel
import com.example.socialmediaapplicaition.ui.mainPackage.MainViewModel
import com.example.socialmediaapplicaition.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
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

    private val postViewModel by viewModels<MainViewModel> ()


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



        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

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
                commonImageUri = uri
                viewModel.uploadImageToFireStore(commonImageUri!!)
                binding.imageView.setImageURI(commonImageUri!!)
            }

        }

        Log.d("shobhitChecking",tokenManager.getProfile().toString())

        Log.d("shobhitChecking",tokenManager.getUserName().toString())
        Log.d("shobhitChecking",tokenManager.getId().toString())

        binding.apply {

            btnSubmit.setOnClickListener(){
                if(commonImageUri== null){
                    Toast.makeText(context,"Please select a image first ",Toast.LENGTH_SHORT).show()

                }else if (txtDescription.text.trim().toString()==""){
                    Toast.makeText(context,"Please write some description",Toast.LENGTH_SHORT).show()

                }else {
                    val post =Post()
                    post.createdAt = System.currentTimeMillis()
//                    post.createdBy = User(viewModel.userData.value?.data!!.name)
                }


            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null

    }

}