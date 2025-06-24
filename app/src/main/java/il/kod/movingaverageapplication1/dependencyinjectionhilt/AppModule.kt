package il.kod.movingaverageapplication1.dependencyinjectionhilt

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase
import il.kod.movingaverageapplication1.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

import com.bumptech.glide.RequestManager
import dagger.hilt.android.qualifiers.ActivityContext
import il.kod.movingaverageapplication1.GlideApp
import il.kod.movingaverageapplication1.MainActivity
import il.kod.movingaverageapplication1.NotificationService
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceNoToken
import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceWithToken

import il.kod.movingaverageapplication1.ui.AppMenu
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import il.kod.movingaverageapplication1.ui.NotificationHandler


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    //okhttp3 client without token
    @Provides
    @Singleton
    @Named("okHttpClientNoToken")
    fun provideOkHttpClient(): OkHttpClient {
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }
        val client_ = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return client_
    }

    @Provides
    @Singleton
    @Named("okHttpClientWithToken")
    fun provideOkHttpClientWithToken(sharedPreferences: SharedPreferences): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return try {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val token = sharedPreferences.getString("access_token", null)
                    Log.d("AppModule", "Token retrieved: $token")

                    val requestBuilder = chain.request().newBuilder()
                    if (!token.isNullOrEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                    } else {
                        Log.w("AppModule", "No token found in SharedPreferences")
                    }

                    chain.proceed(requestBuilder.build())
                }
                .addInterceptor(logging)
                .build()
        } catch (e: Exception) {
            Log.e("AppModule", "Error building OkHttpClient: ${e.message}")
            // Return a fallback client without token
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }
    }


    //RETROFIT -WITHOUT TOKEN
    @Provides
    @Singleton
    @Named("nonTokenRetrofit")
    fun provideRetrofit(gson: Gson, @Named("okHttpClientNoToken") client_: OkHttpClient): Retrofit {
        Log.d("AppModule", "provideRetrofit called")

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_PUBLIC)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client_)
            .build()
    }


    //RETROFIT -WITH TOKEN
    @Provides
    @Singleton
    @Named("tokenRetrofit")
    fun provideRetrofitWithToken(
        gson: Gson,
        @Named("okHttpClientWithToken") client_: OkHttpClient
    ): Retrofit {
        Log.d("AppModule", "provideRetrofitwithToken called")

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_PRIVATE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client_)
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomServerDataBaseService(@Named("nonTokenRetrofit") retrofit: Retrofit): CustomServerDatabaseServiceNoToken =
        retrofit.create(CustomServerDatabaseServiceNoToken::class.java)

    @Provides
    @Singleton
    fun provideCustomServerDataBaseServiceWithToken(@Named("tokenRetrofit") retrofit: Retrofit): CustomServerDatabaseServiceWithToken =
        retrofit.create(CustomServerDatabaseServiceWithToken::class.java)


    //GSON
    @Provides
    fun provideGson(): Gson {
        Log.d("AppModule", "provideGson called")
        return GsonBuilder().create()
    }


    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext appContext: Context): StocksDatabase =
        StocksDatabase.getDatabase(appContext)

    @Provides
    @Singleton
    fun provideStocksDao(database: StocksDatabase) = database.stocksDao()


    @Provides
    @Singleton
    fun provideGlideInstance(@ApplicationContext context: Context): RequestManager {
        Log.d("AppModule", "provideGlideInstance called")
        return GlideApp.with(context)
    }


    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_auth",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    @Provides
    @Singleton
    fun providesSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(provideEncryptedPrefs(context), provideLocalStocksRepository(context))
    }

    @Provides
    @Singleton
    fun providesAppMenu(@ApplicationContext context: Context): AppMenu {
        return AppMenu(providesSessionManager(context))
    }

    @Provides
    @Singleton
    fun provideLocalStocksRepository(
        @ApplicationContext context: Context
    ): LocalStocksRepository {
        return LocalStocksRepository(context as Application)
    }

    @Provides
    @Singleton
    fun provideLocalFollowSetRepository(
        @ApplicationContext context: Context
    ): LocalFollowSetRepository {
        return LocalFollowSetRepository(context as Application)
    }


    /*@Provides
    fun provideApplication(@ApplicationContext context: Context): Application
    {
   return context as Application
    }*/

    /* @Provides
fun provideApplicationContext(@ApplicationContext context: Context): Context {
   return context
}
*/

    @Provides
    @Singleton
    fun providesNotificationHandler(@ApplicationContext context: Context): NotificationHandler {
        return NotificationHandler(context)
    }


    @Provides
    @Singleton
    fun providesNotificationService(): NotificationService {
        return NotificationService()
    }


    @Provides
    @Singleton
    fun provideSyncManagementRepository(
        csdRepository: CustomServerDatabaseRepository,
        localStockRepository: LocalStocksRepository,
        sessionManager: SessionManager,
        localFollowSetRepository: LocalFollowSetRepository
    ): SyncManagementRepository {
        return SyncManagementRepository(
            csdRepository,
            localStockRepository,
            sessionManager,
            localFollowSetRepository
        )
    }

}
