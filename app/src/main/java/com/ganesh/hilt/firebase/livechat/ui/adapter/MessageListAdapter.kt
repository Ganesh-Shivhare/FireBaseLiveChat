package com.ganesh.hilt.firebase.livechat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.ganesh.hilt.firebase.livechat.databinding.ItemMessageBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.utils.formatTimeFromMillis

class MessageListAdapter(private val baseActivity: BaseActivity) :
    RecyclerView.Adapter<MessageListAdapter.UserViewHolder>() {

    var messageList = mutableListOf<ChatMessage>()
    private var currentUserID: String = ""

    fun setCurrentUserId(currentUserID: String) {
        this.currentUserID = currentUserID
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val chatMessage = messageList[position]

        with(holder.binding) {
            if (chatMessage.senderId == currentUserID) {
                clReceiver.isVisible = false
                clSender.isVisible = true

                tvSendMessage.text = chatMessage.message
                tvSendTime.text = chatMessage.timestamp.formatTimeFromMillis()

                if (chatMessage.messageRead) {
                    ivCheck.setImageResource(R.drawable.ic_checked)
                    ivCheck.setColorFilter(ContextCompat.getColor(baseActivity, R.color.themeColor))
                } else {
                    ivCheck.setImageResource(R.drawable.ic_check)
                    ivCheck.setColorFilter(ContextCompat.getColor(baseActivity, R.color.subTextColor))
                }
            } else {
                clReceiver.isVisible = true
                clSender.isVisible = false

                tvReceivedMessage.text = chatMessage.message
                tvReceivedTime.text = chatMessage.timestamp.formatTimeFromMillis()
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    /** Update list using DiffUtil */
    fun updateUserList(newList: List<ChatMessage>) {
        val diffCallback = UserDiffCallback(messageList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messageList.clear()
        messageList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class UserDiffCallback(
        private val oldList: List<ChatMessage>, private val newList: List<ChatMessage>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}


