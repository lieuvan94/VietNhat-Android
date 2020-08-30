package vn.hexagon.vietnhat.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import vn.hexagon.vietnhat.constant.Constant
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

/**
 * Created by DoanNH on 4/6/19.
 */
object Utils {
  private const val NUMBER_OF_MINUTE = 60
  private const val NUMBER_OF_HOUR = 24
  private const val SECOND_MILLIS = 1000
  private const val MINUTE_MILLIS = NUMBER_OF_MINUTE * SECOND_MILLIS
  private const val HOUR_MILLIS = NUMBER_OF_MINUTE * MINUTE_MILLIS
  private const val DAY_MILLIS = NUMBER_OF_HOUR * HOUR_MILLIS

  fun isValidEmailAddress(emailAddress: String): Boolean {
    val emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
    val pattern: Pattern
    // Regex for a valid email address
    // Compare the regex with the email address
    pattern = Pattern.compile(emailRegEx)
    val matcher = pattern.matcher(emailAddress)
    return matcher.find()
  }

  fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phoneRegex = "^[+]?[0-9]{10,13}\$"
    val pattern: Pattern
    // Regex for a valid phone number
    // Compare the regex with the phone number
    pattern = Pattern.compile(phoneRegex)
    val matcher = pattern.matcher(phoneNumber)
    return matcher.find()
  }

  fun blurBitmap(
    context: Context?,
    image: Bitmap,
    scale: Float = 0.2f,
    radius: Float = 1f
  ): Bitmap {
    val width = Math.round(image.width * scale)
    val height = Math.round(image.height * scale)

    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(radius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
  }

  fun getDate(dateString: String?): Date {
    if (dateString.isNullOrEmpty()) return Date(0)

    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    dateFormat.timeZone = timeZone
    return dateFormat.parse(dateString)
  }

  fun getTimeAgo(time: Date): String {
    val timeAgo = time.time
    val now = System.currentTimeMillis()
    if (timeAgo > now || timeAgo <= 0) {
      return SimpleDateFormat(
        "dd/MM",
        Locale.ENGLISH
      ).format(timeAgo).toString()
    }
    val diff = now - timeAgo
    return when {
      diff < MINUTE_MILLIS -> "Just ago"
      diff < HOUR_MILLIS -> {
        val minutes = (diff / MINUTE_MILLIS).toInt()
        "${minutes}m"
      }
      diff < DAY_MILLIS -> {
        SimpleDateFormat(
          "hh:mm a",
          Locale.ENGLISH
        ).format(timeAgo).toString()
      }
      else ->
        SimpleDateFormat(
          "dd/MM",
          Locale.ENGLISH
        ).format(time).toString()
    }
  }

  /**
   * Convert dp to px
   *
   * @param context
   * @param dp
   * @return
   */
  fun dpToPx(context: Context?, dp: Int): Int {
    val density = context?.resources?.displayMetrics?.density
    var result = -1
    density?.let {
      result = (dp.toFloat() * density).roundToInt()
    }
    return result
  }

  fun formatDateTime(time: String): String {
    if (time.isEmpty()) return Constant.EMPTY
    val formatSource = SimpleDateFormat(Constant.FORMAT_DATE_TIME)
    val date = formatSource.parse(time)
    val formatTarget = SimpleDateFormat(Constant.FORMAT_TIME_CHAT)
    return formatTarget.format(date)
  }

  inline fun <reified T> fromJson(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

  inline fun <reified T> toJson(src: T): String =
    Gson().toJson(src, object : TypeToken<T>() {}.type)

}