package vn.hexagon.vietnhat.base.ui

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import vn.hexagon.vietnhat.R

/**
 * Created by NhamVD on 2019-08-25.
 */
abstract class BaseAdapter<T>(callback: DiffUtil.ItemCallback<T>) :
  ListAdapter<T, BaseViewHolder<ViewDataBinding>>(callback) {

  override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
    (holder as BaseViewHolder<*>).binding.root.setTag(R.string.position, position)
    bind(holder.binding, position)
    holder.binding.executePendingBindings()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    getViewHolder(parent, viewType)

  open fun getViewHolder(parent: ViewGroup, viewType: Int) =
    BaseViewHolder(createBinding(parent, viewType))

  protected abstract fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding

  protected abstract fun bind(binding: ViewDataBinding, position: Int)

  //  protected abstract fun bind(binding: ViewDataBinding, item: T)

  protected fun setList(list: List<T>) {
    submitList(list)
  }
}
