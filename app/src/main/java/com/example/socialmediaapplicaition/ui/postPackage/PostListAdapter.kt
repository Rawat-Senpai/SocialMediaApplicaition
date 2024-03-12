package com.example.socialmediaapplicaition.ui.postPackage

import android.util.Log
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
            private var isLiked =false
            fun bind(post: Post) {

                binding.apply {

                 isLiked= post.likedBy.contains(userId)
                    // Bind your data to the views here
                    userName.text = post.createdBy.name
                    Glide.with(root.context).load(post.createdBy.profile).into(userImage)
                    Glide.with(root.context).load(post.imageUrl).into(userImagePost)


                postTime.text = Utils.getTimeAgo(post.createdAt)

                Log.d("checkingSize_",isLiked.toString())

                likeButton.setImageDrawable(ContextCompat.getDrawable(root.context,if (isLiked) R.drawable.liked else R.drawable.fav_unlike))

                userImagePost.setOnClickListener(){onPostClicked(post)}

                likeButton.setOnClickListener{Log.d("checkingSize",post.likedBy.size.toString())
                    isLiked = !isLiked
                        // Update button image based on new isLiked status
                        likeButton.setImageDrawable(ContextCompat.getDrawable(root.context,if (isLiked) R.drawable.liked else R.drawable.fav_unlike))
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