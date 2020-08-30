package vn.hexagon.vietnhat.binding

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Created by NhamVD on 2019-08-02.
 */
object BindingAdapters {
  @JvmStatic
  @BindingAdapter("visibleGone")
  fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
  }

  @JvmStatic
  @BindingAdapter("android:padding")
  fun setPadding(view: View, padding: Int) {
    view.setPadding(
      padding,
      padding,
      padding,
      padding
    )
  }

  @JvmStatic
  @BindingAdapter("android:paddingLeft")
  fun setPaddingLeft(view: View, padding: Int) {
    view.setPadding(
      padding,
      view.paddingTop,
      view.paddingRight,
      view.paddingBottom
    )
  }

  @JvmStatic
  @BindingAdapter("android:paddingRight")
  fun setPaddingRight(view: View, padding: Int) {
    view.setPadding(
      view.paddingLeft,
      view.paddingTop,
      padding,
      view.paddingBottom
    )
  }

  @JvmStatic
  @BindingAdapter("android:paddingTop")
  fun setPaddingTop(view: View, padding: Int) {
    view.setPadding(
      view.paddingLeft,
      padding,
      view.paddingRight,
      view.paddingBottom
    )
  }

  @JvmStatic
  @BindingAdapter("android:paddingBottom")
  fun setPaddingBottom(view: View, padding: Int) {
    view.setPadding(
      view.paddingLeft,
      view.paddingTop,
      view.paddingRight,
      padding
    )
  }
}