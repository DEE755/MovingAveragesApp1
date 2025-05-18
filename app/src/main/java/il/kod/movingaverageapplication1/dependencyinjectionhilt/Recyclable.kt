package il.kod.movingaverageapplication1.dependencyinjectionhilt

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface Recyclable {

    /*@Module
    @InstallIn(SingletonComponent::class)//lives as long as the app is running
    object  GsonModule {
        @Provides
        fun provideGson(): Gson {
            return Gson()
        }
    }

    fun recycle()*/


}