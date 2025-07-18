package com.aeci.mmucompanion.core.di

import com.aeci.mmucompanion.data.remote.api.AECIApiService
import com.aeci.mmucompanion.data.remote.api.MobileServerApiService
import com.aeci.mmucompanion.core.util.NetworkManager
import com.aeci.mmucompanion.core.util.MobileServerConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // Legacy AECI server URL (keep for backward compatibility)
    private const val AECI_SERVER_URL = "https://your-aeci-server.com/api/"
    
    // Mobile server will be dynamically configured
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("mobile_server_client")
    fun provideMobileServerOkHttpClient(mobileServerConfig: MobileServerConfig): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            
            // Add authentication token if available
            // This would be retrieved from secure storage
            // requestBuilder.addHeader("Authorization", "Bearer $token")
            
            chain.proceed(requestBuilder.build())
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("aeci_retrofit")
    fun provideAECIRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AECI_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("mobile_server_retrofit")
    fun provideMobileServerRetrofit(
        @Named("mobile_server_client") okHttpClient: OkHttpClient,
        mobileServerConfig: MobileServerConfig
    ): Retrofit {
        // Get the active server URL (with auto-discovery)
        val baseUrl = runBlocking {
            val activeUrl = mobileServerConfig.getActiveServerUrl()
            if (activeUrl.isNotEmpty()) {
                if (activeUrl.endsWith("/")) activeUrl else "$activeUrl/"
            } else {
                "http://localhost:3000/" // Fallback
            }
        }
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAECIApiService(@Named("aeci_retrofit") retrofit: Retrofit): AECIApiService {
        return retrofit.create(AECIApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideMobileServerApiService(@Named("mobile_server_retrofit") retrofit: Retrofit): MobileServerApiService {
        return retrofit.create(MobileServerApiService::class.java)
    }
}
