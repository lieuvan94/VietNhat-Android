package vn.hexagon.vietnhat.base.ui

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by NhamVD on 2019-08-25.
 */
open class BaseDiffUtil<T> : DiffUtil.ItemCallback<T>() {
  override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

  override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}