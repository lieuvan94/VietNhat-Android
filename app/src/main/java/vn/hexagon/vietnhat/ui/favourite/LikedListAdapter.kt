package vn.hexagon.vietnhat.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.data.model.comment.Comment
import vn.hexagon.vietnhat.databinding.LayoutCommentRowBinding
import vn.hexagon.vietnhat.databinding.LayoutLikedMeberRowBinding
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel

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
 * Create on：2019-12-29
 * =====================================================
 */
class LikedListAdapter(private val viewModel: PostDetailViewModel,
                       private val onMsgBtn: (String, String, String, String) -> Unit,
                       private val onCallBtn:(String) -> Unit) : BaseAdapter<Comment>(ListDiffCallback()) {

    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_liked_meber_row,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is LayoutLikedMeberRowBinding -> {
                binding.position = position
                binding.viewmodel = viewModel
                binding.likedMemberAvt.clipToOutline = true
                binding.likedCallBtn.setOnClickListener {
                    onCallBtn(currentList[position].userPhone)
                }
                binding.likedMsgBtn.setOnClickListener {
                    onMsgBtn(
                        currentList[position].userId,
                        currentList[position].userNm,
                        currentList[position].userAvatar,
                        currentList[position].userPhone
                    )
                }
            }
        }
    }
}