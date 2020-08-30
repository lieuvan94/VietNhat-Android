package vn.hexagon.vietnhat.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.hexagon.vietnhat.ui.MainActivity

/**
 * Created by NhamVD on 2019-08-17.
 */
@Suppress("unused")
@Module
abstract class MainActivityBuilderModule {

  @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
  abstract fun contributeMainActivity(): MainActivity
}