package il.kod.movingaverageapplication1.dependencyinjectionhilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule
{



}