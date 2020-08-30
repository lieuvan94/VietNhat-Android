package vn.hexagon.vietnhat.base.view

import android.content.Context
import android.content.res.Resources
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import vn.hexagon.vietnhat.R

/**
 * Created by VuNBT on 10/25/2019.
 */
class InfinityPager(context: Context) {
    private lateinit var mPager: ViewPager2
    private var mIndicatorCnt: Int = 0
    private lateinit var mIndicatorLayout: LinearLayout
    init {

        // Item less than 1 can not display as pager slider
//    if (indicatorCnt < 1) return
        val indicator = arrayOfNulls<ImageView>(mIndicatorCnt)
        for (i in 0 until mIndicatorCnt) {
            indicator[i] = ImageView(context)
            indicator[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    context.applicationContext,
                    R.drawable.tab_indicator_default
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            mIndicatorLayout.addView(indicator[i], params)
        }
        // Active on first index
        indicator[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                context.applicationContext,
                R.drawable.tab_indicator_selected
            )
        )
        // Sync indicator with pager
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0 until mIndicatorCnt) {
                    indicator[i]?.setImageDrawable(
                        ContextCompat
                            .getDrawable(
                                context.applicationContext,
                                R.drawable.tab_indicator_default
                            )
                    )
                    indicator[position]?.setImageDrawable(
                        ContextCompat
                            .getDrawable(
                                context.applicationContext,
                                R.drawable.tab_indicator_selected
                            )
                    )
                }
            }
        })
    }

    fun setPager(pager: ViewPager2) {
        mPager = pager
    }

    fun setIndicatorCount(indicatorCnt: Int) {
        mIndicatorCnt = indicatorCnt
    }

    fun setIndicatorLayout(layout: LinearLayout) {
        mIndicatorLayout = layout
    }
}