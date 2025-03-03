package com.ganesh.hilt.firebase.livechat.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.adapter.MessageListAdapter
import com.ganesh.hilt.firebase.livechat.utils.GsonUtils
import com.ganesh.hilt.firebase.livechat.utils.formatDateTimeFromMillis
import com.ganesh.hilt.firebase.livechat.utils.toReadableDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private var firstVisiblePosition: Int = 0
    private var lastVisibleItemPosition: Int = 0
    private var isFirstTime = true
    private var _isUserAtBottom: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = true
    }
    private var isUserAtBottom: LiveData<Boolean> = _isUserAtBottom

    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var currentUserData: User
    private lateinit var receiverUserData: User
    private val messageListAdapter by lazy {
        MessageListAdapter(this@ChatActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeView()
        setupObservers()
        setupKeyboardListener()
    }

    private fun initializeView() {
        with(binding) {
            val receiver = intent?.getStringExtra("receiverUserData").toString()
            receiverUserData = GsonUtils.jsonToModel(receiver, User::class.java)
            rvChats.adapter = messageListAdapter

            tvUserName.text = receiverUserData.name
            Glide.with(ivProfilePic).load(receiverUserData.avatarImagePath)
                .placeholder(R.drawable.ic_profile).into(ivProfilePic)

            etMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    etMessage.error = null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            ivSend.setOnClickListener {
                val message = etMessage.text.toString()
                if (message.trim().isEmpty()) {
                    etMessage.error = "Enter Message"
                    return@setOnClickListener
                }

                if (messageListAdapter.itemCount > 0) {
                    rvChats.smoothScrollToPosition(messageListAdapter.itemCount - 1)
                }
                chatViewModel.sendMessage(currentUserData, receiverUserData, message)

                etMessage.setText("")
//                etMessage.hideKeyboard()
            }

            ivScrollToBottom.setOnClickListener {
                if (messageListAdapter.itemCount > 0) {
                    rvChats.smoothScrollToPosition(messageListAdapter.itemCount - 1)
                }
            }

            rvChats.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var animationStarted = false
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    // Check if the user is at the bottom
                    _isUserAtBottom.value = lastVisibleItemPosition == totalItemCount - 1
                    ivScrollToBottom.isVisible = !(isUserAtBottom.value ?: true)

                    if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                        val topMessage = messageListAdapter.messageList[firstVisiblePosition]
                        val newDate = topMessage.timestamp.toReadableDate()
                        Log.d("TAG_date", "onScrolled: $newDate")
                        tvDateTime.text = newDate // Update your floating date header

                        if (!animationStarted && !tvDateTime.isVisible && firstVisiblePosition != 0) {
                            showDateTime()
                        }
                        if (firstVisiblePosition == 0) {
                            tvDateTime.isVisible = false
                        }

                        hideDateTimeHandler.removeCallbacks(hideDateTimeRunnable)
                        hideDateTimeHandler.postDelayed(hideDateTimeRunnable, 2500)
                    }
                }

                private fun showDateTime() {
                    animationStarted = true
                    tvDateTime.isVisible = true
                    val animation =
                        AnimationUtils.loadAnimation(this@ChatActivity, R.anim.top_bottom)
                    binding.tvDateTime.startAnimation(animation)

                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            animationStarted = false
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }
                    })
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        userDetailViewModel.isUserDataAvailable()
        userDetailViewModel.setReceiverID(receiverUserData.uid)
        userDetailViewModel.listenForUserStatus(receiverUserData.uid)

        userDetailViewModel.myUserProfile.observe(this) { result ->
            result.onSuccess {
                currentUserData = it
                messageListAdapter.setCurrentUserId(currentUserData.uid)
            }.onFailure {}
        }

        chatViewModel.getMessages(receiverUserData.uid) {
            Log.d("TAG_message", "setupObservers: " + it.size)
            messageListAdapter.updateUserList(it)
            binding.tvDateTime.isVisible = it.isNotEmpty()

            if (isFirstTime || isUserAtBottom.value!!) {
                if (messageListAdapter.itemCount > 0) {
                    binding.rvChats.scrollToPosition(it.size - 1)
                }
                isFirstTime = true
            }
        }

        isUserAtBottom.observe(this) {
            if (it) {
                chatViewModel.updateMessageReadStatus(
                    receiverUserData.uid, 2, messageListAdapter.messageList
                )
            }
        }

        // Observe User Status Changes
        userDetailViewModel.userStatus.observe(this) { userStatus ->
            Log.d("UserStatus", "User is ${GsonUtils.modelToJson(userStatus)}")
            if (userStatus.lastSeen > 0) {
                binding.tvStatus.isVisible = true

                if (userStatus.status.equals("online", true)) {
                    if (userStatus.typing) {
                        binding.tvStatus.text = "Typing..."
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.themeColor
                            )
                        )
                    } else {
                        binding.tvStatus.text = "Online"
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.subTextColor
                            )
                        )
                    }
                } else {
                    binding.tvStatus.text =
                        "Last Seen ${userStatus.lastSeen.formatDateTimeFromMillis()}"
                    binding.tvStatus.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.subTextColor
                        )
                    )
                }
            } else {
                binding.tvStatus.isVisible = false
            }
        }
    }

    private fun setupKeyboardListener() {
        val rootView = window.decorView.rootView

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            // Simulate typing event
            userDetailViewModel.setTypingStatus(imeVisible)

            if (imeVisible) {
                binding.rvChats.scrollToPosition(lastVisibleItemPosition)
            }

            // Consume the IME insets properly
            insets.toWindowInsets()?.let {
                ViewCompat.onApplyWindowInsets(view, WindowInsetsCompat.CONSUMED)
            }

            insets
        }
    }

    private val hideDateTimeHandler by lazy { Handler(mainLooper) }
    private var hideDateTimeRunnable: Runnable = Runnable {
        val animation = AnimationUtils.loadAnimation(this, R.anim.bottom_top)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.tvDateTime.isVisible = false
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        binding.tvDateTime.startAnimation(animation)
    }
}