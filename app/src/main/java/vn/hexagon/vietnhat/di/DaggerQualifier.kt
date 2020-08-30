package vn.hexagon.vietnhat.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseURL

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RxJavaCallAdapter

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LiveDataCallAdapter

