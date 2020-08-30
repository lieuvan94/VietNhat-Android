package vn.hexagon.vietnhat.base.ui

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import vn.hexagon.vietnhat.R

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
</V></T> */

abstract class BasePagedListAdapter<T, V : ViewDataBinding>(
  diffCallback: BaseDiffUtil<T>
) : PagedListAdapter<T, BaseViewHolder<V>>(diffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<V> {
    val binding = createBinding(parent)
    return BaseViewHolder(binding)
  }

  override fun onBindViewHolder(holder: BaseViewHolder<V>, position: Int) {
    (holder as BaseViewHolder<*>).binding.root.setTag(R.string.position, position)
    getItem(position)?.let { item -> bind(holder.binding, item) }
    holder.binding.executePendingBindings()
  }

  protected fun setList(list: PagedList<*>?) {
    submitList(list as PagedList<T>?)
  }

  protected abstract fun createBinding(parent: ViewGroup): V

  protected abstract fun bind(binding: V, item: T)
}
