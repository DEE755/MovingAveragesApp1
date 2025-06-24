package il.kod.movingaverageapplication1.dependencyinjectionhilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MovingAverageApplication1 : Application() {
    companion object {
        lateinit var instance: MovingAverageApplication1
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}