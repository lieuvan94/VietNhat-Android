package vn.hexagon.vietnhat.ui.chat

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_chat_detail.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.ChatActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.databinding.FragmentChatDetailBinding
import vn.hexagon.vietnhat.ui.chat.adapter.ChatDetailAdapter
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-09-29.
 */
class ChatDetailFragment : MVVMBaseFragment<FragmentChatDetailBinding, ChatDetailViewModel>() {
  private val arg: ChatDetailFragmentArgs by navArgs()

  private val actionBar: ChatActionBar? by lazy {
    baseActionBar as? ChatActionBar
  }
  @Inject
  lateinit var preferences: SharedPreferences
  private val userId: String by lazy {
    preferences.getString(
      getString(vn.hexagon.vietnhat.R.string.variable_local_user_id),
      Constant.BLANK
    )
  }

  private lateinit var chatDetailViewModel: ChatDetailViewModel
  private var chatAdapter: ChatDetailAdapter? = null

  override fun isShowActionBar(): View? = ChatActionBar(activity)

  override fun isShowBottomNavigation(): Boolean = false

  override fun isActionBarOverlap(): Boolean = false

  override fun getLayoutId(): Int = vn.hexagon.vietnhat.R.layout.fragment_chat_detail

  override fun getBindingVariable(): Int = BR.viewmodel

  override fun getBaseViewModel(): ChatDetailViewModel {
    chatDetailViewModel =
      ViewModelProviders.of(this, viewModelFactory).get(ChatDetailViewModel::class.java)
    return chatDetailViewModel
  }

  override fun initData(argument: Bundle?) {
    chatDetailViewModel.detailChatLiveData.observe(viewLifecycleOwner, Observer { pair ->
      pair.second?.let {
        chatAdapter?.setData(pair.second)
        if (pair.first) {
          chatAdapter?.itemCount?.let { position ->
            chatDetailRecycler.smoothScrollToPosition(position)
//          chatDetailRecycler.smoothSnapToPosition(position)
          }
        }
      }
    })

    chatDetailViewModel.getHistoryChat(userId, arg.userId)
    chatDetailViewModel.subIncomingMessage(userId, arg.userId)
  }

  override fun initView() {
    // Init action bar
    actionBar?.apply {
      arg.userName?.let { name ->
        chatTitleText = name
      }
      arg.userAvatar?.let { avatar ->
        avatarUrl = avatar
      }
      onClickRightButton = {
        arg.userPhone?.let { phone ->
          val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
          }
          activity?.let { context ->
            if (intent.resolveActivity(context.packageManager) != null) {
              startActivity(intent)
            }
          }
        }
      }
    }

    chatDetailRecycler.apply {
      setHasFixedSize(true)
      val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
      manager.stackFromEnd = true
      layoutManager = manager
      itemAnimator = DefaultItemAnimator()
      chatAdapter = ChatDetailAdapter(userId)
      adapter = chatAdapter

      addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        if (bottom < oldBottom) {
          adapter?.itemCount?.let { pos -> smoothScrollToPosition(pos) }
        }
      }
    }
  }

  override fun initAction() {
    inputMessageChatDetailEdit.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_SEND) {
        chatDetailViewModel.sendMessage(
          userId,
          arg.userId,
          inputMessageChatDetailEdit.text.toString()
        )
        inputMessageChatDetailEdit.setText("")
        true
      } else {
        false
      }
    }

    sendMessageChatDetailButton.setOnClickListener {
      if (inputMessageChatDetailEdit.text.trim().isNotEmpty()) {
        chatDetailViewModel.sendMessage(
          userId,
          arg.userId,
          inputMessageChatDetailEdit.text.toString()
        )
        inputMessageChatDetailEdit.setText("")
      }
    }
  }
}