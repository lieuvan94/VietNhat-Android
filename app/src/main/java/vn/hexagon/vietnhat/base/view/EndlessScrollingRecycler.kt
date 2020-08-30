package vn.hexagon.vietnhat.base.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.hexagon.vietnhat.base.utils.DebugLog

/*
 * Create by VuNBT on 2019-09-24 
 */
abstract class EndlessScrollingRecycler(private val linearLayoutManager: LinearLayoutManager):
    RecyclerView.OnScrollListener() {
    private var previousCount = 0           // Total items before load more
    private var totalCount = 0              // Total items count
    private var visibleItemCount = 0        // Visible item count
    private var currentPage = 1             // Current page to load (default = 1)

    /**
     * Handle on scrolling recycler view
     *
     * @param recyclerView
     * @param dx
     * @param dy
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            visibleItemCount = recyclerView.childCount
            totalCount = linearLayoutManager.itemCount
            previousCount = linearLayoutManager.findFirstVisibleItemPosition()
            /*if (isLoading) {
                if (totalCount > previousCount) {
                    isLoading = false
                    previousCount = totalCount
                }
            }
            if (!isLoading && (totalCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached
                currentPage++
                onLoadMore(currentPage)
                isLoading = true
            }*/

            if ((visibleItemCount + previousCount) >= totalCount) {
                DebugLog.e("COCA: " + visibleItemCount.toString() + "-" + previousCount.toString() + "-" + totalCount.toShort())
                currentPage++
                onLoadMore(currentPage)
            }
        }
    }

    /**
     * Do load more
     *
     * @param currentPage
     */
    abstract fun onLoadMore(currentPage: Int)
}