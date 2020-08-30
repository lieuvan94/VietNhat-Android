package vn.hexagon.vietnhat.base.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import vn.hexagon.vietnhat.R
import kotlin.math.min


/**
 * The method to expand click area of view
 */
fun View.expandClickArea(
  left: Int = 0,
  top: Int = 0,
  right: Int = 0,
  bottom: Int = 0
) {

  if (left == 0 && top == 0 && right == 0 && bottom == 0) {
    return
  }
  (this.parent as? View)?.post {
    val rect = Rect()
    this.getHitRect(rect)
    rect.left += left
    rect.top += top
    rect.right += right
    rect.bottom += bottom

    val touchDelegate = TouchDelegate(rect, this)
    if (View::class.java.isInstance(this.parent)) {
      (this.parent as View).touchDelegate = touchDelegate
    }
  }
}

fun View.enableCircleRippleAnimation() {
  val value = TypedValue()
  context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true)
  setBackgroundResource(value.resourceId)
}

fun View.enableRectangleRippleAnimation() {
  val value = TypedValue()
  context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)
  setBackgroundResource(value.resourceId)
}

fun View.removeFromParent(): View {
  (parent as? ViewGroup)?.removeView(this)
  return this
}

fun Fragment.hideSoftKeyboard() {
  activity?.hideSoftKeyboard()
}

fun Fragment.showSoftKeyboard() {
  activity?.showSoftKeyboard()
}

fun Fragment.requestOrientation(orientation: Int) {
  if (activity?.requestedOrientation != orientation) {
    activity?.requestedOrientation = orientation
  }
}

fun Activity.hideSoftKeyboard() {
  val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  //Find the currently focused view, so we can grab the correct window token from it.
  var view = currentFocus
  //If no view currently has focus, create a new one, just so we can grab a window token from it
  if (view == null) {
    view = View(this)
  }
  imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showSoftKeyboard() {
  val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  //Find the currently focused view, so we can grab the correct window token from it.
  var view = currentFocus
  //If no view currently has focus, create a new one, just so we can grab a window token from it
  if (view == null) {
    view = View(this)
  }
  imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun ViewGroup.getAllChildViews(): List<View> {
  return (0 until childCount).map { getChildAt(it) }
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
  val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
  val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
  return layoutManager.getPosition(snapView)
}

fun PackageManager.isApplicationInstalled(applicationPackageName: String): Boolean {
  return try {
    getPackageInfo(applicationPackageName, 0) ?: return false
    true
  } catch (e: PackageManager.NameNotFoundException) {
    false
  }
}

fun DialogFragment.showSafety(fragmentManager: FragmentManager, tag: String) {
  if (!isStateSaved) {
    show(fragmentManager, tag)
  }
}

fun DialogFragment.dismissSafety() {
  if (!isStateSaved) {
    dismiss()
  }
}

/**
 * Load model into ImageView as a circle image with borderSize (optional) using Glide
 *
 * @param model - Any object supported by Glide (Uri, File, Bitmap, String, resource id as Int, ByteArray, and Drawable)
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 */
fun <T> ImageView.loadCircularImage(
  model: T,
  borderSize: Float = 0F,
  borderColor: Int = Color.WHITE
) {
  Glide.with(context)
    .asBitmap()
    .load(model)
    .apply(RequestOptions.circleCropTransform())
    .into(object : BitmapImageViewTarget(this) {
      override fun setResource(resource: Bitmap?) {
        setImageDrawable(
          resource?.run {
            RoundedBitmapDrawableFactory.create(
              resources,
              if (borderSize > 0) {
                createBitmapWithBorder(borderSize, borderColor)
              } else {
                this
              }
            ).apply {
              isCircular = true
            }
          }
        )
      }
    })
}

/**
 * Create a new bordered bitmap with the specified borderSize and borderColor
 *
 * @param borderSize - The border size in pixel
 * @param borderColor - The border color
 * @return A new bordered bitmap with the specified borderSize and borderColor
 */
fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int = Color.WHITE): Bitmap {
  val borderOffset = (borderSize * 2).toInt()
  val halfWidth = width / 2
  val halfHeight = height / 2
  val circleRadius = min(halfWidth, halfHeight).toFloat()
  val newBitmap = Bitmap.createBitmap(
    width + borderOffset,
    height + borderOffset,
    Bitmap.Config.ARGB_8888
  )

  // Center coordinates of the image
  val centerX = halfWidth + borderSize
  val centerY = halfHeight + borderSize

  val paint = Paint()
  val canvas = Canvas(newBitmap).apply {
    // Set transparent initial area
    drawARGB(0, 0, 0, 0)
  }

  // Draw the transparent initial area
  paint.isAntiAlias = true
  paint.style = Paint.Style.FILL
  canvas.drawCircle(centerX, centerY, circleRadius, paint)

  // Draw the image
  paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
  canvas.drawBitmap(this, borderSize, borderSize, paint)

  // Draw the createBitmapWithBorder
  paint.xfermode = null
  paint.style = Paint.Style.STROKE
  paint.color = borderColor
  paint.strokeWidth = borderSize
  canvas.drawCircle(centerX, centerY, circleRadius, paint)
  return newBitmap
}

/**
 * Set dimensions with layout params
 *
 * @param width
 * @param height
 * @return view with new layout params
 */
fun View.setDimensions(width:Int, height:Int): View {
  val params = this.layoutParams
  params.width = width
  params.width = height
  this.layoutParams = params
  return this
}

fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
  val smoothScroller = object: LinearSmoothScroller(this.context) {
    override fun getVerticalSnapPreference(): Int {
      return snapMode
    }

    override fun getHorizontalSnapPreference(): Int {
      return snapMode
    }
  }
  smoothScroller.targetPosition = position
  layoutManager?.startSmoothScroll(smoothScroller)
}

/**
 * Custom Spinner with static array
 *
 * @param context
 * @param textArrayResId
 * @param rowLayoutRes
 * @param popupLayoutRes
 */
fun Spinner.applyCustomSpinner(context: Context, @ArrayRes textArrayResId: Int, @LayoutRes rowLayoutRes: Int, @LayoutRes popupLayoutRes: Int) {
  // Create an ArrayAdapter using the string array and a default spinner layout
  ArrayAdapter.createFromResource(
    context,
    textArrayResId,
    rowLayoutRes
  ).also { adapter ->
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(popupLayoutRes)
    // Apply the adapter to the spinner
    this.adapter = adapter
  }
}

/**
 * Custom spinner with dynamic list
 *
 * @param context
 * @param rowLayoutRes
 * @param textArrayResId
 * @param popupLayoutRes
 */
fun Spinner.applyCustomSpinner(context: Context,
                               @LayoutRes rowLayoutRes: Int,
                               @NonNull textArrayResId: List<Any>,
                               @LayoutRes popupLayoutRes: Int) {
  // Create an ArrayAdapter using the string array and a default spinner layout
  ArrayAdapter(
    context,
    rowLayoutRes,
    textArrayResId
  ).also { adapter ->
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(popupLayoutRes)
    // Apply the adapter to the spinner
    this.adapter = adapter
  }
}