package vn.hexagon.vietnhat.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseAdapter
import vn.hexagon.vietnhat.data.model.comment.Comment
import vn.hexagon.vietnhat.databinding.LayoutCommentRowBinding
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel

/*
 * Create by VuNBT on 2019-12-23 
 */
class CommentListAdapter(private val viewModel: PostDetailViewModel): BaseAdapter<Comment>(ListDiffCallback()) {

    // List comments
    private val comments = ArrayList<Comment>()

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
            R.layout.layout_comment_row,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(binding) {
            is LayoutCommentRowBinding -> {
                binding.position = position
                binding.vm = viewModel
                binding.cmtUserAvatar.clipToOutline = true
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    /**
     * Add list comments
     *
     * @param list
     */
    fun insertList(list: ArrayList<Comment>) {
        comments.clear()
        comments.addAll(list)
        notifyDataSetChanged()
    }
}