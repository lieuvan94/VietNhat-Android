package vn.hexagon.vietnhat.ui.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.EditText
import com.hbb20.CountryCodePicker
import vn.hexagon.vietnhat.constant.Constant
import java.io.File

/*
 * Create by VuNBT on 2019-09-27 
 */

enum class TYPE {
    CONTENT,
    ROAD,
    ADDRESS,
    TITLE
}

object WindyConvertUtil {
    /** pairs serviceId & serviceName */
    private val mapServices = hashMapOf(
        Constant.ADS_SERVICE_ID to Constant.ADS_SERVICE_NM,
        Constant.SUPPORT_SERVICE_ID to Constant.SUPPORT_SERVICE_NM,
        Constant.DELIVER_SERVICE_ID to Constant.DELIVER_SERVICE_NM,
        Constant.VISA_SERVICE_ID to Constant.VISA_SERVICE_NM,
        Constant.PHONE_SERVICE_ID to Constant.PHONE_SERVICE_NM,
        Constant.SPA_SERVICE_ID to Constant.SPA_SERVICE_NM,
        Constant.RESTAURANT_SERVICE_ID to Constant.RESTAURANT_SERVICE_NM,
        Constant.CAR_SERVICE_ID to Constant.CAR_SERVICE_NM,
        Constant.TRAVEL_SERVICE_ID to Constant.TRAVEL_SERVICE_NM,
        Constant.NEW_SERVICE_ID to Constant.NEW_SERVICE_NM,
        Constant.TRANSLATOR_SERVICE_ID to Constant.TRANSLATOR_SERVICE_NM,
        Constant.JOB_SERVICE_ID to Constant.JOB_SERVICE_NM,
        Constant.MART_SERVICE_ID to Constant.MART_SERVICE_NM
    )
    /** Content array */
    private val contentArray = arrayOf(
        Constant.MART_SERVICE_ID, Constant.TRANSLATOR_SERVICE_ID,
        Constant.TRAVEL_SERVICE_ID, Constant.CAR_SERVICE_ID,
        Constant.SUPPORT_SERVICE_ID, Constant.RESTAURANT_SERVICE_ID
    )
    /** Address array */
    private val addressArray = arrayOf(
        Constant.JOB_SERVICE_ID, Constant.SPA_SERVICE_ID,
        Constant.PHONE_SERVICE_ID, Constant.VISA_SERVICE_ID, Constant.ADS_SERVICE_ID
    )


    /**
     * Get service name by service ID
     *
     * @param serviceId
     * @return Service name
     */
    fun getServiceName(serviceId:String?): String {
        if (serviceId == null) return Constant.BLANK
        if (!mapServices.containsKey(serviceId)) return Constant.BLANK
        return mapServices.getValue(serviceId)
    }

    /**
     * Get service ID by service name
     *
     * @param serviceName
     * @return Service ID
     */
    fun getServiceId(serviceName:String): String {
        val reversed = mapServices.entries.associateBy({ it.value }) { it.key }
        return if (reversed.containsKey(serviceName)) reversed.getValue(serviceName) else Constant.BLANK
    }

    /**
     * Check contain service ID or not
     *
     * @param serviceId
     * @return true if correct service ID
     */
    fun getTypeString(serviceId: String): TYPE {
        return when {
            contentArray.contains(serviceId) -> TYPE.CONTENT
            addressArray.contains(serviceId) -> TYPE.ADDRESS
            TextUtils.equals(serviceId, Constant.DELIVER_SERVICE_ID) -> TYPE.ROAD
            else -> TYPE.TITLE
        }
    }

    /**
     * Get road type name by road ID
     *
     * @param roadType
     * @return Road type name
     */
    fun getRoadName(roadType: String): String {
        return if (roadType == "1") Constant.ROAD_VN_JP else Constant.ROAD_JP_VN
    }

    /**
     * Convert phone number as phone with country code
     *
     * @param countryCd
     * @param phoneEd
     * @return
     */
    fun convertPhoneNumber(countryCd: CountryCodePicker, phoneEd: EditText): String {
        if (phoneEd.text.isEmpty()) return Constant.BLANK
        val s = phoneEd.text.toString().replaceFirst("0","")
        return countryCd.selectedCountryCodeWithPlus + " " + s
    }

    /**
     * Convert phone number as phone with country code (trim)
     *
     * @param countryCd
     * @param phoneEd
     * @return
     */
    fun convertPhoneNumberTrim(countryCd: CountryCodePicker, phoneEd: EditText): String {
        if (phoneEd.text.isEmpty()) return Constant.BLANK
        val s = phoneEd.text.toString().replaceFirst("0","")
        return countryCd.selectedCountryCodeWithPlus + s
    }

    /**
     * Get phone number with format
     *
     * @return phone number without prefix
     */
    fun getPhoneNumber(phoneNm: String): String {
        return phoneNm.substringAfter(" ", phoneNm)
    }

    /**
     * Substring between
     *
     * @return string
     */
    fun getStringBetween(targetString: String, firstDelimiter: String, secondDelimiter: String): String {
        var s = targetString.substringAfter(firstDelimiter)
        s = s.substringBefore(secondDelimiter)
        return s
    }

    /**
     * Substring get country code
     *
     * @return string
     */
    fun getCountryCode(targetString: String): String {
        var s = Constant.BLANK
        if (targetString.isNotEmpty()) {
            s = targetString.substring(0, targetString.length - 9)
        }
        return s.substringAfter("+")
    }

    /**
     * Save image from camera to gallery
     *
     * @param photoPath
     */
    fun saveToGallery(context: Context, photoPath: String) {
        val galleryIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(photoPath)
        val uri = Uri.fromFile(file)
        galleryIntent.data = uri
        context.sendBroadcast(galleryIntent)
    }

    /**
     * filter numeric from string
     *
     * @param str
     * @return string numeric filtered
     */
    fun filterNumeric(str: String): String {
        return str.replace(Regex("[^0-9]"), Constant.BLANK)
    }
}