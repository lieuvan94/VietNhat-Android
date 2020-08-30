package vn.hexagon.vietnhat.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.hexagon.vietnhat.ui.example.ExampleActivity

/**
 * Created by NhamVD on 2019-07-28.
 */

@Suppress("unused")
@Module
abstract class ExampleActivityBuilderModule {

    @ContributesAndroidInjector(modules = [ExampleFragmentBuilderModule::class])
    abstract fun contributeExampleActivity(): ExampleActivity
}