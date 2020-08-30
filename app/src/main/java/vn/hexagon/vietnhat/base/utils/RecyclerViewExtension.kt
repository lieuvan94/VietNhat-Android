package vn.hexagon.vietnhat.base.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/*
 * Create by VuNBT on 2019-11-05 
 */
class RecyclerViewExtension {

    /**
     * Use for control smooth scrolling with recycler view using vertical scroll
     * and horizontal scroll
     *
     */
    fun RecyclerView.smoothCrossScrolling() {
        this.addOnItemTouchListener(
            object: RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onInterceptTouchEvent(rv: RecyclerView, e:
                MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_DOWN &&
                        rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                        rv.stopScroll()
                    }
                    return false
                }
                override fun onRequestDisallowInterceptTouchEvent(
                    disallowIntercept: Boolean) {}
            })
    }
}