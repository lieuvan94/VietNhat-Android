package vn.hexagon.vietnhat.repository.posts.mypost

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.data.model.translator.Translator
import vn.hexagon.vietnhat.data.remote.NetworkService
import vn.hexagon.vietnhat.data.remote.NetworkState
import javax.inject.Inject

/**
 * Created by VuNBT on 9/16/2019.
 */
class PersonalPostRepository @Inject constructor(private val apiService: NetworkService) {

    // Personal post live paged list
    lateinit var personalPostPagedList: LiveData<PagedList<Translator>>
    // Person post data source factory
    lateinit var personalPostDataSourceFactory: PersonalPostDataSourceFactory

    /**
     * Set up Paged List for personal posts
     *
     * @param compositeDisposable
     * @return
     */
    fun fetchLivePostPagedList(compositeDisposable: CompositeDisposable, userId: String, serviceId: String): LiveData<PagedList<Translator>> {
        personalPostDataSourceFactory = PersonalPostDataSourceFactory(apiService, compositeDisposable, userId, serviceId)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(5)
            .setPageSize(20)
            .build()
        personalPostPagedList = LivePagedListBuilder(personalPostDataSourceFactory, config).build()
        return personalPostPagedList
    }

    /**
     * Get network state status
     *
     * @return network state
     */
    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<PersonalPostDataSource, NetworkState>(
            personalPostDataSourceFactory.getMutableLiveData(), PersonalPostDataSource::networkState)
    }
}