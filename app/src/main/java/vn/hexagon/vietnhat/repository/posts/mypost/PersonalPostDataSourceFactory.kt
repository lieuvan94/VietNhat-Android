package vn.hexagon.vietnhat.repository.posts.mypost

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.data.model.translator.Translator
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by VuNBT on 9/16/2019.
 */
class PersonalPostDataSourceFactory @Inject constructor(private val apiNetworkService: NetworkService,
                                                        private val compositeDisposable: CompositeDisposable,
                                                        private val userId:String,
                                                        private val serviceId:String): DataSource.Factory<Int, Translator>() {
    // Live Data Source
    val personalPostLiveDataSource = MutableLiveData<PersonalPostDataSource>()

    /**
     * Create factory data source
     *
     * @return Personal post data source
     */
    override fun create(): DataSource<Int, Translator> {
        val personalPostDataSource = PersonalPostDataSource(apiNetworkService, compositeDisposable, userId, serviceId)
        personalPostLiveDataSource.postValue(personalPostDataSource)
        return personalPostDataSource
    }

    /**
     * Get personal post mutable data
     *
     * @return personalPostLiveDataSource
     */
    fun getMutableLiveData(): MutableLiveData<PersonalPostDataSource> {
        return personalPostLiveDataSource
    }
}