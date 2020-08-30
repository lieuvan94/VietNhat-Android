package vn.hexagon.vietnhat.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.data.model.detail.Images

/**
 * Created by VuNBT on 10/25/2019.
 */
class PagerDetailAdapter(private val context: Context,
                         private val onClickImg:(ArrayList<String>, Int) -> Unit) : RecyclerView.Adapter<DetailImageViewHolder>() {
    // List images
    var listItem:List<Images> = listOf()
    // Minimum pager size
    private val MINIMUM_SIZE = 1
    private val imgList = ArrayList<String>()

    // Request options
    private val requestOptions = RequestOptions().apply {
        placeholder(R.drawable.ic_placeholder_small)
        error(R.drawable.ic_placeholder_small)
        centerInside()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailImageViewHolder {
        return DetailImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_pager_item, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listItem.isNotEmpty()) listItem.size else MINIMUM_SIZE
    }

    override fun onBindViewHolder(holder: DetailImageViewHolder, position: Int) {
        if (listItem.isEmpty()) {
            Glide.with(context).load(R.drawable.ic_placeholder_small).into(holder.img)
        } else {
            Glide
                .with(context)
                .load(listItem[position].img)
                .fitCenter()
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.img)
            holder.img.setOnClickListener {
                onClickImg(imgList, position)
            }
        }
    }

    /**
     * Add image list
     *
     * @param list
     */
    fun setItem(list: List<Images>) {
        this.listItem = list
        list.forEach {
            imgList.add(it.img)
        }
        notifyDataSetChanged()
    }
}

class DetailImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val img: ImageView = view.findViewById(R.id.pagerItemImage)
}