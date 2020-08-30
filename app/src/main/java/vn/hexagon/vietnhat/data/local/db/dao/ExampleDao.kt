package vn.hexagon.vietnhat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.ExampleUser

/**
 * Created by NhamVD on 2019-08-03.
 */

@Dao
interface ExampleDao {

    @Insert(onConflict = REPLACE)
    fun save(user: ExampleUser)

    @Query("SELECT * FROM exampleuser")
    fun load(): Single<List<ExampleUser>>

}