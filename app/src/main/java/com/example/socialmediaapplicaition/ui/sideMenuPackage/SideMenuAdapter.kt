package com.example.socialmediaapplicaition.ui.sideMenuPackage

import android.service.autofill.UserData
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.LayoutHistoryChatBinding
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.ui.chatFragment.UserChatHistoryAdapter


class SideMenuAdapter ( private val onActionClicked:(User, String) -> Unit,
                        private val myId:String
) : ListAdapter<ChatRoomModel, SideMenuAdapter.UserPreviousChatViewHolder>(ComparatorDiffUtil()) {

    inner class UserPreviousChatViewHolder(private val binding: LayoutHistoryChatBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(userChat: ChatRoomModel) {

            Log.d("checkingDataAdapter0",userChat.toString())

            var name =""
            var profile=""
            var status =""
            var userData=User()
            for (data in userChat.userList){
                Log.d("checkingDataAdapter1",data.toString())
                Log.d("checkingDataAdapter12",myId)
                //h2ik9gkxyuhXJwPFtNaKeT1r1ti1
                if(data.id != myId){
                    name = data.name
                    profile = data.profile
                    status = data.status
                    userData =User(name,data.id.toString(),profile,status)
                    Log.d("checkingDataAdapter21",name+"  "+profile)
                }else{
                 // my data which is shown here
                }
            }




            binding.apply {
                userName.text = name
                userStatus.text = status
                Glide.with(root).load(profile).placeholder(R.drawable.ic_default_person).into(imgCardView)

                root.setOnClickListener{
                    onActionClicked(userData,"")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPreviousChatViewHolder {
        val binding = LayoutHistoryChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPreviousChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserPreviousChatViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)


    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<ChatRoomModel>() {
        override fun areItemsTheSame(oldItem: ChatRoomModel, newItem: ChatRoomModel): Boolean {
            return oldItem.chatroomId == newItem.chatroomId
        }

        override fun areContentsTheSame(oldItem: ChatRoomModel, newItem: ChatRoomModel): Boolean {
            return oldItem == newItem
        }
    }
}