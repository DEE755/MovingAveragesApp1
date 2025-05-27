package il.kod.movingaverageapplication1.dependencyinjectionhilt

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
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseService
import il.kod.movingaverageapplication1.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import il.kod.movingaverageapplication1.GlideApp
import java.util.concurrent.TimeUnit



@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
    Log.d("AppModule", "provideRetrofit called")
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }
        val client_= OkHttpClient.Builder()
            .addInterceptor(logging).connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client_)
            .build()
    }





    @Provides
    fun provideGson(): Gson {
        Log.d("AppModule", "provideGson called")
        return GsonBuilder().create()
    }


    @Provides
    fun provideCustomServerDataBaseService(retrofit: Retrofit) : CustomServerDatabaseService =
        retrofit.create(CustomServerDatabaseService::class.java)



    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext appContext: Context): StocksDatabase
            = StocksDatabase.getDatabase(appContext)

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



}
