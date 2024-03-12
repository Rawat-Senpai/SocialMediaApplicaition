package com.example.socialmediaapplicaition.ui.postPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.FragmentPostDetailsBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PostDetailsFragment : Fragment() {
    private var post :Post?= null

    private var _binding:FragmentPostDetailsBinding?= null
    val  binding  get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialData()
    }

    private fun setInitialData() {
        val jsonNote = arguments?.getString("post")
        if (jsonNote != null) {
            post = Gson().fromJson<Post>(jsonNote, Post::class.java)
            post?.let {
                // bind ui code here
                binding.apply {
                    val isLiked= post?.likedBy?.contains(tokenManager.getId().toString())
                    // Bind your data to the views here
                    userName.text = post?.createdBy?.name
                    Glide.with(root.context).load(post?.createdBy?.profile).into(userImage)
                    Glide.with(root.context).load(post?.imageUrl).into(userImagePost)

                    postTime.text = Utils.getTimeAgo(post?.createdAt!!)

                    if(isLiked!!){
                        likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context,R.drawable.liked))
                    }
                    else{
                        likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context,R.drawable.fav_unlike))
                    }

                }


            }
        }
    }


}