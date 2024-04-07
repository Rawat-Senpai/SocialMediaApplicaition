package com.example.socialmediaapplicaition.ui.chatFragment

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.contentcapture.ContentCaptureSession
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.RowChatBinding
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.Utils

class ChatAdapter (
    private val onActionClicked:(ChatMessageModel) -> Unit,
    private val myUserId:String
) : ListAdapter<ChatMessageModel, ChatAdapter.ChatViewHolder>(ComparatorDiffUtil()) {

    inner class ChatViewHolder(private val binding: RowChatBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(chat: ChatMessageModel) {

            binding.apply {
                if(chat.removedBy.contains(myUserId)){
                    binding.root.isVisible=false
                    itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }


                if(chat.senderId==myUserId){
                    // layout for my chat
                    // sent layout


                    llRecieveLayout.isVisible=false
                    llSentLayout.isVisible=true


                    val myEmojiList =ArrayList<ImageView>()
                    myEmojiList.add(emojiSent)
                    myEmojiList.add(emojiSentMe)

                    chat.emojiReacted.forEachIndexed { index, item ->

                        Log.d("chheckingDistance",index.toString())
                        if(index<2){
                            when(item.senderReaction){

                                Constants.ACTION_SED->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_sed).into(myEmojiList[index])
                                }

                                Constants.ACTION_LAUGHING->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_loughing).into(myEmojiList[index])
                                }

                                Constants.ACTION_SURPRISED->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_surpriced).into(myEmojiList[index])
                                }

                                Constants.ACTION_THUMBS_UP->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_thumbsup).into(myEmojiList[index])
                                }

                                Constants.ACTION_THUMBS_DOWN->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_thoumbsdown).into(myEmojiList[index])
                                }

                            }
                        }

                    }


                    if(chat.reply!=""){
                        repliedTextSender.text=chat.message
                        repliedTextSender.visibility=View.VISIBLE
                    }

                    // checking message type
                    if(chat.type==Constants.MESSAGE_TYPE_TEXT){
                        sentMessage.isVisible=true
                        sentImage.isVisible=false
                        sentVideo.isVisible=false
                        sentMessage.text=chat.message

                    }else if (chat.type == Constants.MESSAGE_TYPE_IMAGE){
                        sentMessage.isVisible=false
                        sentImage.isVisible=true
                        sentVideo.isVisible=false
                        Glide.with(root).load(chat.message).into(sentImage)
                    }else if (chat.type ==  Constants.MESSAGE_TYPE_VIDEO){
                        sentMessage.isVisible=false
                        sentImage.isVisible=false
                        sentVideo.isVisible=true
                        sentVideo.setVideoURI(Uri.parse(chat.message))
                         sentVideo.setOnClickListener(){
                             if(!sentVideo.isPlaying){
                                 sentVideo.start()
                             }
                         }
                    }

                    sentTime.text = Utils.convertMillisToTime(chat.timeStamp)

                }else{
                    // layout for front person
                    // recieved layout


                    val myEmojiList =ArrayList<ImageView>()
                    myEmojiList.add(emojiReceived)
                    myEmojiList.add(emojiReceivedMe)

                    chat.emojiReacted.forEachIndexed { index, item ->
                        Log.d("chheckingDistance1",index.toString())
                        if(index<2){
                            when(item.senderReaction){

                                Constants.ACTION_SED->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_sed).into(myEmojiList[index])
                                }

                                Constants.ACTION_LAUGHING->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_loughing).into(myEmojiList[index])
                                }

                                Constants.ACTION_SURPRISED->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_surpriced).into(myEmojiList[index])
                                }

                                Constants.ACTION_THUMBS_UP->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_thumbsup).into(myEmojiList[index])
                                }

                                Constants.ACTION_THUMBS_DOWN->{
                                    Glide.with(myEmojiList[index]).load(R.drawable.emoji_thoumbsdown).into(myEmojiList[index])
                                }

                            }

                        }



                    }

                    llRecieveLayout.isVisible=true
                    llSentLayout.isVisible=false

                    if(chat.reply!=""){
                        repliedTextRecieve.visibility=View.VISIBLE
                        repliedTextRecieve.text=chat.reply
                    }


                    // checking message type
                    if(chat.type==Constants.MESSAGE_TYPE_TEXT){
                        recieveImage.isVisible=false
                        recievedMessage.isVisible=true
                        recieveVideo.isVisible = false
                        recievedMessage.text= chat.message
                    }else if(chat.type == Constants.MESSAGE_TYPE_IMAGE) {
                        recievedMessage.isVisible = false
                        recieveImage.isVisible = true
                        recieveVideo.isVisible = false
                        Glide.with(root).load(chat.message).into(recieveImage)
                    }
                    else if (chat.type == Constants.MESSAGE_TYPE_VIDEO){
                        recievedMessage.isVisible = false
                        recieveImage.isVisible = false
                        recieveVideo.isVisible = true
                        recieveVideo.setVideoURI(Uri.parse(chat.message))
                        recieveVideo.setOnClickListener(){
                            if(!recieveVideo.isPlaying){
                                recieveVideo.start()
                            }
                        }

                    }
                    recieveTime.text = Utils.convertMillisToTime(chat.timeStamp)

                }

                root.setOnLongClickListener(){
                    onActionClicked(chat)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
//        return super.getItemCount()
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = RowChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }


    // Function to set animation
    private fun setAnimation(view: View, position: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.water_drop_animation)
        view.startAnimation(animation)
    }

    // Helper function to clear animation when needed
    fun clearAnimation(view: View) {
        view.clearAnimation()
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)

        setAnimation(holder.itemView, position)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<ChatMessageModel>() {
        override fun areItemsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }
        override fun areContentsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
            return oldItem == newItem
        }
    }
}