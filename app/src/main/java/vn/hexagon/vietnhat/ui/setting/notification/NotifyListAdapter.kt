package vn.hexagon.vietnhat.ui.setting.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.data.model.notification.Notifications
import vn.hexagon.vietnhat.databinding.ItemLayoutSettingNotifyBinding

/*
 * Create by VuNBT on 2019-09-30 
 */
class NotifyListAdapter(private val viewModel: NotifyViewModel,
                        private val onClick: (String) -> Unit): BaseAdapter<Notifications>(ListDiffCallback()) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_layout_setting_notify,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is ItemLayoutSettingNotifyBinding -> {
                binding.viewmodel = viewModel
                binding.position = position
                binding.notifyItemArea.setOnClickListener {
                    onClick(getItem(position).id)
                }
            }
        }
    }

    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<Notifications>() {
            override fun areItemsTheSame(oldItem: Notifications, newItem: Notifications): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Notifications, newItem: Notifications): Boolean {
                return oldItem == newItem
            }
        }
    }
}