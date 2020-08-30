package vn.hexagon.vietnhat.base.utils

import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout


class SingleTouchViewGroup : RelativeLayout {
  companion object {
    private const val BLOCKING_TIME = 300L
  }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  private var isBlockingTouchEvent = false

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    if (ev.action == MotionEvent.ACTION_DOWN) {
      if (isClickedInExceptionViews(ev)) return super.onInterceptTouchEvent(ev)

      val pointerCount = ev.pointerCount
      if (pointerCount >= 2) {
        return true
      }

      if (isBlockingTouchEvent) return true

      when (isBlockingTouchEvent) {
        true -> return true
        else -> {
          isBlockingTouchEvent = true
          Handler().postDelayed({
            isBlockingTouchEvent = false
          }, BLOCKING_TIME)
        }
      }
    }

    return super.onInterceptTouchEvent(ev)
  }


  private fun getCoordinateRelativeToParent(rootView: View, child: View): PointF {
    if (rootView == child) return PointF()

    val position = PointF(child.x, child.y)
    val parentPosition = getCoordinateRelativeToParent(rootView, child.parent as View)
    return PointF(
      position.x + parentPosition.x,
      position.y + parentPosition.y
    )
  }

  private fun isClickedInExceptionViews(ev: MotionEvent): Boolean {
    allowMultitouchViews().forEach { exceptView ->
      if (exceptView != null && clickedInView(ev.x, ev.y, exceptView)) {
        return true
      }
    }
    return false
  }

  private fun clickedInView(x: Float, y: Float, view: View): Boolean {
    val coordinate = getCoordinateRelativeToParent(this, view)

    if (
      x >= coordinate.x && x <= coordinate.x + view.width
      && y >= coordinate.y && y <= coordinate.y + view.height
    ) {
      return true
    }

    return false
  }

  private fun allowMultitouchViews(): List<View?> {
    return allowMultitouchViewIds().map { id ->
      findViewById<View?>(id)
    }
  }

  private fun allowMultitouchViewIds(): List<Int> {
    return listOf()
  }
}