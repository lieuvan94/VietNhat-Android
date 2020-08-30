package vn.hexagon.vietnhat.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import vn.hexagon.vietnhat.BaseApp
import vn.hexagon.vietnhat.di.module.AppModule
import vn.hexagon.vietnhat.di.module.ExampleActivityBuilderModule
import vn.hexagon.vietnhat.di.module.MainActivityBuilderModule
import javax.inject.Singleton

/**
 * Created by NhamVD on 2019-07-28.
 */
@Singleton
@Component(
  modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    MainActivityBuilderModule::class,
    ExampleActivityBuilderModule::class
  ]
)
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
  }

  fun inject(app: BaseApp)
}