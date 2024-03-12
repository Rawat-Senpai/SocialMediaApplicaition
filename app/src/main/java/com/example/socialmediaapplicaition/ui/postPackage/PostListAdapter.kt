package com.example.socialmediaapplicaition.ui.postPackage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.LayoutPostBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.utils.Utils
import javax.inject.Inject


class PostListAdapter(private val onPostClicked: (Post) -> Unit,private val onPostLiked: (Post) -> Unit,private val userId:String) : ListAdapter<Post, PostListAdapter.PostViewHolder>(ComparatorDiffUtil()) {

    inner class PostViewHolder(private val binding: LayoutPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {

                    val isLiked= post.likedBy.contains(userId)
                    // Bind your data to the views here
                    userName.text = post.createdBy.name
                    Glide.with(root.context).load(post.createdBy.profile).into(userImage)
                    Glide.with(root.context).load(post.imageUrl).into(userImagePost)

                    postTime.text = Utils.getTimeAgo(post.createdAt)

                if(isLiked){
                    likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context,R.drawable.liked))
                }
                else{
                    likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.context,R.drawable.fav_unlike))
                }


                    userImagePost.setOnClickListener(){
                        onPostClicked(post)
                    }

                    likeButton.setOnClickListener(){
                        onPostLiked(post)
                    }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = LayoutPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}