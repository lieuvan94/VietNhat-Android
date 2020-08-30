package vn.hexagon.vietnhat.ui.setting.account

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_account_info.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.databinding.FragmentAccountInfoBinding
import vn.hexagon.vietnhat.ui.dialog.DialogSelectPhotoFragment
import vn.hexagon.vietnhat.ui.dialog.PhotoSelectListener
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-09-10
 * =====================================================
 */
class AccountInfoFragment: MVVMBaseFragment<FragmentAccountInfoBinding, AccountInfoViewModel>(),
    View.OnClickListener, PhotoSelectListener, EasyPermissions.PermissionCallbacks {

    // Account Info View Model
    private lateinit var accountInfoViewModel: AccountInfoViewModel
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    private var userId: String? = Constant.BLANK
    // Request option
    private val requestOptionsAvatar = RequestOptions().apply {
        placeholder(R.drawable.ic_ava_nodata)
        error(R.drawable.ic_ava_nodata)
        centerCrop()
    }

    override fun getBaseViewModel(): AccountInfoViewModel {
        accountInfoViewModel = ViewModelProviders.of(this, viewModelFactory)[AccountInfoViewModel::class.java]
        return accountInfoViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val bindingData = getBaseViewDataBinding()
        bindingData.viewmodel = accountInfoViewModel
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id),
            Constant.BLANK)
        loadData()
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.account_title)
        leftButtonVisible = true
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_account_info

    override fun initView() {
        // Get response account info
        accountInfoViewModel.userInfoResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Get response update account info
        accountInfoViewModel.updateProfileResponse.observe(this, Observer { response ->
            getChangeProfileRequest(response)
        })
        // Get response upload avatar
        accountInfoViewModel.uploadAvatarResponse.observe(this, Observer { response ->
            getUploadAvatarResponse(response)
        })
        // Handle onClick confirm button
        accountInfoConfirmBtn.setOnClickListener(this)
        accountInfoAvatar.setOnClickListener(this)
        accountInfoAvaArea.setOnClickListener(this)
        accountInfoPassword.setOnClickListener(this)
    }

    /**
     * Response account info
     *
     * @param response
     */
    private fun getResponse(response: LoginResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch Account Info Success: ${response.errorId}")
            accountInfoViewModel.userName = response.user.name
            accountInfoViewModel.userEmail = response.user.email
            accountInfoViewModel.userPhone = response.user.phone
            accountInfoViewModel.userAddress = response.user.address
            accountInfoBalance.text = resources.getString(R.string.dynamic_money_unit,
                String.format("%,d", WindyConvertUtil.filterNumeric(response.user.account).toInt()))
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Response upload avatar
     *
     * @param response
     */
    private fun getUploadAvatarResponse(response: LoginResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Upload Avatar Success: ${response.errorId}")
            showAlertDialog(getString(R.string.success_title), response.message, "OK")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Response account info
     *
     * @param response
     */
    private fun getChangeProfileRequest(response: LoginResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.d("Change Account Success: ${response.errorId}")
            showAlertDialog(getString(R.string.success_title), response.message, "OK")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Request user info
     *
     */
    private fun loadData() {
        userId?.let { userId ->
            accountInfoViewModel.getUserInfo(userId)
        }
    }

    /**
     * Save any data to local
     *
     * @param key
     * @param value
     */
    private fun saveToLocal(key: Int, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString(getString(key), value)
            apply()
        }
    }

    /**
     * Handle events onClick
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.accountInfoConfirmBtn -> validateInsertFields()
            R.id.accountInfoAvaArea -> addImages()
            R.id.accountInfoPassword -> {
                // Request change password
                val action =
                    AccountInfoFragmentDirections.actionAccountInfoFragmentToRequestSmsCodeFragment(
                        accountInfoViewModel.userPhone
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun initAction() {
    }

    /**
     * Handle account requests
     *
     */
    private fun handleAccountRequest() {
        userId?.let {
            // Request update info
            accountInfoViewModel.sendRequestUpdateInfo(
                it,
                accountInfoEmail.getContent(),
                accountInfoViewModel.userPhone, //:TODO - Add phone number to update account info
                accountInfoName.getContent(),
                accountInfoAddress.getContent()
            )
            // Request upload avatar
            accountInfoViewModel.requestUploadAvt(it)
        }
    }

    /**
     * Validate insert fields
     *
     */
    private fun validateInsertFields() {
        when {
            accountInfoName.getContent().isEmpty() -> accountInfoName.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            accountInfoEmail.getContent().isEmpty() -> accountInfoEmail.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            !Utils.isValidEmailAddress(accountInfoEmail.getContent()) -> accountInfoEmail.apply {
                error = getString(R.string.common_error_email_format)
                requestFocus()
            }
            accountInfoAddress.getContent().isEmpty() -> accountInfoAddress.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            else -> handleAccountRequest()
        }
    }

    /**
     * Get text by edit text
     *
     * @return edit text content
     */
    private fun EditText.getContent(): String {
        return this.text.toString()
    }

    /**
     * Open gallery or camera
     *
     */
    private fun addImages() {
        fragmentManager?.let {
            val dialog = DialogSelectPhotoFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(it, "SelectPhotoDialog")
        }
    }

    /**
     * Handle event select platform
     *
     * @param isCamera
     */
    override fun onSelectPhoto(isCamera: Boolean) {
        if (isCamera) {
            insertImages(Constant.REQUEST_CAMERA)
        } else {
            insertImages(Constant.REQUEST_GALLERY)
        }
    }

    /**
     * Listener permission, Open gallery/ camera
     * immediately after permissions was granted
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * Handle when permission denied
     *
     * @param requestCode
     * @param perms
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Do nothing
    }

    /**
     * Handle when permission granted
     *
     * @param requestCode
     * @param perms
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when(requestCode) {
            Constant.REQUEST_CD_GRANT_PERMISSION -> {
                this.openGallery(false, Constant.REQUEST_CD_PERMISSION)
            }
            Constant.REQUEST_CD_GRANT_PERMISSION_CAMERA -> {
                activity?.let { context -> this.processTakePhoto(context) }
            }
        }
    }

    /**
     * Activity Result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_GALLERY -> {
                val selectedImg = data?.data
                try {
                    val filePathColumn =  arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    if (selectedImg != null) {

                        val cursor = context?.contentResolver?.query(
                            selectedImg,
                            filePathColumn,
                            null,
                            null,
                            null
                        )

                        // Move to first row
                        cursor?.moveToFirst()
                        // Get the column index of MediaStore.Images.Media.DATA
                        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        val imgDecorableString = columnIndex?.let { cursor.getString(it) }
                        // Close cursor
                        cursor?.close()
                        // Add value image path
                        accountInfoViewModel.img = imgDecorableString
                        // Set and visible preview upload image
                        accountInfoAvatar.clipToOutline = true
                        context?.let { Glide.with(it).load(selectedImg).apply(requestOptionsAvatar).into(accountInfoAvatar) }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Constant.REQUEST_CAMERA -> {
                val imageFile = File(currentPhotoPath)
                // Save to gallery
                activity?.let { context ->
                    currentPhotoPath?.let { path ->
                        WindyConvertUtil.saveToGallery(
                            context,
                            path
                        )
                    }
                }
                // Add value image path
                accountInfoViewModel.img = currentPhotoPath
                // Display image
                accountInfoAvatar.clipToOutline = true
                context?.let { Glide.with(it).load(currentPhotoPath).apply(requestOptionsAvatar).into(accountInfoAvatar) }
            }
            else -> DebugLog.e("Not OK")
        }
    }
}