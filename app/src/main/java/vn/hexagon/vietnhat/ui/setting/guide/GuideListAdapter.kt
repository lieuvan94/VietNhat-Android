package vn.hexagon.vietnhat.ui.setting.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.data.model.guide.Guide
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.databinding.ItemGuideLayoutBinding

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
 * Create on：2019-09-29
 * =====================================================
 */
class GuideListAdapter(
                        private val onClick: (String) -> Unit): BaseAdapter<Guide>(ListDiffCallback()) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_guide_layout,
                parent,
                false
            )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is ItemGuideLayoutBinding -> {
//                binding.viewmodel = viewModel
                binding.position = position
                binding.guideListTitle.text = getItem(position).title
                binding.guideItemArea.setOnClickListener {
                    onClick(getItem(position).id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        DebugLog.e(super.getItemCount().toString())
        return currentList.size
    }

    companion object {
        class ListDiffCallback : DiffUtil.ItemCallback<Guide>() {
            override fun areItemsTheSame(oldItem: Guide, newItem: Guide): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Guide, newItem: Guide): Boolean {
                return oldItem == newItem
            }
        }
    }
}