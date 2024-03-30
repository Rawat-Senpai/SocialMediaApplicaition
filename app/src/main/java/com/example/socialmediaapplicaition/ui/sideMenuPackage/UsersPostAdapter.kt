package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.LayoutUserPostBinding
import com.example.socialmediaapplicaition.models.Post


class UsersPostAdapter (
    private val onPostClicked: (Post) -> Unit
) : ListAdapter<Post, UsersPostAdapter.PostViewHolder>(ComparatorDiffUtil()) {

    inner class PostViewHolder(private val binding: LayoutUserPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {

            binding.apply {
                Glide.with(root.context).load(post.createdBy.profile).placeholder(R.drawable.ic_default_person).into(imageView)
                imageView.setOnClickListener() { onPostClicked(post) }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = LayoutUserPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }


}