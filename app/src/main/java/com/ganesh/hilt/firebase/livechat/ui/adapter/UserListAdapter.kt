package com.ganesh.hilt.firebase.livechat.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ItemChatListBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.activity.ChatActivity
import com.google.gson.Gson

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

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.tvUserName.text = user.name

        Log.d("TAG_avatarImagePath", "onBindViewHolder: " + user.avatarImagePath)

        Glide.with(holder.binding.ivProfilePic).load(user.avatarImagePath)
            .placeholder(R.drawable.ic_profile).into(holder.binding.ivProfilePic)

        holder.binding.llChatData.isVisible = false

        holder.itemView.setOnClickListener {
            val intent = Intent(baseActivity, ChatActivity::class.java)
            intent.putExtra("receiverUserData", Gson().toJson(user))
            baseActivity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size

    /** Update list using DiffUtil */
    fun updateUserList(newList: List<User>) {
        val diffCallback = UserDiffCallback(userList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        userList.clear()
        userList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

