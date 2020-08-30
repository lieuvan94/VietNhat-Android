package vn.hexagon.vietnhat.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import vn.hexagon.vietnhat.constant.APIConstant
import vn.hexagon.vietnhat.constant.IConstant
import vn.hexagon.vietnhat.data.remote.NetworkService
import vn.hexagon.vietnhat.data.remote.SocketFactory
import vn.hexagon.vietnhat.data.remote.SocketService
import vn.hexagon.vietnhat.di.BaseURL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by NhamVD on 2019-07-29.
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(@BaseURL baseURL: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkService(retrofit: Retrofit): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

    @Provides
    @Singleton
    @BaseURL
    fun provideBaseURL(constant: IConstant): String = constant.getDevelopmentURL()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideCache(context: Context): Cache {
        val cacheSize = 5 * 1024 * 1024 // 5 MB
        val cacheDir = context.cacheDir
        return Cache(cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(constant: APIConstant, interceptor: HttpLoggingInterceptor, cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(interceptor)
        builder.connectTimeout(constant.TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
        builder.cache(cache)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideSocketService() : SocketService = SocketFactory.socketService()

}