package vn.hexagon.vietnhat.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.network_state_item.view.*
import kotlinx.android.synthetic.main.post_list_item_layout.view.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.data.model.translator.Translator
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.data.remote.Status
import vn.hexagon.vietnhat.databinding.PostListItemLayoutBinding



/**
 * Created by VuNBT on 9/16/2019.
 */
class PostListAdapter(private val context: Context?): PagedListAdapter<Translator, RecyclerView.ViewHolder>(PostDiffCallback()) {

    val POST_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    // Network state
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            POST_VIEW_TYPE -> {
                val binding = DataBindingUtil
                    .inflate<PostListItemLayoutBinding>(
                        layoutInflater,
                        R.layout.post_list_item_layout,
                        parent,
                        false
                    )
                return PostItemViewHolder(binding)
            }
            NETWORK_VIEW_TYPE -> {
                val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
                return NetworkStateViewHolder(view)
            }
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            POST_VIEW_TYPE -> {
                getItem(position)?.let { (holder as PostItemViewHolder).bind(it) }
            }
            NETWORK_VIEW_TYPE -> {
                (holder as NetworkStateViewHolder).bindState(networkState)
            }
        }
    }

    private fun hasExtraRow() : Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        println("item count: $itemCount --- $position")
        return if (hasExtraRow() && position == itemCount - 1) {
            println("item network state:")
            NETWORK_VIEW_TYPE
        } else {
            println("item type post:")
            POST_VIEW_TYPE
        }
    }

    class PostItemViewHolder(private val binding: PostListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:Translator) {
//            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }

    inner class NetworkStateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindState(state: NetworkState?) {
            if (state != null && state.status == Status.RUNNING) {
                itemView.footerProgress.visibility = View.VISIBLE
                itemView.footerErrMsg.visibility = View.GONE
            } else if (state != null && state.status == Status.FAILED) {
                itemView.footerProgress.visibility = View.GONE
                itemView.footerErrMsg.visibility = View.VISIBLE
                itemView.footerErrMsg.text = state.msg
            } else {
                itemView.footerProgress.visibility = View.GONE
                itemView.footerErrMsg.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {                                          // If hadExtraRow = true & hasExtraRow = false
                notifyItemRemoved(super.getItemCount())                 // Remove last item(progressBar)
            } else {                                                    // hadExtraRow = false & hasExtraRow = true
                notifyItemInserted(super.getItemCount())                // add ProgressBar at the end
            }
        } else if (hasExtraRow && previousState != newNetworkState) {   // If hasExtraRow = true & hadExtraRow = true
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        class PostDiffCallback : DiffUtil.ItemCallback<Translator>() {
            override fun areItemsTheSame(oldItem: Translator, newItem: Translator): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Translator, newItem: Translator): Boolean {
                return oldItem == newItem
            }
        }
    }
}