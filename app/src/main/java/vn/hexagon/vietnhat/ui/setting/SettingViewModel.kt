package vn.hexagon.vietnhat.ui.setting

import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
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
class SettingViewModel @Inject constructor(): MVVMBaseViewModel() {
    fun getImageBanner(): String {
        return "https://is2-ssl.mzstatic.com/image/thumb/Features123/v4/aa/81/92/aa81922d-c919-0c35-cab0-95e47bd38c36/source/4320x1080.png"
    }
}