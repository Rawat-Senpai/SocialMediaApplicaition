package com.example.socialmediaapplicaition.ui.chatFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapplicaition.databinding.LayoutHistoryChatBinding
import com.example.socialmediaapplicaition.databinding.LayoutPostBinding
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.Constants
import kotlin.reflect.KFunction2


class UserListAdapter(
    private val onActionClicked:(User,String) -> Unit,
    private val action:String
) : ListAdapter<User, UserListAdapter.UserViewHolder>(ComparatorDiffUtil()) {

    inner class UserViewHolder(private val binding: LayoutHistoryChatBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(user: User) {



                binding.apply {
                    userName.text = user.name.toString()

                    if(action==Constants.ADD_USER_ACTION){
                        // code to hide a view and show another view
                    }else if (action ==Constants.CALLING_ACTIONS){
                        // code to hide a view and show another view
                    }

                    root.setOnClickListener{
                        onActionClicked(user,"")
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = LayoutHistoryChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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