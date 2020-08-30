package vn.hexagon.vietnhat.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.daimajia.swipe.SwipeLayout
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.databinding.LayoutItemListFavBinding
import vn.hexagon.vietnhat.ui.list.TYPE
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil

/*
 * Create by VuNBT on 2019-09-26 
 */
class FavouriteListAdapter(private val context: Context, private val viewModel: FavoriteViewModel,
                           private val onClick: (String, String, String) -> Unit,
                           private val onDeleteClick:(Int) -> Unit): BaseAdapter<Post>(ListDiffCallback()) {

    private var list = ArrayList<Post>()

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_item_list_fav,
                parent,
                false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is LayoutItemListFavBinding -> {
                val item = list[position]
                binding.position = position
                binding.viewmodel = viewModel
                // Binding service image
                binding.previewImg.clipToOutline = true
                // Binding service name
                binding.favServiceNm.text = WindyConvertUtil.getServiceName(item.serviceId)
                // Binding service price || salary
                if (item.price == "0" && item.salary == "0") {
                    binding.favPrice.visibility = View.GONE
                } else {
                    binding.favPrice.visibility = View.VISIBLE
                    binding.favPrice.priceFormatProcess(item)
                }
                // Binding service fav content
                val finalContent = when(WindyConvertUtil.getTypeString(item.serviceId)) {
                    TYPE.CONTENT -> item.content
                    TYPE.ADDRESS -> item.address
                    TYPE.ROAD -> WindyConvertUtil.getRoadName(item.roadType)
                    TYPE.TITLE -> item.title
                }
                binding.itemLastText.text = finalContent
                // Handle go to detail
                binding.favouriteItemArea.setOnClickListener {
                    onClick(item.userId, item.id, item.serviceId)
                }
                // Set mode
                binding.favSwipeLayout.showMode = SwipeLayout.ShowMode.LayDown
                binding.favBackgroundArea.setOnClickListener { onDeleteClick(position) }
                binding.favSwipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
                    override fun onOpen(layout: SwipeLayout?) {
                    }

                    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                    }

                    override fun onStartOpen(layout: SwipeLayout?) {
                    }

                    override fun onStartClose(layout: SwipeLayout?) {
                    }

                    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                    }

                    override fun onClose(layout: SwipeLayout?) {
                    }

                })
            }
        }
    }

    /**
     * Handle display price/salary format
     *
     * @param post
     */
    private fun TextView.priceFormatProcess(post: Post) {
        val money = if (post.salary == "0") post.price else post.salary
        when (post.serviceId) {
            Constant.JOB_SERVICE_ID -> {
                when (post.salaryType) {
                    "1" -> this.processHandleFormatMoney(R.string.dynamic_money_per_hour, money)
                    "2" -> this.processHandleFormatMoney(R.string.dynamic_money_per_month, money)
                    else -> this.processHandleFormatMoney(R.string.dynamic_money_per_hour, money)
                }
            }
            Constant.TRANSLATOR_SERVICE_ID -> {
                when (post.translateType) {
                    "1" -> this.processHandleFormatMoney(R.string.dynamic_money_per_hour, money)
                    "2" -> this.processHandleFormatMoney(R.string.dynamic_money_per_month, money)
                    else -> this.processHandleFormatMoney(R.string.dynamic_money_per_hour, money)
                }
            }
            Constant.TRAVEL_SERVICE_ID -> {
                this.processHandleFormatMoney(R.string.dynamic_price_per_people, money)
            }
            else -> {
                this.processHandleFormatMoney(R.string.dynamic_money_unit, money)
            }
        }
    }

    /**
     * Process handle format money
     *
     * @param money
     */
    private fun TextView.processHandleFormatMoney(@StringRes format: Int, money: String) {
        this.apply {
            text = resources.getString(
                format,
                String.format(
                    "%,d",
                    WindyConvertUtil.filterNumeric(money).toInt()
                )
            )
        }
    }

    /**
     * Insert item to favList
     *
     * @param favList
     */
    fun insertItem(favList: ArrayList<Post>) {
        list = favList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }

}