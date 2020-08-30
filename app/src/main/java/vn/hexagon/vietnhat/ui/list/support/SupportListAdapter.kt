package vn.hexagon.vietnhat.ui.list.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.news.News
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.LayoutItemListServiceSupportBinding
import vn.hexagon.vietnhat.databinding.NetworkStateItemBinding
import vn.hexagon.vietnhat.ui.list.PostListViewModel

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-09-26
 * =====================================================
 */
class SupportListAdapter (private val viewModel: PostListViewModel,
                          private val onClick: (String) -> Unit): BaseAdapter<News>(ListDiffCallback()) {

    // Network state
    private var networkState: NetworkState? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return if (viewType == Constant.LIST_ITEM_TYPE) {
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_item_list_service_support,
                parent,
                false
            )
        } else {
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.network_state_item,
                parent,
                false
            )
        }
    }


    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is LayoutItemListServiceSupportBinding -> {
                binding.position = position
                binding.viewmodel = viewModel
                binding.previewImg.clipToOutline = true
                // Handle onClick item
                binding.supportListItemArea.setOnClickListener {
                    onClick(getItem(position).id)
                }
            }
            is NetworkStateItemBinding -> {
                binding.viewmodel = viewModel
                binding.isShowProgress =
                    networkState != null && networkState == NetworkState.LOADING
                if (networkState != null && networkState == NetworkState.ERROR) {
                    binding.isShowErrMessage = true
                    binding.message = networkState!!.msg
                } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                    binding.isShowErrMessage = true
                    binding.message = networkState!!.msg
                } else {
                    binding.isShowErrMessage = false
                }
            }
        }
    }

    /**
     * Return network state status if have more row
     *
     * @return
     */
    private fun hasExtraRow() : Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    /**
     * Get item count
     *
     * @return items count
     */
    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    /**
     * Get view type
     *
     * @param position
     * @return network/item type
     */
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            Constant.NETWORK_ITEM_TYPE
        } else {
            Constant.LIST_ITEM_TYPE
        }
    }

    /**
     * Handle network state with whole screen progressBar
     *
     * @param newNetworkState
     */
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
        class ListDiffCallback : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }
}