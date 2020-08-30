package vn.hexagon.vietnhat.binding

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import vn.hexagon.vietnhat.R
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject constructor(val context: Context) {
  private val requestOptions = RequestOptions().apply {
    placeholder(R.drawable.ic_placeholder_small)
    error(R.drawable.ic_placeholder_small)
    centerInside()
  }

  private val requestOptionsAvatar = RequestOptions().apply {
    placeholder(R.drawable.ic_ava_nodata)
    error(R.drawable.ic_ava_nodata)
    centerCrop()
  }


  @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
  fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
    Glide.with(context).load(url).apply(requestOptions)
      .listener(object : RequestListener<Drawable?> {
        override fun onLoadFailed(
          e: GlideException?,
          model: Any?,
          target: Target<Drawable?>?,
          isFirstResource: Boolean
        ): Boolean {
          imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
          return false
        }

        override fun onResourceReady(
          resource: Drawable?,
          model: Any?,
          target: Target<Drawable?>?,
          dataSource: DataSource?,
          isFirstResource: Boolean
        ): Boolean {
          imageView.scaleType = ImageView.ScaleType.FIT_XY
          return false
        }

      }).into(imageView)
  }

  @BindingAdapter(value = ["imageAvatar"], requireAll = false)
  fun bindImageAvatar(imageView: ImageView, url: String?) {
    Glide.with(context).load(url).apply(requestOptionsAvatar).into(imageView)
  }

 /* @BindingAdapter(
    value = ["imageUrl1", "imageRequestListener1", "errorDrawable", "isCircleCrop"],
    requireAll = false
  )
  fun bindImageWithOptions(
    imageView: ImageView,
    url: String?,
    listener: RequestListener<Drawable?>?,
    errorDrawable: Int? = null,
    isCircleCrop: Boolean = false
  ) {
    val glide = Glide.with(context).load(url).listener(listener)
    errorDrawable?.let { resId ->
      glide.placeholder(resId)
      glide.error(resId)
    }
    if (isCircleCrop) {
      glide.apply(RequestOptions.circleCropTransform())
    }
    glide.into(imageView)
  }*/
}

