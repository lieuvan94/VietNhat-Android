package vn.hexagon.vietnhat.base.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BuildConfig
import vn.hexagon.vietnhat.constant.Constant
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Open gallery to choice only display image or only display video
 * @param isOpenVideo true open video, false open Image
 * @param requestCode code requestCode
 */
fun Fragment.openGallery(isOpenVideo: Boolean = false, requestCode: Int) {
    val intent: Intent
    if (isOpenVideo) {
        intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.type = "video/*"
    } else {
        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
    }

    val packageManager = this.activity?.packageManager
    if (packageManager != null && intent.resolveActivity(packageManager) != null) {
        startActivityForResult(intent, requestCode)
    } else {
        Log.e("openGallery", " The package manage is null or intent is not resolved")
    }
}

/**
 * Open gallery with multi pick images
 *
 * @param requestCode
 */
fun Fragment.multiImagesPicker(requestCode: Int) {
    val intent = Intent()
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "image/*"
    val packageManager = this.activity?.packageManager
    if (packageManager != null && intent.resolveActivity(packageManager) != null) {
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), requestCode)
    } else {
        Log.e("openGallery", " The package manage is null or intent is not resolved")
    }
}
/** Photo path */
var currentPhotoPath:String? = null
/**
 * Handle send intent take photo
 */
fun Fragment.processTakePhoto(context: Context) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(context.packageManager) != null) {
        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createFileName(context)
        } catch (ex: IOException) {
            // Error occurred while creating the File
            DebugLog.e("handleTakePhoto: $ex")
        }

        // If the File was successfully created, start send command
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, Constant.REQUEST_CAMERA)
        }
    }
}

/**
 * Create file name
 *
 * @return
 * @throws IOException
 */
@Throws(IOException::class)
private fun createFileName(context: Context): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timeStamp"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir      /* directory */
    )

    // Save a file: path for use with ACTION_VIEW intents
    currentPhotoPath = image.absolutePath
    return image
}

/**
 * Open gallery or camera
 *
 */
fun Fragment.insertImages(mode: Int) {
    activity?.let { context ->
        when(mode) {
            Constant.REQUEST_GALLERY -> {
                val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
                if (EasyPermissions.hasPermissions(context, permissionRead)) {
                    openGallery(false, Constant.REQUEST_CD_PERMISSION)
                } else {
                    EasyPermissions.requestPermissions(
                        this, "Access for storage",
                        Constant.REQUEST_CD_GRANT_PERMISSION, permissionRead
                    )
                }
            }
            Constant.REQUEST_GALLERY_MULTI_PICK -> {
                val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
                if (EasyPermissions.hasPermissions(context, permissionRead)) {
                    multiImagesPicker(Constant.REQUEST_GALLERY_MULTI_PICK)
                } else {
                    EasyPermissions.requestPermissions(
                        this, "Access for storage",
                        Constant.REQUEST_CD_MULTI_GRANT_PERMISSION, permissionRead
                    )
                }
            }
            Constant.REQUEST_CAMERA -> {
                val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
                val permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE
                val permissionCamera = Manifest.permission.CAMERA
                if (EasyPermissions.hasPermissions(context, permissionRead, permissionWrite, permissionCamera)) {
                    processTakePhoto(context)
                } else {
                    EasyPermissions.requestPermissions(
                        this, "Access for storage",
                        Constant.REQUEST_CD_GRANT_PERMISSION_CAMERA, permissionRead, permissionWrite, permissionCamera
                    )
                }
            }
        }
    }
}

/**
 * Get path file
 *
 * @param uri
 */
fun getPathFile(context: Context?, uri: Uri, pathArray: ArrayList<String>) {
    val file = File(uri.path)
    val filePath = file.path.split(":")
    val imageId = filePath[filePath.size - 1]

    val cursor = context?.contentResolver?.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        null, MediaStore.Images.Media._ID + " = ? ",
        arrayOf(imageId), null)
    if (cursor != null) {
        cursor.moveToFirst()
        val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        if (pathArray.size < 4) {
            pathArray.add(imagePath)
        }
        cursor.close()
    }
}