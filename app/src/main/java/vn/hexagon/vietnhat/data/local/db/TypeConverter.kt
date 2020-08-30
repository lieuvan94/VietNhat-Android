package vn.hexagon.vietnhat.data.local.db

import androidx.room.TypeConverter
import vn.hexagon.vietnhat.base.utils.DebugLog
import java.util.*

/**
 * Created by NhamVD on 2019-08-03.
 */

object TypeConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(date: Long): Date = Date(date)

    @TypeConverter
    @JvmStatic
    fun toLong(date: Date): Long = date.time

    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let { dataString ->
            dataString.split(",").map {
                try {
                    dataString.toInt()
                } catch (ex: NumberFormatException) {
                    DebugLog.e("Cannot convert $dataString to number: $ex")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }
}