package il.kod.movingaverageapplication1.dependencyinjectionhilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import il.kod.movingaverageapplication1.MainActivity
import il.kod.movingaverageapplication1.ui.NotificationHandler

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

}