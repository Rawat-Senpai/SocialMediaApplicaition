package com.example.socialmediaapplicaition.ui.postPackage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentMainBinding
import com.example.socialmediaapplicaition.databinding.LayoutSideMenuBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.viewModels.AuthViewModel
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PostFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostListAdapter
    private val viewModel by viewModels<AuthViewModel>()
    private val postViewModel by viewModels<FirebaseViewModel>()
    private var SideMenuImage: ImageView? = null
    private var userTextView: TextView? = null
    private var videoCall: LinearLayout? = null
    private var contactLayout: LinearLayout? = null
    private var savedLayout: LinearLayout? = null
    private var settingLayout: LinearLayout? = null
    private var shareLayout: LinearLayout? = null
    private var sideMenuBackground: LinearLayout? = null
    private var profileLayout: LinearLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostListAdapter(
            ::onPostClicked,
            ::onPostLiked,
            ::onPostUserClicked,
            ::onPostSaved,
            tokenManager.getId().toString()
        )
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        viewModel.getUserFullDetails(tokenManager.getId().toString())

        bindObserver()
        bindViews()
        bindSideMenu()
        // update onlineStatus
        postViewModel.updateUserOnlineStatus(tokenManager.getId().toString(), Constants.ONLINE)


    }

    private fun bindSideMenu() {
        val sideBinding = LayoutSideMenuBinding.bind(binding.sideMenu)
        SideMenuImage = sideBinding.profileImage
        userTextView = sideBinding.personName
        videoCall = sideBinding.videoCallLayout
        contactLayout = sideBinding.contactLayout
        savedLayout = sideBinding.savedPost
        settingLayout = sideBinding.settingLayout
        shareLayout = sideBinding.shareApp
        sideMenuBackground = sideBinding.sideMenuBackground
        profileLayout = sideBinding.profileLayout

        // this is done so that it do n
        sideMenuBackground?.setOnClickListener() {}

        videoCall?.setOnClickListener() {
            Toast.makeText(requireContext(), "working on this", Toast.LENGTH_SHORT).show()
        }

        contactLayout?.setOnClickListener() {
            findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
        }

        savedLayout?.setOnClickListener() {
            val bundle = Bundle()
            bundle.putString("myUserId", Gson().toJson(tokenManager.getId()))
            findNavController().navigate(R.id.action_mainFragment_to_savedPostFragment, bundle)
        }

        profileLayout?.setOnClickListener() {
            val bundle = Bundle()
            bundle.putString("myProfileId", Gson().toJson(tokenManager.getId()))
            findNavController().navigate(R.id.action_mainFragment_to_profileDetailsFragment, bundle)
        }

        userTextView?.text = tokenManager.getUserName().toString()
        Glide.with(requireContext()).load(tokenManager.getProfile())
            .placeholder(R.drawable.ic_default_person).into(SideMenuImage!!)

    }

    private fun onPostClicked(postResponse: Post) {
        val bundle = Bundle()
        bundle.putString("post", Gson().toJson(postResponse))
        findNavController().navigate(R.id.action_mainFragment_to_postDetailsFragment, bundle)
    }

    private fun onPostLiked(post: Post) {
        postViewModel.addLikeToPost(post, tokenManager.getId().toString())
    }

    private fun onPostUserClicked(post: Post) {
        val bundle = Bundle()
        bundle.putString("otherProfileId", Gson().toJson(post.createdBy.id))
        findNavController().navigate(R.id.action_mainFragment_to_profileDetailsFragment, bundle)
    }

    private fun onPostSaved(post: Post, action: String) {
        postViewModel.crateSavePost(post, tokenManager.getId().toString(), action)

        postViewModel.savePost(post, tokenManager.getId().toString())


    }


    private fun bindObserver() {

        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.userData.collect { it ->
                    when (it) {
                        is NetworkResult.Success -> {
                            Log.d("checkingDataUser", it.data?.profile.toString())
                            Log.d("checkingDataUser", it.data?.name.toString())
                        }

                        is NetworkResult.Error -> {
                            // Handle error state if necessary
                        }

                        is NetworkResult.Loading -> {
                            // Handle loading state if necessary
                        }

                        else -> {

                        }

                    }
                }
            }

            launch {
                postViewModel.allPosts.collect { it ->
                    binding.progressBar.isVisible = false
                    when (it) {
                        is NetworkResult.Success -> {
                            Log.d("checkingPostResponse00", it.data.toString())
                            adapter.submitList(it.data)
                        }

                        is NetworkResult.Error -> {
                            Log.d("checkingPostResponse01", it.message.toString())
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        else -> {}
                    }
                }
            }


        }


    }

    private fun bindViews() {

        binding.apply {

            menuImg.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }


            infoImg.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_infoFragment)
            }

            chatLayout.setOnClickListener() {
                findNavController().navigate(R.id.action_mainFragment_to_chatHistoryFragment)
            }

            setting.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_allUserListFragment)
            }

            userName.text = viewModel.currentUser?.displayName.toString()
            Log.d("checkingResponse", viewModel.currentUser?.uid.toString())
            viewModel.getUserFullDetails(viewModel.currentUser?.uid.toString())

            addPost.setOnClickListener() {
//                startActivity(Intent(context,NewActivity::class.java))
                findNavController().navigate(R.id.action_mainFragment_to_createPostFragmnet)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        postViewModel.updateUserOnlineStatus(
            tokenManager.getId().toString(),
            System.currentTimeMillis().toString()
        )
        _binding = null
    }


}