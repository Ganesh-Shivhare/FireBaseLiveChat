package com.ganesh.hilt.firebase.livechat.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ItemChatListBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.activity.ChatActivity
import com.ganesh.hilt.firebase.livechat.utils.Debug
import com.ganesh.hilt.firebase.livechat.utils.GsonUtils
import com.ganesh.hilt.firebase.livechat.utils.formatTimeFromMillis

class UserListAdapter(private val baseActivity: BaseActivity) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private var userList = mutableListOf<User>()

    inner class UserViewHolder(val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        with(holder.binding) {

            tvUserName.text = user.name

            Debug.d("TAG_avatarImagePath", "onBindViewHolder: " + user.chatMessage.message)

            Glide.with(ivProfilePic).load(user.avatarImagePath).placeholder(R.drawable.ic_profile)
                .into(ivProfilePic)

            llChatData.isVisible = user.chatMessage.message.isNotEmpty()
            tvMessage.text = user.chatMessage.message
            tvLastSeen.text = user.chatMessage.timestamp.formatTimeFromMillis()
            tvUnreadMessageCount.text = user.unreadMessageCount.toString()

            if (user.chatMessage.senderId == currentUserID) {
                ivCheck.isVisible = true
                when (user.chatMessage.messageStatus) {
                    0 -> {
                        ivCheck.setImageResource(R.drawable.ic_check)
                        ivCheck.setColorFilter(
                            ContextCompat.getColor(
                                baseActivity, R.color.subTextColor
                            )
                        )
                    }

                    1 -> {
                        ivCheck.setImageResource(R.drawable.ic_checked)
                        ivCheck.setColorFilter(
                            ContextCompat.getColor(
                                baseActivity, R.color.subTextColor
                            )
                        )
                    }

                    2 -> {
                        ivCheck.setImageResource(R.drawable.ic_checked)
                        ivCheck.setColorFilter(
                            ContextCompat.getColor(
                                baseActivity, R.color.themeColor
                            )
                        )
                    }
                }
            } else {
                ivCheck.isVisible = false
            }
            if (user.unreadMessageCount > 0) {
                tvLastSeen.isVisible = false
                tvUnreadMessageCount.isVisible = true
            } else {
                tvLastSeen.isVisible = true
                tvUnreadMessageCount.isVisible = false
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(baseActivity, ChatActivity::class.java)
                intent.putExtra("receiverUserData", GsonUtils.modelToJson(user))
                baseActivity.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = userList.size

    /** Update list using DiffUtil */
    fun updateUserList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }

    private var currentUserID: String = ""
    fun setCurrentUserId(currentUserID: String) {
        this.currentUserID = currentUserID
        notifyDataSetChanged()
    }

}

