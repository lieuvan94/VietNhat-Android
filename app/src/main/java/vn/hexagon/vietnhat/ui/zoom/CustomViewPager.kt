package vn.hexagon.vietnhat.ui.zoom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.lang.IllegalArgumentException

/*
 * Create by VuNBT on 2020-01-09 
 */
class CustomViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?
) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e:IllegalArgumentException) {
            false
        }
    }
}