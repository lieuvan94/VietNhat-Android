package vn.hexagon.vietnhat.ui.post

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.data.model.translator.Translator
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.posts.mypost.PersonalPostRepository
import javax.inject.Inject

/**
 * Created by VuNBT on 9/14/2019.
 */
class PostViewModel @Inject constructor(private val repository: PersonalPostRepository): MVVMBaseViewModel() {

    // Post paged list
    fun getPostPagedList(userId: String, serviceId: String): LiveData<PagedList<Translator>> {
        return repository.fetchLivePostPagedList(compositeDisposable, userId, serviceId)
    }

    // Network state
    val postPagedList:LiveData<PagedList<Translator>> by lazy {
        repository.fetchLivePostPagedList(compositeDisposable, "8", "11")
    }

    /**
     * Check list is empty or not
     *
     * @return isEmpty or isNotEmpty
     */
    fun listIsEmpty(userId: String, serviceId: String): Boolean {
        return postPagedList.value?.isEmpty() ?: true
    }
}