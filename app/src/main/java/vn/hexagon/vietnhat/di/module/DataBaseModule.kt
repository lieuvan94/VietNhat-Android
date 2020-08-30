package vn.hexagon.vietnhat.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import vn.hexagon.vietnhat.constant.Constant.PREFS_NAME
import vn.hexagon.vietnhat.data.local.db.DataBaseConstant
import vn.hexagon.vietnhat.data.local.db.ExampleDataBase
import vn.hexagon.vietnhat.data.local.db.dao.ExampleDao
import vn.hexagon.vietnhat.di.ApplicationContext
import javax.inject.Singleton

/**
 * Created by NhamVD on 2019-07-31.
 */
@Module
class DataBaseModule {

    @Provides
    @Singleton
    fun provideDataBase(context: Application): ExampleDataBase {
        return Room
            .databaseBuilder(context, ExampleDataBase::class.java, DataBaseConstant.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideExampleUserDao(db: ExampleDataBase): ExampleDao {
        return db.exampleUserDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreference(context:Application): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}