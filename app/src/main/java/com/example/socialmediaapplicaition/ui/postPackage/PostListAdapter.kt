package com.example.socialmediaapplicaition.ui.postPackage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapplicaition.R
import com.example.socialmediaapplicaition.databinding.LayoutPostBinding
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.Utils



class PostListAdapter(
    private val onPostClicked: (Post) -> Unit,
    private val onPostLiked: (Post) -> Unit,
    private val onPostOwner: (Post) ->Unit,
    private val onPostSaved:(Post,String) ->Unit,
    private val userId: String
) : ListAdapter<Post, PostListAdapter.PostViewHolder>(ComparatorDiffUtil()) {

    inner class PostViewHolder(private val binding: LayoutPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isLiked = false
        private var isSaved = false
        private var likeCount = 0
        fun bind(post: Post) {

            likeCount = post.likedBy.size
            binding.apply {

                isLiked = post.likedBy.contains(userId)
                isSaved = post.savedBy.contains(userId)
                // Bind your data to the views here
                userName.text = post.createdBy.name
                Glide.with(root.context).load(post.createdBy.profile).placeholder(R.drawable.ic_default_person).into(userImage)
                Glide.with(root.context).load(post.imageUrl).into(userImagePost)

                totalLike.text = "${likeCount} liked this post"
                captionName.text = post.createdBy.name
                userText.text = post.text

                postTime.text = Utils.getTimeAgo(post.createdAt)

                Log.d("checkingSize_", isLiked.toString())

                likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if (isLiked) R.drawable.liked else R.drawable.fav_unlike
                    )
                )

                bookmarkedButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if(isSaved) R.drawable.bookmarked else R.drawable.not_bookmarked
                    )
                )

                userImagePost.setOnClickListener() { onPostClicked(post) }

                likeButton.setOnClickListener {
                    isLiked = !isLiked
                    likeButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context, if (isLiked) {
                                likeCount++
                                totalLike.text = "$likeCount liked this post"
                                R.drawable.liked
                            } else {
                                likeCount--
                                totalLike.text = "$likeCount liked this post"
                                R.drawable.fav_unlike
                            }
                        )
                    )
                    onPostLiked(post)
                }

                bookmarkedButton.setOnClickListener(){
                   isSaved = !isSaved
                    bookmarkedButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context, if (isSaved) {
                                onPostSaved(post,Constants.SAVE)
                                R.drawable.bookmarked

                            } else {
                                onPostSaved(post,Constants.REMOVE)
                                R.drawable.not_bookmarked
                            }
                        )
                    )

                }

                if(post.comments.size>0){

                    commentBox.isVisible=true
                    Glide.with(root).load(post.comments[0].personProfile).into(commentPersonImage)
                    personName.text = post.comments[0].personName
                    commentText.text = post.comments[0].comment

                }

                userImage.setOnClickListener(){
                    onPostOwner(post)
                }

                userName.setOnClickListener(){
                    onPostOwner(post)
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