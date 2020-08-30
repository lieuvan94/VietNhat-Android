package vn.hexagon.vietnhat.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.hexagon.vietnhat.ui.example.ExampleFragment

/**
 * Created by NhamVD on 2019-07-28.
 */
@Suppress("unused")
@Module
abstract class ExampleFragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeExampleFragment(): ExampleFragment
}