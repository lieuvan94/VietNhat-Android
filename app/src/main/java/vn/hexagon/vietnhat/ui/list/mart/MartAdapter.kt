package vn.hexagon.vietnhat.ui.list.mart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.data.model.mart.Mart
import vn.hexagon.vietnhat.data.remote.NetworkState

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
 * Create on：2019-08-31
 * =====================================================
 */
class MartAdapter(private val context: Context?): PagedListAdapter<Mart, RecyclerView.ViewHolder>(MovieDiffCallback()) {
    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
//        if (viewType == MOVIE_VIEW_TYPE) {
            return MovieItemViewHolder(
                layoutInflater.inflate(
                    R.layout.layout_item_list_service_translator,
                    parent,
                    false
                )
            )
//        }
//        else {
//            return NetworkStateItemViewHolder(
//                layoutInflater.inflate(
//                    R.layout.network_state_item,
//                    parent,
//                    false
//                )
//            )
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            context?.let { (holder as MovieItemViewHolder).bind(getItem(position), it) }
        }
//        else {
//            (holder as NetworkStateItemViewHolder).bind(networkState)
//        }
    }

    private fun hasExtraRow() : Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
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
        class MovieDiffCallback : DiffUtil.ItemCallback<Mart>() {
            override fun areItemsTheSame(oldItem: Mart, newItem: Mart): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Mart, newItem: Mart): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mart: Mart?, context: Context) {
        }
    }
}