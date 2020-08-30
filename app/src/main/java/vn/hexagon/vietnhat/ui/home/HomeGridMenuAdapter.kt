package vn.hexagon.vietnhat.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.hexagon.vietnhat.R

/**
 * Created by VuNBT on 8/15/2019.
 */
class HomeGridMenuAdapter(private val context: Context,
                          private val menuList:Array<String>,
                          private val menuIconList:List<Int>,
                          private val onClick: (Int) -> Unit) : RecyclerView.Adapter<HomeGridMenuAdapter.GridMenuViewHolder>() {

    /**
     * Create View Holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home_grid_menu_item, parent, false)
        return GridMenuViewHolder(view)
    }

    /**
     * Return size of menu list
     */
    override fun getItemCount(): Int {
        return menuList.size
    }

    /**
     * Bind data for each itemView
     */
    override fun onBindViewHolder(holder: GridMenuViewHolder, position: Int) {
        holder.menuTitle.text = menuList[position]
        holder.menuIcon.setImageDrawable(ContextCompat.getDrawable(context.applicationContext, menuIconList[position]))
        if (position == menuList.lastIndex && menuList.size > 5) {
            holder.itemArea.background = ContextCompat.getDrawable(context.applicationContext, R.drawable.circle_menu_pink_bg)
        } else {
            holder.itemArea.background = ContextCompat.getDrawable(context.applicationContext, R.drawable.circle_menu_white_bg)
        }
        holder.itemView.setOnClickListener { onClick(position) }
    }

    /**
     * Declare items
     */
    class GridMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuTitle: TextView = itemView.findViewById(R.id.home_grid_menu_title)
        val menuIcon: ImageView = itemView.findViewById(R.id.home_grid_menu_icon)
        val itemArea : RelativeLayout = itemView.findViewById(R.id.home_grid_menu_background)
    }
}