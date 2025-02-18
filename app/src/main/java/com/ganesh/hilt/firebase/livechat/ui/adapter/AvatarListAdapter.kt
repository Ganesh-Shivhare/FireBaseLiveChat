package com.ganesh.hilt.firebase.livechat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.databinding.ItemAvatarBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity

class AvatarListAdapter(private val baseActivity: BaseActivity) :
    RecyclerView.Adapter<AvatarListAdapter.UserViewHolder>() {

    private var avatarList = mutableListOf<String>()
    private var selectedAvatar = ""
    private lateinit var result: (String) -> Unit

    inner class UserViewHolder(val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val avatarImage = avatarList[position]

        holder.itemView.isSelected = avatarImage == selectedAvatar

        Glide.with(holder.binding.ivAvatar).load(avatarImage).placeholder(R.drawable.ic_profile)
            .into(holder.binding.ivAvatar)

        holder.itemView.setOnClickListener {
            result(avatarImage)
        }
    }

    override fun getItemCount(): Int = avatarList.size

    /** Update list using DiffUtil */
    fun updateUserList(newList: List<String>) {
        val diffCallback = UserDiffCallback(avatarList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        avatarList.clear()
        avatarList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setSelectedAvatar(selectedAvatar: String) {
        this.selectedAvatar = selectedAvatar
        notifyDataSetChanged()
    }

    fun getSelectedAvatar(result: (String) -> Unit) {
        this.result = result
        notifyDataSetChanged()
    }

    inner class UserDiffCallback(
        private val oldList: List<String>,
        private val newList: List<String>
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

