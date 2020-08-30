package vn.hexagon.vietnhat.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import vn.hexagon.vietnhat.constant.APIConstant
import vn.hexagon.vietnhat.constant.GlobalConstant
import vn.hexagon.vietnhat.constant.IConstant
import javax.inject.Singleton

/**
 * Created by NhamVD on 2019-07-28.
 */

@Module(
    includes = [ViewModelModule::class,
        NetworkModule::class,
        DataBaseModule::class]
)
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    /*@Provides
    @Singleton
    @ActivityContext
    fun provideActivityContext(activity: Activity): Context { return activity }*/

    @Provides
    @Singleton
    fun provideApplicationConstant(): IConstant = GlobalConstant()

    @Provides
    @Singleton
    fun provideAPIConstant(): APIConstant = APIConstant
}