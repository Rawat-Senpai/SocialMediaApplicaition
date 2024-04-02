package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentProfileDetailsBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import com.example.socialmediaapplicaition.viewModels.AuthViewModel
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailsFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager
    private val postViewModel by viewModels<FirebaseViewModel>()
    private val viewModel by viewModels<AuthViewModel> ()
    private  var _binding: FragmentProfileDetailsBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapter: UsersPostAdapter
    var userProfilePic:String=""
    var userName:String=""


    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var commonImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UsersPostAdapter(::onPostClicked)
        binding.postRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.postRecyclerView.adapter = adapter

        setInitialState()
        bindingView()
        bindObserver()




        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

                commonImageUri?.let { uri ->

                    viewModel.uploadImageToFireStore(commonImageUri!!)

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
            }
        }
    }

    private fun setInitialState() {

        val myProfileId = arguments?.getString("myProfileId")
        val otherUserId = arguments?.getString("otherProfileId")



        if(myProfileId != null){
            binding.apply {

                Log.d("checkingUserId",myProfileId)

                userProfilePic = tokenManager.getProfile().toString()
                userName= tokenManager.getUserName().toString()
                personName.text = tokenManager.getUserName()
                userStatus.text = tokenManager.getStatus()
                Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).into(personImageSquare)
                onlineStatus.text = "Online"
                postViewModel.filterPostUsingId(myProfileId)
                postViewModel.getUserProfileData(myProfileId)

            }


        }else if (otherUserId != null){
            binding.apply {
                Log.d("checkingUserId",otherUserId)
                postViewModel.getUserProfileData(otherUserId)
                postViewModel.filterPostUsingId(otherUserId)
            }
        }
    }

    private fun onPostClicked(postResponse: Post){
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
        findNavController().navigate(R.id.action_profileDetailsFragment_to_postDetailsFragment, bundle)
    }


    private fun bindObserver() {



        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                postViewModel.userProfileData.collect{it->
                    binding.progressBar.isVisible = it is NetworkResult.Loading
                    when(it){
                       is NetworkResult.Error -> {
                           Log.d("checkingData",it.message.toString())
                       }

                       is NetworkResult.Loading -> {

                           binding.progressBar.isVisible = true
                       }

                       is NetworkResult.Success -> {
                           binding.apply {
                                Log.d("checkingData",it.data.toString())
                               personName.text = it.data?.name
                               userStatus.text = it.data?.status
                               onlineStatus.text = it.data?.onlineStatus
                               userProfilePic = it.data?.profile.toString()
                               userName = it.data?.name.toString()
                               Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).into(personImageSquare)

                           }

                       }

                       null -> {}
                   }

                }
            }


            launch {
                postViewModel.userSpecificPost.collect { it ->
                    Log.d("checkingShobhitprofile", it?.data.toString())
                    when (it) {
                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                        is NetworkResult.Success -> {
                            Log.d("checkingDataPost", it.data.toString())
                            adapter.submitList(it.data)
                        }
                        is NetworkResult.Error -> {
                            Log.d("error", it.message.toString())
                            Log.d("checkingShobhitprofile", it?.message.toString())
                        }
                        else -> {}
                    }
                }
            }

            launch {
                viewModel.uploadPhotoResult.collect{
                    when(it){
                        is NetworkResult.Error -> {}
                        is NetworkResult.Loading -> {}
                        is NetworkResult.Success -> {
                            userProfilePic = it.data.toString()
                            updateUserData()
                        }
                        null -> {}
                    }
                }
            }

        }

    }

    private fun updateUserData() {

    }

    private fun bindingView() {

        binding.apply {

            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {}

                override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                    Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).circleCrop().into(personImageSquare)
                }

                override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {

                    if(currentId == R.id.start){
                        Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).into(personImageSquare)
                    }else if (currentId == R.id.end){
                        Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).circleCrop().into(personImageSquare)
                    }

                }

                override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
            })

            backBtn.setOnClickListener(){
                findNavController().popBackStack()
            }


            personImageSquare.setOnClickListener(){
                chooseImage(requireActivity())
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