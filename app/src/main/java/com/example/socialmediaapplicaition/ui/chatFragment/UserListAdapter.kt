package com.example.socialmediaapplicaition.ui.chatFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapplicaition.databinding.LayoutPostBinding
import com.example.socialmediaapplicaition.models.User
import kotlin.reflect.KFunction2


class UserListAdapter(
    private val onActionClicked:(User,String) -> Unit,
) : ListAdapter<User, UserListAdapter.UserViewHolder>(ComparatorDiffUtil()) {

    inner class UserViewHolder(private val binding: LayoutPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: User) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = LayoutPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}