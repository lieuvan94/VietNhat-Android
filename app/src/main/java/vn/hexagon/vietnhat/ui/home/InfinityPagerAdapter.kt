package vn.hexagon.vietnhat.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.data.model.banner.Banner

/**
 * Created by VuNBT on 8/16/2019.
 */
class InfinityPagerAdapter : RecyclerView.Adapter<ImageViewHolder>() {

    var listItem:List<Banner> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pager_header_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.img).load(listItem[position].img).into(holder.img)
    }

    fun setItem(list: List<Banner>) {
        this.listItem = list
        notifyDataSetChanged()
    }
}

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val img = view.findViewById<ImageView>(R.id.pager_image)
}