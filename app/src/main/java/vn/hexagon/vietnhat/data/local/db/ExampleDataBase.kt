package vn.hexagon.vietnhat.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.hexagon.vietnhat.data.local.db.dao.ExampleDao
import vn.hexagon.vietnhat.data.model.ExampleUser

/**
 * Created by NhamVD on 2019-08-03.
 */
@Database(
    entities = [ExampleUser::class],
    version = DataBaseConstant.DATABASE_VERSION,
    exportSchema = false
)
abstract class ExampleDataBase : RoomDatabase() {
    abstract fun exampleUserDao(): ExampleDao
}