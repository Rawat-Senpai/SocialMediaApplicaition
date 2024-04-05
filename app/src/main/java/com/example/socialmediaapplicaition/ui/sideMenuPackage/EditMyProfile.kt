package com.example.socialmediaapplicaition.ui.sideMenuPackage

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
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentEditMyProfileBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.CustomEditProfileDialog
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import com.example.socialmediaapplicaition.viewModels.AuthViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import javax.inject.Inject

@AndroidEntryPoint
class EditMyProfile : Fragment() {

    private var _binding :FragmentEditMyProfileBinding ?= null
    val binding get() = _binding!!
    var user: User?=null
    private val viewModel by viewModels<AuthViewModel> ()
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var commonImageUri: Uri? = null
    private var updateType=""
    private var updateValue=""

    @Inject
    lateinit var  tokenManager:TokenManager
    private var myUserId=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditMyProfileBinding.inflate(layoutInflater,container,false)
        return binding.root

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        myUserId = tokenManager.getId().toString()

        setInitialState()

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {

                commonImageUri?.let { uri ->
                    binding.userImage.setImageURI(uri)

                }
            } else {
                Toast.makeText(requireContext(),"photo capture failed", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d("Checking", "selectedPic")

            if(uri!= null){


                commonImageUri = uri
                binding.userImage.setImageURI(commonImageUri!!)
            }

        }


        bindingView()
        bindObserver()

    }

    private fun setInitialState() {

        val myUserProfile = arguments?.getString("profile")

        Log.d("newProfile",myUserProfile.toString())
        if(myUserProfile!= null){

            Log.d("newProfile",myUserProfile.toString())

            user = Gson().fromJson<User>(myUserProfile,User::class.java)
            user?.let {
                Log.d("newProfile___",user.toString())
                binding.apply {
                    Glide.with(userImage).load(user?.profile).placeholder(R.drawable.ic_default_person).into(userImage)
                    userName.text = user?.name
                    userStatus.text = user?.status
                    gmailText.text
                }
            }

        }

    }

    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.uploadPhotoResult.collect(){
                    when(it){
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {

                        }
                        is NetworkResult.Success -> {
                            viewModel.updateUserData(myUserId,Constants.PROFILE,it.data.toString())
                        }
                        null -> {

                        }
                    }
                }
            }
        }



    }

    private fun bindingView() {
        binding.apply {

            backBtn.setOnClickListener(){
                findNavController().popBackStack()
            }

            clickPhoto.setOnClickListener(){
                chooseImage(requireActivity())
            }

            userName.setOnClickListener(){
                updateType=Constants.NAME
                val dialog = CustomEditProfileDialog(requireActivity(),::onActionClicked,updateType)
                dialog.show()
            }



            userStatus.setOnClickListener(){
                updateType=Constants.STATUS
                val dialog = CustomEditProfileDialog(requireActivity(),::onActionClicked,updateType)
                dialog.show()
            }


        }
    }


    private fun onActionClicked(value:String){
        viewModel.updateUserData(myUserId,updateType,value)

       when (updateType){
           Constants.NAME -> {
               binding.userName.text = value
           }

           Constants.STATUS ->{
               binding.userStatus.text = value
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


}