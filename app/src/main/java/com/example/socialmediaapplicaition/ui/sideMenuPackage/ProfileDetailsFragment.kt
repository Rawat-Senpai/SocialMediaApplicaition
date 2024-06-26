package com.example.socialmediaapplicaition.ui.sideMenuPackage


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
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
    private var mySavedId:String=""
    private var userProfilePic:String=""
    private var userName:String=""
    private var userAbout:String=""
    private var userPersonId:String=""
    private var userEmail:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.personImageSquare.visibility = View.GONE

        mySavedId=tokenManager.getId().toString().removeSurrounding("\"")
        adapter = UsersPostAdapter(::onPostClicked)
        binding.postRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.postRecyclerView.adapter = adapter

        setInitialState()
        bindingView()
        bindObserver()


    }

    private fun setInitialState() {

        val myProfileId = arguments?.getString("myProfileId")
        val otherUserId = arguments?.getString("otherProfileId")

        if(myProfileId != null){
            binding.apply {
                userPersonId=myProfileId.removeSurrounding("\"")

                Log.d("checkingUserId",myProfileId)
                userProfilePic = tokenManager.getProfile().toString()
                userName= tokenManager.getUserName().toString()
                personName.text = tokenManager.getUserName()
                userStatus.text = tokenManager.getStatus()
                personImageSquare.visibility = View.VISIBLE
                Glide.with(personImageSquare).load(userProfilePic).placeholder(R.drawable.ic_default_person).into(personImageSquare)
                onlineStatus.text = "Online"
                postViewModel.filterPostUsingId(userPersonId)
                postViewModel.getUserProfileData(userPersonId)
            }
        }else if (otherUserId != null){
            binding.apply {
                userPersonId=otherUserId.removeSurrounding("\"")

                Log.d("checkingUserId_",userPersonId)
                postViewModel.getUserProfileData(userPersonId)
                postViewModel.filterPostUsingId(userPersonId)
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
                               userAbout = it.data?.status.toString()
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

            if(userPersonId==mySavedId){
                Log.d("shobhitTesting","visible")
                val clickableArea: View = motionLayout.findViewById(R.id.editProfile)
                motionLayout.getConstraintSet(R.id.start).getConstraint(clickableArea.id).propertySet.mVisibilityMode =1
                clickableArea.visibility = VISIBLE
            }else{
                Log.d("shobhitTesting","gone")
                val clickableArea: ImageView = motionLayout.findViewById(R.id.editProfile)
                motionLayout.getConstraintSet(R.id.start).getConstraint(clickableArea.id).propertySet.mVisibilityMode=1
                motionLayout.getConstraintSet(R.id.end).getConstraint(clickableArea.id).propertySet.mVisibilityMode=1
                clickableArea.visibility = GONE
            }

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

            binding.editProfile.setOnClickListener(){
                val user =User(userName,mySavedId,userProfilePic,userAbout,Constants.ONLINE,userEmail)
                val bundle = Bundle()
                bundle.putString("profile", Gson().toJson(user))
                findNavController().navigate(R.id.action_profileDetailsFragment_to_editMyProfile,bundle)
            }


            backBtn.setOnClickListener(){
                findNavController().popBackStack()
            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}