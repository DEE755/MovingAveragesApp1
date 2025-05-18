package il.kod.movingaverageapplication1.dependencyinjectionhilt

import android.content.Context
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
            .addInterceptor(logging)
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

}