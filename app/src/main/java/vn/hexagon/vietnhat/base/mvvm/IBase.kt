package vn.hexagon.vietnhat.base.mvvm

import androidx.annotation.LayoutRes

/**
 * Created by NhamVD on 2019-07-07.
 */
interface IBase {

    @LayoutRes
    fun getLayoutId(): Int

    fun initView()

    fun initAction()

    fun hideKeyBoard()

    fun showLoading(isShow: Boolean)

    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        buttonName: String = "OK",
        cb: (() -> Unit)? = null
    )

    fun showAskingDialog(
        title: String? = null,
        message: String? = null,
        positionName: String = "Yes",
        negativeName: String = "No",
        cbPositive: (() -> Unit)? = null,
        cbNegative: (() -> Unit)? = null
    )
}