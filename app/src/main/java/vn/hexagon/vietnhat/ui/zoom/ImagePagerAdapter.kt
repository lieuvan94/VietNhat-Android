package vn.hexagon.vietnhat.ui.zoom

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import vn.hexagon.vietnhat.R

/*
 * Create by VuNBT on 2020-01-09 
 */
class ImagePagerAdapter(private val context: Context,
                        private val imageList: ArrayList<String>): PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imageList.size ?: 0
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(container.context)
        Glide.with(context)
            .load(imageList[position])
            .fitCenter()
            .placeholder(R.drawable.ic_placeholder_small)
            .into(photoView)
        photoView.maximumScale = 5.0f
        photoView.mediumScale = 3.0f
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        // Do nothing
    }
}