package vn.hexagon.vietnhat.ui.gallery

import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.remove
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_gallery_custom_row.view.*
import vn.hexagon.vietnhat.R

/*
 * Create by VuNBT on 2020-01-06 
 */
class GalleryAdapter(private val context: Context,
                     private val onSelectItem:(String, Boolean) -> Unit) : ListAdapter<String, GalleryAdapter.ItemViewHolder>(
    TaskDiffCallback()
) {

    // List images
    private var mList = ArrayList<String>()
    // List sparse boolean
    val mSparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.layout_gallery_custom_row, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    companion object {
        class TaskDiffCallback : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * Insert list images
     *
     * @param list
     */
    fun insertList(list: ArrayList<String>) {
        mList = list
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(uri: String) {
            itemView.galleryItemCheckbox.setOnClickListener {
                Log.d("OnClick", "$adapterPosition")
                if (!mSparseBooleanArray.get(adapterPosition, false)) {
                    itemView.galleryItemCheckbox.isChecked = true
                    mSparseBooleanArray.put(adapterPosition, true)
                    onSelectItem(uri, true)
                } else {
                    itemView.galleryItemCheckbox.isChecked = false
                    mSparseBooleanArray.remove(adapterPosition, true)
                    onSelectItem(uri, false)
                }
            }
            itemView.galleryItemCheckbox.isChecked = mSparseBooleanArray.get(adapterPosition, false)

            Glide.with(context).load(uri).into(itemView.galleryItem)
        }
    }
}