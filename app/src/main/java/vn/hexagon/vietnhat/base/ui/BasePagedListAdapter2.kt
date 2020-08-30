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

abstract class BasePagedListAdapter2<T>(
  diffCallback: BaseDiffUtil<T>
) : PagedListAdapter<T, BaseViewHolder<ViewDataBinding>>(diffCallback) {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): BaseViewHolder<ViewDataBinding> {
    val binding = createBinding(parent, viewType)
    return BaseViewHolder(binding)
  }

  override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
    (holder as BaseViewHolder<*>).binding.root.setTag(R.string.position, position)
    bind(holder.binding, position)
    holder.binding.executePendingBindings()
  }

  protected fun setList(list: PagedList<*>?) {
    submitList(list as PagedList<T>?)
  }

  protected abstract fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding

  protected abstract fun bind(binding: ViewDataBinding, position: Int)

  //protected abstract fun bind(binding: ViewDataBinding, item: T)
}
