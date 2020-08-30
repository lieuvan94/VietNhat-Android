package vn.hexagon.vietnhat.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.data.model.product.Product
import vn.hexagon.vietnhat.databinding.ProductDetailItemLayoutBinding
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil

/*
 * Create by VuNBT on 2019-10-04 
 */
class ProductDetailAdapter(private val items:ArrayList<Product>): BaseAdapter<Product>(ListDiffCallback()) {
    // Product list
    var list = ArrayList<Product>()
    // Expand more item or not(default = false)
    var isRevealAllItem: Boolean = false

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_detail_item_layout,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when (binding) {
            is ProductDetailItemLayoutBinding -> {
                binding.productTitle.text = list[position].name
                binding.productPrice.processHandlePrice(position)
                Glide.with(binding.productImg).load(list[position].img).into(binding.productImg)
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
            text = resources.getString(
                R.string.dynamic_money_unit,
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
    fun insertItem(product: List<Product>) {
        this.list = items
        list.addAll(product)
        notifyDataSetChanged()
    }

    /**
     * Clear all data
     *
     */
    fun clearAllItem() {
        this.list = items
        list.clear()
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