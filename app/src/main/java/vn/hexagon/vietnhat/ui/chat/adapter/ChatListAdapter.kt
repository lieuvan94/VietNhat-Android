package vn.hexagon.vietnhat.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.base.ui.BaseDiffUtil
import vn.hexagon.vietnhat.data.model.remote.ChatList
import vn.hexagon.vietnhat.databinding.ItemChatListBinding

/**
 * Created by NhamVD on 2019-09-22.
 */
class ChatListAdapter(private val onClick: (ChatList) -> Unit) : BaseAdapter<ChatList>(BaseDiffUtil()) {

  override fun createBinding(parent: ViewGroup, viewType: Int): ItemChatListBinding {
    return DataBindingUtil.inflate(
      LayoutInflater.from(parent.context),
      R.layout.item_chat_list,
      parent,
      false
    )
  }

  override fun bind(binding: ViewDataBinding, position: Int) {
    val bindingItem = binding as? ItemChatListBinding
    val item = getItem(position)
    bindingItem?.chatList = item
    bindingItem?.root?.setOnClickListener {
      item.numberUnseen = "0"
      onClick.invoke(item)
    }
  }
}