package vn.hexagon.vietnhat.ui.post

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.daimajia.swipe.SwipeLayout
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.data.model.product.Product
import vn.hexagon.vietnhat.databinding.ProductItemLayoutBinding
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil

/*
 * Create by VuNBT on 2019-10-01 
 */
class ProductPostAdapter(private val items:ArrayList<Product>,
                         private val onDeleteClick:(Int) -> Unit): BaseAdapter<Product>(ListDiffCallback()) {
    // Product list
    var list = ArrayList<Product>()
    // Expand more item or not(default = false)
    var isRevealAllItem:Boolean = false
    // Request options
    private val requestOptions = RequestOptions().apply {
        placeholder(R.drawable.ic_placeholder)
        error(R.drawable.ic_placeholder)
        centerInside()
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_item_layout,
            parent,
            false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is ProductItemLayoutBinding -> {
                binding.productTitle.text = list[position].name
                binding.productPrice.processHandlePrice(position)
                binding.productImg.clipToOutline = true
                Glide.with(binding.productImg).applyDefaultRequestOptions(requestOptions).load(list[position].img).listener(object :
                    RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.productImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.productImg.scaleType = ImageView.ScaleType.FIT_XY
                        return false
                    }

                }).into(binding.productImg)

                // Set mode
                binding.productSwipeLayout.showMode = SwipeLayout.ShowMode.LayDown
                binding.productBackgroundArea.setOnClickListener { onDeleteClick(position) }
                binding.productSwipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
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
     * Format price product
     *
     * @param position
     */
    private fun TextView.processHandlePrice(position: Int) {
        this.apply {
            text = resources.getString(R.string.dynamic_money_unit,
                String.format("%,d", WindyConvertUtil.filterNumeric(list[position].price).toInt()))
        }
    }

    /**
     * Item count by condition
     *
     * @return total items
     */
    override fun getItemCount(): Int {
        return if (!isRevealAllItem) list.take(5).size else items.size
    }

    /**
     * Insert product
     *
     * @param product
     */
    fun insertItem(product:Product) {
        this.list = items
        list.add(product)
        notifyDataSetChanged()
    }

    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}