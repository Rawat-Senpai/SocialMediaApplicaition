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
import com.example.socialmediaapplicaition.databinding.LayoutCommentsBinding
import com.example.socialmediaapplicaition.models.PersonComments


class CommentAdapter () : ListAdapter<PersonComments, CommentAdapter.CommentViewHolder>(ComparatorDiffUtil()) {

    inner class CommentViewHolder(private val binding: LayoutCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(comment:PersonComments){
                binding.apply {
                    Glide.with(root).load(comment.personProfile).placeholder(R.drawable.ic_default_person).into(myImage)
                    commentText.text = comment.comment
                    personName.text = comment.personName
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = LayoutCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<PersonComments>() {
        override fun areItemsTheSame(oldItem: PersonComments, newItem: PersonComments): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: PersonComments, newItem: PersonComments): Boolean {
            return oldItem == newItem
        }
    }
}