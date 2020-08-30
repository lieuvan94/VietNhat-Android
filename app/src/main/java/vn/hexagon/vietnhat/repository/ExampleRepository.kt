package vn.hexagon.vietnhat.repository

import io.reactivex.Single
import vn.hexagon.vietnhat.data.local.db.dao.ExampleDao
import vn.hexagon.vietnhat.data.model.ExampleUser
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-08-02.
 */
class ExampleRepository @Inject constructor(
    private val networkService: NetworkService,
    private val userDao: ExampleDao
) {
    fun getUserFromRemote(id: String): Single<ExampleUser> {
        return networkService.getUser(id).doOnSuccess {
            userDao.save(it)
        }
    }

    fun getUserFromDB(): Single<List<ExampleUser>> {
        return userDao.load()
    }
}