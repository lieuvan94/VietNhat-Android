package vn.hexagon.vietnhat.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.databinding.LayoutImagesHorizontalBinding

/*
 * Create by VuNBT on 2019-10-24 
 */
class HorizontalImagesAdapter(private val onItemClick: () -> Unit) : BaseAdapter<String>(ListDiffCallback()) {

    // Limit size
    var LIMIT_SIZE = 4
    // List images
    var images = ArrayList<String>()
    // Selected position
    var mPosition = -1

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_images_horizontal,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when (binding) {
            is LayoutImagesHorizontalBinding -> {
                if (images.size > 0) {
                    val item = images[position]
                    binding.realPostPreviewImg.clipToOutline = true
                    Glide.with(binding.realPostPreviewImg).load(item)
                        .into(binding.realPostPreviewImg)
                    // Click on image
                    binding.postPreviewImgForeground.clipToOutline = true
                    binding.postPreviewImgForeground.setOnClickListener {
                        mPosition = position
                        onItemClick()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (images.size > LIMIT_SIZE) LIMIT_SIZE else images.size
    }

    /**
     * Specific limit size for pager
     *
     * @param limit
     */
    fun specificLimitSize(limit: Int) {
        LIMIT_SIZE = limit
    }

    /**
     * Add images list to current list
     *
     * @param list
     */
    fun addImages(list: ArrayList<String>) {
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Remove image by position
     *
     */
    fun removeImages() {
        images.removeAt(mPosition)
        notifyItemRemoved(mPosition)
        notifyItemRangeChanged(mPosition, images.size)
    }

    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}